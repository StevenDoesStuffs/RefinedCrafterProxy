package dev.stevendoesstuffs.refinedcrafterproxy

import com.refinedmods.refinedstorage.render.model.FullbrightBakedModel
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyScreen
import net.minecraft.client.gui.ScreenManager
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderTypeLookup
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelBakeEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

object ClientEvents {
    @SubscribeEvent
    fun init(event: FMLClientSetupEvent) {
        ScreenManager.register(Registration.CRAFTER_PROXY_CONTAINER) { container, inventory, title
            ->
            CrafterProxyScreen(container, inventory, title)
        }
        RenderTypeLookup.setRenderLayer(Registration.CRAFTER_PROXY_BLOCK, RenderType.cutout())
        val parent = "block/${Registration.CRAFTER_PROXY_ID}/cutouts/"
        Registration.BAKED_MODEL_OVERRIDE_REGISTRY.add(
                Registration.CRAFTER_PROXY_BLOCK.registryName
        ) { base, _ ->
            FullbrightBakedModel(
                    base,
                    true,
                    ResourceLocation(RefinedCrafterProxy.MODID, parent + "side_connected"),
                    ResourceLocation(RefinedCrafterProxy.MODID, parent + "top_connected")
            )
        }
    }

    @SubscribeEvent
    fun onModelBake(e: ModelBakeEvent) {
        FullbrightBakedModel.invalidateCache()
        for (id in e.modelRegistry.keys) {
            val factory =
                    Registration.BAKED_MODEL_OVERRIDE_REGISTRY[ResourceLocation(id.namespace, id.path)]
            if (factory != null) {
                e.modelRegistry[id] = factory.create(e.modelRegistry[id], e.modelRegistry)
            }
        }
    }
}
