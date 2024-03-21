package dev.stevendoesstuffs.refinedcrafterproxy

import java.util.*
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist

@Mod(RefinedCrafterProxy.MODID)
object RefinedCrafterProxy {
    const val MODID: String = "refinedcrafterproxy"

    init {
        runForDist(clientTarget = ClientEvents::addListeners, serverTarget = {})
        CommonEvents.addListeners()
        Registration.registerAll()
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC)
    }
}
