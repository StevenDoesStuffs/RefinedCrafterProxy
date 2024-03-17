package dev.stevendoesstuffs.refinedcrafterproxy.generator

import com.mojang.datafixers.util.Pair
import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyLootItemFunction
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier
import net.minecraft.data.DataGenerator
import net.minecraft.data.loot.BlockLoot
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.*
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.parameters.*
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue

class LootTableGenerator(gen: DataGenerator) : LootTableProvider(gen) {

    override fun getTables():
            MutableList<
                    Pair<
                            Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>,
                            LootContextParamSet>> {
        return mutableListOf(
                Pair.of(Supplier { RefinedCrafterProxyBlockLoot() }, LootContextParamSets.BLOCK)
        )
    }

    override fun validate(
            map: MutableMap<ResourceLocation, LootTable>,
            validationtracker: ValidationContext
    ) {}

    override fun getName(): String {
        return "Refined Crafter Proxy Loot Tables"
    }

    private class RefinedCrafterProxyBlockLoot : BlockLoot() {
        override fun addTables() {
            val block = Registration.CRAFTER_PROXY_BLOCK
            val builder = CrafterProxyLootItemFunction.builder()
            add(
                    block,
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ConstantValue.exactly(1.0f))
                                            .add(LootItem.lootTableItem(block).apply(builder))
                                            .`when`(ExplosionCondition.survivesExplosion())
                            )
            )
        }

        override fun getKnownBlocks(): Iterable<Block> {
            return listOf(Registration.CRAFTER_PROXY_BLOCK)
        }
    }
}
