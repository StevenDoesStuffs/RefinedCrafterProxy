package dev.stevendoesstuffs.refinedcrafterproxy.generator

import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object GeneratorSetup {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        if (event.includeClient()) {
            generator.addProvider(LanguageGenerator(generator))
        }
        if (event.includeServer()) {
            generator.addProvider(LootTableGenerator(generator))
        }
    }
}
