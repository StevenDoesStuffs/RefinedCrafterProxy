package dev.stevendoesstuffs.refinedcrafterproxy.generator

import com.mojang.datafixers.util.Pair
import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyLootFunction
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Supplier
import net.minecraft.block.Block
import net.minecraft.data.DataGenerator
import net.minecraft.data.LootTableProvider
import net.minecraft.data.loot.BlockLootTables
import net.minecraft.loot.*
import net.minecraft.loot.conditions.SurvivesExplosion
import net.minecraft.util.ResourceLocation

class LootTableGenerator(gen: DataGenerator) : LootTableProvider(gen) {

    override fun getTables():
            MutableList<
                    Pair<
                            Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>,
                            LootParameterSet>> {
        return mutableListOf(
                Pair.of(Supplier { CrafterProxyBlockLootTables() }, LootParameterSets.BLOCK)
        )
    }

    override fun validate(
            map: MutableMap<ResourceLocation, LootTable>,
            validationtracker: ValidationTracker
    ) {}

    override fun getName(): String {
        return "Refined Crafter Proxy Loot Tables"
    }

    private class CrafterProxyBlockLootTables : BlockLootTables() {
        override fun addTables() {
            val block = Registration.CRAFTER_PROXY_BLOCK
            val builder = CrafterProxyLootFunction.builder()
            add(
                    block,
                    LootTable.lootTable()
                            .withPool(
                                    LootPool.lootPool()
                                            .setRolls(ConstantRange.exactly(1))
                                            .add(ItemLootEntry.lootTableItem(block).apply(builder))
                                            .`when`(SurvivesExplosion.survivesExplosion())
                            )
            )
        }

        override fun getKnownBlocks(): Iterable<Block> {
            return listOf(Registration.CRAFTER_PROXY_BLOCK)
        }
    }
}
