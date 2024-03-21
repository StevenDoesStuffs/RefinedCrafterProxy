package dev.stevendoesstuffs.refinedcrafterproxy

import com.mojang.serialization.Codec
import com.refinedmods.refinedstorage.render.BakedModelOverrideRegistry
import dev.stevendoesstuffs.refinedcrafterproxy.RefinedCrafterProxy.MODID
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.*
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyNetworkNode.Companion.NBT_TIER
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxycard.CrafterProxyCardItem
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object Registration {
    val ITEMS_REGISTRY = DeferredRegister.createItems(MODID)
    val BLOCKS_REGISTRY = DeferredRegister.createBlocks(MODID)
    val BLOCK_ENTITIES_REGISTRY =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID)
    val CONTAINERS_REGISTRY = DeferredRegister.create(BuiltInRegistries.MENU, MODID)
    val LOOT_FUNCTIONS_REGISTRY =
            DeferredRegister.create(BuiltInRegistries.LOOT_FUNCTION_TYPE, MODID)
    val CREATIVE_TABS_REGISTRY = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, MODID)
    val BAKED_MODEL_OVERRIDE_REGISTRY = BakedModelOverrideRegistry()

    const val CRAFTER_PROXY_ID = "crafter_proxy"
    const val CRAFTER_PROXY_CARD_ID = "crafter_proxy_card"
    const val CREATIVE_TAB_GENERAL_ID = "general"

    val CRAFTER_PROXY_CARD by
            ITEMS_REGISTRY.register(CRAFTER_PROXY_CARD_ID) { -> CrafterProxyCardItem() }

    val CRAFTER_PROXY_BLOCK by BLOCKS_REGISTRY.register(CRAFTER_PROXY_ID) { -> CrafterProxyBlock() }

    val CRAFTER_PROXY_BLOCK_ITEM by
            ITEMS_REGISTRY.register(CRAFTER_PROXY_ID) { -> CrafterProxyBlockItem() }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    val CRAFTER_PROXY_BLOCK_ENTITY: BlockEntityType<CrafterProxyBlockEntity> by
            BLOCK_ENTITIES_REGISTRY.register(CRAFTER_PROXY_ID) { ->
                BlockEntityType.Builder.of(
                                { pos, state -> CrafterProxyBlockEntity(pos, state) },
                                CRAFTER_PROXY_BLOCK
                        )
                        .build(null)
            }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    val CRAFTER_PROXY_CONTAINER: MenuType<CrafterProxyMenu?> by
            CONTAINERS_REGISTRY.register(CRAFTER_PROXY_ID) { ->
                IMenuTypeExtension.create { windowId, inv, data ->
                    val pos = data.readBlockPos()
                    val te = inv.player.commandSenderWorld.getBlockEntity(pos)
                    if (te !is CrafterProxyBlockEntity) {
                        return@create null
                    }
                    return@create CrafterProxyMenu(windowId, inv.player, te)
                }
            }

    val CRAFTER_PROXY_LOOT_FUNCTION by
            LOOT_FUNCTIONS_REGISTRY.register(CRAFTER_PROXY_ID) { ->
                LootItemFunctionType(Codec.unit(CrafterProxyLootFunction()))
            }

    val CRAFTER_PROXY_TAB by
            CREATIVE_TABS_REGISTRY.register(CREATIVE_TAB_GENERAL_ID) { ->
                CreativeModeTab.builder()
                        .title(
                                Component.translatable(
                                        "item_group.${MODID}.${CREATIVE_TAB_GENERAL_ID}"
                                )
                        )
                        .icon() { ItemStack(CRAFTER_PROXY_CARD) }
                        .displayItems() { _, output ->
                            output.accept(ItemStack(CRAFTER_PROXY_CARD))
                            output.accept(ItemStack(CRAFTER_PROXY_BLOCK_ITEM))
                            for (tier in Config.CONFIG.getCustomTiers()) {
                                val stack = ItemStack(CRAFTER_PROXY_BLOCK_ITEM)
                                stack.orCreateTag.putString(NBT_TIER, tier)
                                output.accept(stack)
                            }
                        }
                        .build()
            }

    fun registerAll() {
        ITEMS_REGISTRY.register(MOD_BUS)
        BLOCKS_REGISTRY.register(MOD_BUS)
        BLOCK_ENTITIES_REGISTRY.register(MOD_BUS)
        CONTAINERS_REGISTRY.register(MOD_BUS)
        LOOT_FUNCTIONS_REGISTRY.register(MOD_BUS)
        CREATIVE_TABS_REGISTRY.register(MOD_BUS)
    }
}
