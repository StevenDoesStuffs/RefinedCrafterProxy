package dev.stevendoesstuffs.refinedcrafterproxy.jei

import dev.stevendoesstuffs.refinedcrafterproxy.RefinedCrafterProxy
import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.ISubtypeRegistration
import net.minecraft.util.ResourceLocation

@JeiPlugin
class RefinedCrafterProxyJeiPlugin : IModPlugin {
    override fun getPluginUid(): ResourceLocation {
        return ResourceLocation(RefinedCrafterProxy.MODID, "jei")
    }

    override fun registerItemSubtypes(registration: ISubtypeRegistration) {
        registration.useNbtForSubtypes(Registration.CRAFTER_PROXY_BLOCK_ITEM)
    }
}
