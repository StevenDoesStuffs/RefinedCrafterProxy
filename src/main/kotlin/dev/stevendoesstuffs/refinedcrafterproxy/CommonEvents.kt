package dev.stevendoesstuffs.refinedcrafterproxy

import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager
import dev.stevendoesstuffs.refinedcrafterproxy.Registration.CRAFTER_PROXY_BLOCK_ENTITY
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyBlockEntity
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyNetworkNode
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

object CommonEvents {
    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun init(event: FMLCommonSetupEvent) {
        API.instance().networkNodeRegistry.add(
                        ResourceLocation(RefinedCrafterProxy.MODID, Registration.CRAFTER_PROXY_ID)
                ) { tag: CompoundTag, level: Level, pos: BlockPos? ->
            val node = CrafterProxyNetworkNode(level, pos)
            node.read(tag)
            return@add node
        }

        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        Registration.CRAFTER_PROXY_BLOCK_ENTITY.create(BlockPos.ZERO, null)!!.dataManager.parameters
                .forEach(BlockEntitySynchronizationManager::registerParameter)
    }

    @SubscribeEvent
    fun registerCapabilities(event: RegisterCapabilitiesEvent) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                CRAFTER_PROXY_BLOCK_ENTITY,
                CrafterProxyBlockEntity::getProxyCardInv
        )
    }

    fun addListeners() {
        MOD_BUS.addListener(::init)
        MOD_BUS.addListener(::registerCapabilities)
    }
}
