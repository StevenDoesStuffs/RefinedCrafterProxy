package dev.stevendoesstuffs.refinedcrafterproxy

import com.refinedmods.refinedstorage.render.model.FullbrightBakedModel
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyScreen
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.event.ModelBakeEvent
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
        ItemBlockRenderTypes.setRenderLayer(Registration.CRAFTER_PROXY_BLOCK, RenderType.cutout())
        val parent = "block/${Registration.CRAFTER_PROXY_ID}/cutouts/"
        Registration.bakedModelOverrideRegistry.add(
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
                    Registration.bakedModelOverrideRegistry[ResourceLocation(id.namespace, id.path)]
            if (factory != null) {
                e.modelRegistry[id] = factory.create(e.modelRegistry[id], e.modelRegistry)
            }
        }
    }

    fun addListeners() {
        MOD_BUS.addListener(ClientEvents::init)
        MOD_BUS.addListener(ClientEvents::onModelBake)
    }
}
