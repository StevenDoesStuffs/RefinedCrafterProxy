package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyNetworkNode.Companion.NBT_TIER
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition

class CrafterProxyLootItemFunction private constructor(conditions: Array<out LootItemCondition>) :
        LootItemConditionalFunction(conditions) {

    companion object {
        fun builder(): Builder<*> {
            return simpleBuilder { conditions -> CrafterProxyLootItemFunction(conditions) }
        }
    }

    override fun run(stack: ItemStack, lootContext: LootContext): ItemStack {
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

    class Serializer : LootItemConditionalFunction.Serializer<CrafterProxyLootItemFunction?>() {
        override fun deserialize(
                obj: JsonObject,
                deserializationContext: JsonDeserializationContext,
                conditions: Array<out LootItemCondition>
        ): CrafterProxyLootItemFunction {
            return CrafterProxyLootItemFunction(conditions)
        }
    }
}
