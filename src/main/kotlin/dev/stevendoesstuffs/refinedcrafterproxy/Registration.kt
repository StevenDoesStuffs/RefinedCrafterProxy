package dev.stevendoesstuffs.refinedcrafterproxy

import com.refinedmods.refinedstorage.render.BakedModelOverrideRegistry
import dev.stevendoesstuffs.refinedcrafterproxy.RefinedCrafterProxy.MODID
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.*
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxycard.CrafterProxyCardItem
import net.minecraft.core.Registry
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType
import net.minecraftforge.common.extensions.IForgeMenuType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.registerObject

object Registration {
    val ITEMS_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MODID)
    val BLOCKS_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID)
    val BLOCK_ENTITIES_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID)
    val CONTAINERS_REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID)
    val LOOT_FUNCTIONS_REGISTRY = DeferredRegister.create(Registry.LOOT_FUNCTION_REGISTRY, MODID)
    val BAKED_MODEL_OVERRIDE_REGISTRY = BakedModelOverrideRegistry()

    const val CRAFTER_PROXY_ID = "crafter_proxy"
    const val CRAFTER_PROXY_CARD_ID = "crafter_proxy_card"

    val CRAFTER_PROXY_CARD by
            ITEMS_REGISTRY.registerObject(CRAFTER_PROXY_CARD_ID) { CrafterProxyCardItem() }

    val CRAFTER_PROXY_BLOCK by
            BLOCKS_REGISTRY.registerObject(CRAFTER_PROXY_ID) { CrafterProxyBlock() }

    val CRAFTER_PROXY_BLOCK_ITEM by
            ITEMS_REGISTRY.registerObject(CRAFTER_PROXY_ID) { CrafterProxyBlockItem() }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    val CRAFTER_PROXY_BLOCK_ENTITY: BlockEntityType<CrafterProxyBlockEntity> by
            BLOCK_ENTITIES_REGISTRY.registerObject(CRAFTER_PROXY_ID) {
                BlockEntityType.Builder.of(
                                { pos, state -> CrafterProxyBlockEntity(pos, state) },
                                CRAFTER_PROXY_BLOCK
                        )
                        .build(null)
            }

    val CRAFTER_PROXY_CONTAINER: MenuType<CrafterProxyMenu?> by
            CONTAINERS_REGISTRY.registerObject(CRAFTER_PROXY_ID) {
                IForgeMenuType.create { windowId, inv, data ->
                    val pos = data.readBlockPos()
                    val te = inv.player.commandSenderWorld.getBlockEntity(pos)
                    if (te !is CrafterProxyBlockEntity) {
                        return@create null
                    }
                    return@create CrafterProxyMenu(windowId, inv.player, te)
                }
            }

    val CRAFTER_PROXY_LOOT_FUNCTION by
            LOOT_FUNCTIONS_REGISTRY.registerObject(CRAFTER_PROXY_ID) {
                LootItemFunctionType(CrafterProxyLootFunction.Serializer())
            }

    fun registerAll() {
        ITEMS_REGISTRY.register(MOD_BUS)
        BLOCKS_REGISTRY.register(MOD_BUS)
        BLOCK_ENTITIES_REGISTRY.register(MOD_BUS)
        CONTAINERS_REGISTRY.register(MOD_BUS)
        LOOT_FUNCTIONS_REGISTRY.register(MOD_BUS)
    }

    val CRAFTER_PROXY_TAB: CreativeModeTab =
            object : CreativeModeTab("${MODID}_tab") {
                override fun makeIcon(): ItemStack {
                    return ItemStack(CRAFTER_PROXY_CARD)
                }
            }
}
