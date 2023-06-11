package dev.stevendoesstuffs.refinedcrafterproxy

import com.refinedmods.refinedstorage.render.BakedModelOverrideRegistry
import dev.stevendoesstuffs.refinedcrafterproxy.RefinedCrafterProxy.MODID
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.*
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxycard.CrafterProxyCardItem
import net.minecraft.inventory.container.ContainerType
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootFunctionType
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.ResourceLocation
import net.minecraft.util.registry.Registry
import net.minecraftforge.common.extensions.IForgeContainerType
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.KDeferredRegister
import thedarkcolour.kotlinforforge.forge.MOD_BUS


object Registration {
    val ITEMS_REGISTRY = KDeferredRegister(ForgeRegistries.ITEMS, MODID)
    val BLOCKS_REGISTRY = KDeferredRegister(ForgeRegistries.BLOCKS, MODID)
    val BLOCK_ENTITIES_REGISTRY = KDeferredRegister(ForgeRegistries.TILE_ENTITIES, MODID)
    val CONTAINERS_REGISTRY = KDeferredRegister(ForgeRegistries.CONTAINERS, MODID)
    val bakedModelOverrideRegistry = BakedModelOverrideRegistry()

    const val CRAFTER_PROXY_ID = "crafter_proxy"
    const val CRAFTER_PROXY_CARD_ID = "crafter_proxy_card"

    val CRAFTER_PROXY_CARD by ITEMS_REGISTRY.registerObject(CRAFTER_PROXY_CARD_ID) {
        CrafterProxyCardItem()
    }

    val CRAFTER_PROXY_BLOCK by BLOCKS_REGISTRY.registerObject(CRAFTER_PROXY_ID) {
        CrafterProxyBlock()
    }

    val CRAFTER_PROXY_BLOCK_ITEM by ITEMS_REGISTRY.registerObject(CRAFTER_PROXY_ID) {
        CrafterProxyBlockItem()
    }

    val CRAFTER_PROXY_BLOCK_ENTITY: TileEntityType<CrafterProxyBlockEntity> by BLOCK_ENTITIES_REGISTRY.registerObject(
        CRAFTER_PROXY_ID
    ) {
        TileEntityType.Builder.of({ CrafterProxyBlockEntity() }, CRAFTER_PROXY_BLOCK).build(null)
    }

    val CRAFTER_PROXY_CONTAINER: ContainerType<CrafterProxyContainer?> by CONTAINERS_REGISTRY.registerObject(
        CRAFTER_PROXY_ID
    ) {
        IForgeContainerType.create { windowId, inv, data ->
            val pos = data.readBlockPos()
            val te = inv.player.commandSenderWorld.getBlockEntity(pos)
            if (te !is CrafterProxyBlockEntity) {
                return@create null
            }
            return@create CrafterProxyContainer(windowId, inv.player, te)
        }
    }

    lateinit var CRAFTER_PROXY_LOOT_FUNCTION: LootFunctionType

    fun registerAll() {
        registerForge()
        registerLootFunctions()
    }

    private fun registerForge() {
        ITEMS_REGISTRY.register(MOD_BUS)
        BLOCKS_REGISTRY.register(MOD_BUS)
        BLOCK_ENTITIES_REGISTRY.register(MOD_BUS)
        CONTAINERS_REGISTRY.register(MOD_BUS)
    }

    private fun registerLootFunctions() {
        CRAFTER_PROXY_LOOT_FUNCTION = Registry.register(
            Registry.LOOT_FUNCTION_TYPE,
            ResourceLocation(MODID, CRAFTER_PROXY_ID),
            LootFunctionType(CrafterProxyLootFunction.Serializer())
        )
    }

    val CRAFTER_PROXY_TAB: ItemGroup = object : ItemGroup("${MODID}_tab") {
        override fun makeIcon(): ItemStack {
            return ItemStack(CRAFTER_PROXY_CARD)
        }
    }
}
