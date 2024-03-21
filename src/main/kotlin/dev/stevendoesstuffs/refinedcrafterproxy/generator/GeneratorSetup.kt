package dev.stevendoesstuffs.refinedcrafterproxy.generator

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.data.event.GatherDataEvent

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
object GeneratorSetup {
    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        generator.addProvider(event.includeClient(), ::LanguageGenerator)
        generator.addProvider(event.includeServer(), ::RecipeGenerator)
        generator.addProvider(event.includeServer(), ::LootTableGenerator)
    }
}
