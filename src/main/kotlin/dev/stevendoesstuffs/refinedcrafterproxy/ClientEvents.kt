package dev.stevendoesstuffs.refinedcrafterproxy

import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyScreen
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import thedarkcolour.kotlinforforge.forge.MOD_BUS

object ClientEvents {
    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun init(event: FMLClientSetupEvent) {
        MenuScreens.register(Registration.CRAFTER_PROXY_CONTAINER) { container, inventory, title ->
            CrafterProxyScreen(container, inventory, title)
        }
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
        MOD_BUS.addListener(ClientEvents::init)
        MOD_BUS.addListener(ClientEvents::onModelBake)
    }
}
