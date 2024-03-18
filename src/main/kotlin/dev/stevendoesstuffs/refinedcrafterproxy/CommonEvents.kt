package dev.stevendoesstuffs.refinedcrafterproxy

import com.refinedmods.refinedstorage.apiimpl.API
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyNetworkNode
import net.minecraft.nbt.CompoundNBT
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

object CommonEvents {
    @SubscribeEvent
    fun init(event: FMLCommonSetupEvent) {
        API.instance().networkNodeRegistry.add(
                        ResourceLocation(RefinedCrafterProxy.MODID, Registration.CRAFTER_PROXY_ID)
                ) { tag: CompoundNBT, world: World, pos: BlockPos? ->
            val node = CrafterProxyNetworkNode(world, pos)
            node.read(tag)
            return@add node
        }

        Registration.CRAFTER_PROXY_BLOCK_ENTITY.create()!!.dataManager.parameters.forEach {
                parameter: TileDataParameter<*, *>? ->
            TileDataManager.registerParameter(parameter)
        }
    }
}
