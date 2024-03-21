package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy

import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyNetworkNode.Companion.NBT_TIER
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.functions.LootItemFunction
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType
import net.minecraft.world.level.storage.loot.parameters.LootContextParams

class CrafterProxyLootFunction : LootItemFunction {

    override fun apply(stack: ItemStack, lootContext: LootContext): ItemStack {
        val blockEntity = lootContext.getParamOrNull(LootContextParams.BLOCK_ENTITY)
        val removedNode = (blockEntity as CrafterProxyBlockEntity).removedNode ?: blockEntity.node
        if (removedNode.getDisplayName() != null) {
            stack.setHoverName(removedNode.getDisplayName())
        }
        removedNode.tier?.let { stack.orCreateTag.putString(NBT_TIER, it) }
        return stack
    }

    override fun getType(): LootItemFunctionType {
        return Registration.CRAFTER_PROXY_LOOT_FUNCTION
    }
}
