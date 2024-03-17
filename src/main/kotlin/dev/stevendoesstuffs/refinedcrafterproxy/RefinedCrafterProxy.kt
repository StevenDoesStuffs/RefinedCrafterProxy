package dev.stevendoesstuffs.refinedcrafterproxy

import java.util.*
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(RefinedCrafterProxy.MODID)
object RefinedCrafterProxy {
    const val MODID: String = "refinedcrafterproxy"

    init {
        DistExecutor.safeRunWhenOn(Dist.CLIENT) {
            DistExecutor.SafeRunnable(ClientEvents::addListeners)
        }
        MOD_BUS.addListener(CommonEvents::init)
        Registration.registerAll()
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.CONFIG_SPEC)
    }
}
