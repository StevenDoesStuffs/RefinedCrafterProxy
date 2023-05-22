package dev.stevendoesstuffs.refinedcrafterproxy

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import java.util.*

@Mod(RefinedCrafterProxy.MODID)
object RefinedCrafterProxy {
    const val MODID: String = "refinedcrafterproxy"

    init {
        DistExecutor.safeRunWhenOn(Dist.CLIENT) {
            DistExecutor.SafeRunnable {
                MOD_BUS.addListener(ClientEvents::init)
                MOD_BUS.addListener(ClientEvents::onModelBake)
            }
        }
        MOD_BUS.addListener(CommonEvents::init)
        Registration.registerAll()
    }
}
