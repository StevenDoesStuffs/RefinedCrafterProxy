package dev.stevendoesstuffs.refinedcrafterproxy.generator

import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyLootFunction
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.*
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.parameters.*
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue

class LootTableGenerator(gen: PackOutput) :
        LootTableProvider(
                gen,
                emptySet(),
                listOf(
                        LootTableProvider.SubProviderEntry(
                                ::CrafterProxyBlockLootSubProvider,
                                LootContextParamSets.BLOCK
                        )
                )
        ) {

    private class CrafterProxyBlockLootSubProvider :
            BlockLootSubProvider(emptySet(), FeatureFlags.REGISTRY.allFlags()) {

        override fun generate() {
            val block = Registration.CRAFTER_PROXY_BLOCK
            val builder = CrafterProxyLootFunction.builder()
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
