package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootContext
import net.minecraft.loot.LootFunction
import net.minecraft.loot.LootFunctionType
import net.minecraft.loot.LootParameters
import net.minecraft.loot.conditions.ILootCondition

class CrafterProxyLootFunction private constructor(conditions: Array<out ILootCondition>) : LootFunction(conditions) {

    companion object {
        fun builder(): Builder<*> {
            return simpleBuilder { conditions -> CrafterProxyLootFunction(conditions) }
        }
    }

    override fun run(stack: ItemStack, lootContext: LootContext): ItemStack {
        val blockEntity = lootContext.getParamOrNull(LootParameters.BLOCK_ENTITY)
        val removedNode = (blockEntity as CrafterProxyBlockEntity).removedNode ?: blockEntity.node
        if (removedNode.getDisplayName() != null) {
            stack.setHoverName(removedNode.getDisplayName())
        }
        return stack
    }

    override fun getType(): LootFunctionType {
        return Registration.CRAFTER_PROXY_LOOT_FUNCTION
    }

    class Serializer : LootFunction.Serializer<CrafterProxyLootFunction?>() {
        override fun deserialize(
            obj: JsonObject,
            deserializationContext: JsonDeserializationContext,
            conditions: Array<out ILootCondition>
        ): CrafterProxyLootFunction {
            return CrafterProxyLootFunction(conditions)
        }
    }
}
