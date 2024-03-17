package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxycard

import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.apiimpl.API
import dev.stevendoesstuffs.refinedcrafterproxy.RefinedCrafterProxy.MODID
import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import dev.stevendoesstuffs.refinedcrafterproxy.Registration.CRAFTER_PROXY_CARD_ID
import dev.stevendoesstuffs.refinedcrafterproxy.Registration.CRAFTER_PROXY_TAB
import java.util.Locale
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level

class CrafterProxyCardItem : Item(Properties().stacksTo(1).tab(CRAFTER_PROXY_TAB)) {
    companion object {
        const val BOUND_DIM = "BoundDim"
        const val BOUND_X = "BoundX"
        const val BOUND_Y = "BoundY"
        const val BOUND_Z = "BoundZ"
        const val STATUS = "Status"
    }

    fun getPos(stack: ItemStack): Pair<ResourceKey<Level>, BlockPos>? {
        val tag = stack.tag
        if (stack.item != Registration.CRAFTER_PROXY_CARD ||
                        tag == null ||
                        !tag.contains(BOUND_DIM) ||
                        !tag.contains(BOUND_X) ||
                        !tag.contains(BOUND_Y) ||
                        !tag.contains(BOUND_Z)
        )
                return null

        val dimName = tag.getString(BOUND_DIM)
        val x = tag.getInt(BOUND_X)
        val y = tag.getInt(BOUND_Y)
        val z = tag.getInt(BOUND_Z)

        val pos = BlockPos(x, y, z)
        val dimKey =
                ResourceKey.create(Registry.DIMENSION_REGISTRY, ResourceLocation(dimName))
                        ?: return null

        return Pair(dimKey, pos)
    }

    fun setPos(stack: ItemStack, level: Level, pos: BlockPos): Boolean {
        if (stack.item != Registration.CRAFTER_PROXY_CARD) return false
        val compound = stack.orCreateTag

        val dimStr = level.dimension().location().toString()
        compound.putString(BOUND_DIM, dimStr)
        compound.putInt(BOUND_X, pos.x)
        compound.putInt(BOUND_Y, pos.y)
        compound.putInt(BOUND_Z, pos.z)

        return true
    }

    fun getNode(server: MinecraftServer, stack: ItemStack): INetworkNode? {
        val (dimKey, pos) = getPos(stack) ?: return null

        val dim = server.getLevel(dimKey) ?: return null
        val node = API.instance().getNetworkNodeManager(dim).getNode(pos)
        if (node == null || !node.isActive || node.network == null) return null
        return node
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player
        if (context.level.isClientSide || player == null) return InteractionResult.SUCCESS
        val stack = context.itemInHand

        if (!setPos(stack, context.level, context.clickedPos)) {
            return InteractionResult.CONSUME
        }

        player.displayClientMessage(
                formatTranslate(
                        "$MODID.$CRAFTER_PROXY_CARD_ID.select",
                        color = ChatFormatting.BLUE
                ),
                true
        )

        return InteractionResult.SUCCESS
    }

    override fun use(
            level: Level,
            player: Player,
            hand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        if (player.level.isClientSide || !player.isCrouching) return super.use(level, player, hand)
        val stack = player.getItemInHand(hand)
        stack.tag = null
        player.displayClientMessage(
                formatTranslate("$MODID.$CRAFTER_PROXY_CARD_ID.clear", color = ChatFormatting.GOLD),
                true
        )
        return InteractionResultHolder(InteractionResult.CONSUME, stack)
    }

    override fun appendHoverText(
            stack: ItemStack,
            level: Level?,
            information: MutableList<Component>,
            flag: TooltipFlag
    ) {
        val tag = stack.tag ?: return
        val dim = formatDimName(tag.getString(BOUND_DIM), color = ChatFormatting.BLUE)
        val x = formatInt(tag.getInt(BOUND_X), color = ChatFormatting.GOLD)
        val y = formatInt(tag.getInt(BOUND_Y), color = ChatFormatting.GOLD)
        val z = formatInt(tag.getInt(BOUND_Z), color = ChatFormatting.GOLD)

        information.add(formatTranslate("$MODID.$CRAFTER_PROXY_CARD_ID.tooltip", x, y, z, dim))

        val status = tag.getString(STATUS)
        if (status == "") return
        val color = if (status == "connected") ChatFormatting.GREEN else ChatFormatting.RED
        information.add(
                formatTranslate("$MODID.$CRAFTER_PROXY_CARD_ID.status.prefix")
                        .append(
                                formatTranslate(
                                        "$MODID.$CRAFTER_PROXY_CARD_ID.status.$status",
                                        color = color
                                )
                        )
        )
    }
}

fun formatTranslate(
        key: String,
        vararg args: Any,
        color: ChatFormatting? = null
): MutableComponent {
    var text: MutableComponent = TranslatableComponent(key, *args)
    if (color != null) text = text.withStyle(color)
    return text
}

fun formatInt(i: Int, color: ChatFormatting? = null): MutableComponent {
    return if (color != null) TextComponent(i.toString()).withStyle(color)
    else TextComponent(i.toString())
}

fun formatDimName(dimension: String, color: ChatFormatting? = null): MutableComponent {
    val split = dimension.indexOf(':')
    var dimensionName = if (split >= 0) dimension.substring(split + 1) else dimension

    if (dimensionName.isEmpty()) return TextComponent("!").withStyle(ChatFormatting.RED)

    dimensionName =
            dimensionName
                    .substring(
                            (dimensionName.indexOf('/') + 1).coerceIn(
                                    0,
                                    (dimensionName.length - 1).coerceAtLeast(0)
                            )
                    )
                    .lowercase(Locale.getDefault())

    dimensionName =
            dimensionName.substring(0, 1).uppercase(Locale.getDefault()) +
                    dimensionName.substring(1)

    for (i in 0 until dimensionName.length - 1) {
        if (dimensionName[i] == '_' && Character.isAlphabetic(dimensionName[i + 1].code)) {
            val tmp =
                    if (i + 2 < dimensionName.length)
                            dimensionName.substring(i + 1, i + 2).uppercase(Locale.getDefault()) +
                                    dimensionName.substring(i + 2)
                    else dimensionName.substring(i + 1).uppercase(Locale.getDefault())
            dimensionName = "${dimensionName.substring(0, i)} ${tmp.also { dimensionName = it }}"
        }
    }

    return if (color != null) TextComponent(dimensionName).withStyle(color)
    else TextComponent(dimensionName)
}
