package dev.stevendoesstuffs.refinedcrafterproxy.generator

import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object GeneratorSetup {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        generator.addProvider(event.includeClient(), LanguageGenerator(generator))
        generator.addProvider(event.includeServer(), RecipeGenerator(generator))
        generator.addProvider(event.includeServer(), LootTableGenerator(generator))
    }
}
