package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer
import com.refinedmods.refinedstorage.api.autocrafting.task.ICraftingTask
import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener
import com.refinedmods.refinedstorage.item.UpgradeItem
import com.refinedmods.refinedstorage.util.StackUtils
import com.refinedmods.refinedstorage.util.WorldUtils
import dev.stevendoesstuffs.refinedcrafterproxy.Config
import dev.stevendoesstuffs.refinedcrafterproxy.RefinedCrafterProxy.MODID
import dev.stevendoesstuffs.refinedcrafterproxy.Registration.CRAFTER_PROXY_CARD
import dev.stevendoesstuffs.refinedcrafterproxy.Registration.CRAFTER_PROXY_ID
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxycard.CrafterProxyCardItem
import java.util.*
import kotlin.math.roundToInt
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.CompoundNBT
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.Direction
import net.minecraft.util.INameable
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextComponentUtils
import net.minecraft.util.text.TranslationTextComponent
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.minecraftforge.items.wrapper.CombinedInvWrapper

class CrafterProxyNetworkNode(world: World, pos: BlockPos?) :
        NetworkNode(world, pos), ICraftingPatternContainer {

    enum class CrafterMode {
        IGNORE,
        SIGNAL_UNLOCKS_AUTOCRAFTING,
        SIGNAL_LOCKS_AUTOCRAFTING,
        PULSE_INSERTS_NEXT_SET;

        companion object {
            fun getById(id: Int): CrafterMode {
                return if (id >= 0 && id < values().size) {
                    values()[id]
                } else IGNORE
            }
        }
    }

    companion object {
        const val NBT_TIER = "Tier"
        private const val NBT_DISPLAY_NAME = "DisplayName"
        private const val NBT_UUID = "CrafterUuid"
        private const val NBT_MODE = "Mode"
        private const val NBT_LOCKED = "Locked"
        private const val NBT_WAS_POWERED = "WasPowered"
        private val DEFAULT_NAME: ITextComponent =
                TranslationTextComponent("block.$MODID.$CRAFTER_PROXY_ID")
        private val ID = ResourceLocation(MODID, CRAFTER_PROXY_ID)
        private const val CARD_UPDATE_CLEAR_DELAY = 1
        private const val CARD_UPDATE_SET_DELAY = 4
    }

    inner class CardItemHandler : BaseItemHandler(1) {
        override fun getSlotLimit(slot: Int): Int {
            return 1
        }

        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            return if (!stacks[slot].isEmpty) {
                stack
            } else {
                super.insertItem(slot, stack, simulate)
            }
        }

        fun overrideItem(stack: ItemStack): Boolean {
            val changed = this.stacks[0] == stack
            this.stacks[0] = stack
            return changed
        }
    }

    val cardInventory =
            CardItemHandler()
                    .addValidator { stack -> stack.item == CRAFTER_PROXY_CARD }
                    .addListener(NetworkNodeInventoryListener(this))
                    .addListener { _: BaseItemHandler?, _: Int, reading: Boolean ->
                        if (!reading) {
                            cardUpdateTick = Int.MIN_VALUE
                            cardUpdateStack = ItemStack(Items.AIR)
                            if (network != null) network!!.craftingManager.invalidate()
                        }
                    } as
                    CardItemHandler

    private val upgrades =
            UpgradeItemHandler(4, UpgradeItem.Type.SPEED)
                    .addListener(NetworkNodeInventoryListener(this)) as
                    UpgradeItemHandler

    var tier: String? = null

    private var cardUpdateTick = Int.MIN_VALUE
    private var cardUpdateStack = ItemStack(Items.AIR)
    private var numPatterns = 0

    private var visited = false
    private var displayName: ITextComponent? = null
    private var uuid: UUID? = null

    private var mode = CrafterMode.IGNORE
    private var locked = false
    private var wasPowered = false

    override fun getEnergyUsage(): Int {
        return Config.CONFIG.getCrafterEnergyUsage(tier) +
                Config.CONFIG.getPatternsEnergyUsage(tier) * numPatterns +
                (Config.CONFIG.getUpgradesEnergyMultiplier(tier) * upgrades.energyUsage)
                        .roundToInt()
    }

    override fun update() {
        super.update()
        if (mode == CrafterMode.PULSE_INSERTS_NEXT_SET && world.isLoaded(pos)) {
            if (world.hasNeighborSignal(pos)) {
                wasPowered = true
                markDirty()
            } else if (wasPowered) {
                wasPowered = false
                locked = false
                markDirty()
            }
        }

        if (ticks == cardUpdateTick + CARD_UPDATE_CLEAR_DELAY) {
            val card = cardUpdateStack.copy()
            card.removeTagKey(CrafterProxyCardItem.STATUS)
            if (cardInventory.overrideItem(card)) markDirty()
        } else if (ticks == cardUpdateTick + CARD_UPDATE_CLEAR_DELAY + CARD_UPDATE_SET_DELAY) {
            if (cardInventory.overrideItem(cardUpdateStack)) markDirty()
        }
    }

    override fun onConnectedStateChange(
            network: INetwork,
            state: Boolean,
            cause: ConnectivityStateChangeCause?
    ) {
        super.onConnectedStateChange(network, state, cause)
        network.craftingManager.invalidate()
    }

    override fun onDisconnected(network: INetwork) {
        super.onDisconnected(network)
        network.craftingManager
                .tasks
                .stream()
                .filter { task: ICraftingTask -> task.pattern.container.position == pos }
                .forEach { task: ICraftingTask -> network.craftingManager.cancel(task.id) }
    }

    override fun onDirectionChanged(direction: Direction?) {
        super.onDirectionChanged(direction)
        if (network != null) {
            network!!.craftingManager.invalidate()
        }
    }

    override fun read(tag: CompoundNBT) {
        super.read(tag)
        StackUtils.readItems(cardInventory, 0, tag)
        StackUtils.readItems(upgrades, 1, tag)
        if (tag.contains(NBT_DISPLAY_NAME)) {
            displayName = ITextComponent.Serializer.fromJson(tag.getString(NBT_DISPLAY_NAME))
        }
        if (tag.hasUUID(NBT_UUID)) {
            uuid = tag.getUUID(NBT_UUID)
        }
        if (tag.contains(NBT_MODE)) {
            mode = CrafterMode.getById(tag.getInt(NBT_MODE))
        }
        if (tag.contains(NBT_LOCKED)) {
            locked = tag.getBoolean(NBT_LOCKED)
        }
        if (tag.contains(NBT_WAS_POWERED)) {
            wasPowered = tag.getBoolean(NBT_WAS_POWERED)
        }
        if (tag.contains(NBT_TIER)) {
            tier = tag.getString(NBT_TIER)
        }
    }

    override fun getId(): ResourceLocation {
        return ID
    }

    override fun write(tag: CompoundNBT): CompoundNBT {
        super.write(tag)
        StackUtils.writeItems(cardInventory, 0, tag)
        StackUtils.writeItems(upgrades, 1, tag)
        displayName?.let { tag.putString(NBT_DISPLAY_NAME, ITextComponent.Serializer.toJson(it)) }
        uuid?.let { tag.putUUID(NBT_UUID, it) }
        tag.putInt(NBT_MODE, mode.ordinal)
        tag.putBoolean(NBT_LOCKED, locked)
        tag.putBoolean(NBT_WAS_POWERED, wasPowered)
        tier?.let { tag.putString(NBT_TIER, it) }
        return tag
    }

    override fun getUpdateInterval(): Int {
        return Config.CONFIG.getUpdateInterval(
                tier,
                upgrades.getUpgradeCount(UpgradeItem.Type.SPEED)
        )
    }

    override fun getMaximumSuccessfulCraftingUpdates(): Int {
        return Config.CONFIG.getMaximumSuccessfulCraftingUpdates(
                tier,
                upgrades.getUpgradeCount(UpgradeItem.Type.SPEED)
        )
    }

    override fun getConnectedInventory(): IItemHandler? {
        val proxy = rootContainer ?: return null
        return WorldUtils.getItemHandler(proxy.facingTile, proxy.direction.opposite)
    }

    override fun getConnectedFluidInventory(): IFluidHandler? {
        val proxy = rootContainer ?: return null
        return WorldUtils.getFluidHandler(proxy.facingTile, proxy.direction.opposite)
    }

    override fun getConnectedTile(): TileEntity? {
        val proxy = rootContainer ?: return null
        return proxy.facingTile
    }

    override fun getFacingTile(): TileEntity? {
        val facingPos = pos.relative(direction)
        return if (!world.isLoaded(facingPos)) {
            null
        } else world.getBlockEntity(facingPos)
    }

    override fun getPatterns(): List<ICraftingPattern> {
        val card = cardInventory.getStackInSlot(0)
        if (card.item != CRAFTER_PROXY_CARD) {
            numPatterns = 0
            return emptyList()
        }

        val node = CRAFTER_PROXY_CARD.getNode(world.server!!, card)
        var res: List<ICraftingPattern> = emptyList()
        val msg =
                if (node == null || node.network != network) {
                    "disconnected"
                } else if (node !is ICraftingPatternContainer || node is CrafterProxyNetworkNode) {
                    "invalid_crafter"
                } else {
                    res = node.patterns
                    "connected"
                }

        val newCard =
                if ((card.tag?.getString(CrafterProxyCardItem.STATUS) ?: "") != msg) {
                    val cardCopy = card.copy()
                    cardCopy.orCreateTag.putString(CrafterProxyCardItem.STATUS, msg)
                    cardCopy
                } else card

        cardUpdateTick = ticks
        cardUpdateStack = newCard
        numPatterns = res.size

        return res
    }

    override fun getPatternInventory(): IItemHandlerModifiable? {
        // crafting manager does not play well with non-pattern items I think
        return null
    }

    override fun getName(): ITextComponent {
        fun getNameInternal(): ITextComponent {
            displayName?.let {
                return it
            }

            val facing = connectedTile
            if (facing is INameable) {
                return (facing as INameable).name
            }

            return if (facing != null) {
                TranslationTextComponent(world.getBlockState(facing.blockPos).block.descriptionId)
            } else DEFAULT_NAME
        }

        val name = getNameInternal()
        Config.CONFIG.getDisplayName(tier)?.let {
            return StringTextComponent("")
                    .append(name)
                    .append(StringTextComponent(" "))
                    .append(TextComponentUtils.wrapInSquareBrackets(it))
        }
        return name
    }

    fun setDisplayName(displayName: ITextComponent?) {
        this.displayName = displayName
    }

    fun getDisplayName(): ITextComponent? {
        return displayName
    }

    override fun getPosition(): BlockPos? {
        return pos
    }

    fun getMode(): CrafterMode {
        return mode
    }

    fun setMode(mode: CrafterMode) {
        this.mode = mode
        wasPowered = false
        locked = false
        markDirty()
    }

    fun getUpgrades(): IItemHandler {
        return upgrades
    }

    override fun getDrops(): IItemHandler {
        return CombinedInvWrapper(cardInventory, upgrades)
    }

    override fun getRootContainer(): ICraftingPatternContainer? {
        if (visited) {
            return null
        }
        val facing =
                API.instance()
                        .getNetworkNodeManager(world as ServerWorld)
                        .getNode(pos.relative(direction))
        if (facing !is ICraftingPatternContainer || facing.network !== network) {
            return this
        }
        visited = true
        val facingContainer = (facing as ICraftingPatternContainer).rootContainer
        visited = false
        return facingContainer
    }

    fun getRootContainerNotSelf(): Optional<ICraftingPatternContainer> {
        val root = rootContainer
        return if (root != null && root !== this) {
            Optional.of(root)
        } else Optional.empty()
    }

    override fun getUuid(): UUID? {
        if (uuid == null) {
            uuid = UUID.randomUUID()
            markDirty()
        }
        return uuid
    }

    override fun isLocked(): Boolean {
        val root = getRootContainerNotSelf()
        return if (root.isPresent) {
            root.get().isLocked
        } else
                when (mode) {
                    CrafterMode.IGNORE -> false
                    CrafterMode.SIGNAL_LOCKS_AUTOCRAFTING -> world.hasNeighborSignal(pos)
                    CrafterMode.SIGNAL_UNLOCKS_AUTOCRAFTING -> !world.hasNeighborSignal(pos)
                    CrafterMode.PULSE_INSERTS_NEXT_SET -> locked
                }
    }

    override fun unlock() {
        locked = false
    }

    override fun onUsedForProcessing() {
        val root = getRootContainerNotSelf()
        if (root.isPresent) {
            root.get().onUsedForProcessing()
            return
        }
        if (mode == CrafterMode.PULSE_INSERTS_NEXT_SET) {
            locked = true
            markDirty()
        }
    }

    override fun getItemStack(): ItemStack {
        val itemstack = super.getItemStack()
        Config.CONFIG.getDisplayName(tier)?.let {
            itemstack.hoverName =
                    StringTextComponent("")
                            .append(DEFAULT_NAME)
                            .append(StringTextComponent(" "))
                            .append(TextComponentUtils.wrapInSquareBrackets(it))
        }
        return itemstack
    }
}
