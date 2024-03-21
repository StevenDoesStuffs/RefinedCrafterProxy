package dev.stevendoesstuffs.refinedcrafterproxy

import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyScreen
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.ModelEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

object ClientEvents {

    @SubscribeEvent
    fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(Registration.CRAFTER_PROXY_CONTAINER, ::CrafterProxyScreen)
    }

    @SubscribeEvent
    fun onModelBake(e: ModelEvent.BakingCompleted) {
        for (id in e.models.keys) {
            val factory =
                    Registration.BAKED_MODEL_OVERRIDE_REGISTRY[
                            ResourceLocation(id.namespace, id.path)]
            if (factory != null) {
                e.models.put(id, factory.create(e.models.get(id), e.models))
            }
        }
    }

    fun addListeners() {
        MOD_BUS.addListener(::registerScreens)
        MOD_BUS.addListener(::onModelBake)
    }
}
