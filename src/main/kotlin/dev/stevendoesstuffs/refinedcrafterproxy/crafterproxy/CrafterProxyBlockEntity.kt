package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy

import com.refinedmods.refinedstorage.tile.NetworkNodeTile
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import dev.stevendoesstuffs.refinedcrafterproxy.Registration.CRAFTER_PROXY_BLOCK_ENTITY
import net.minecraft.network.datasync.DataSerializers
import net.minecraft.util.Direction
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler

class CrafterProxyBlockEntity : NetworkNodeTile<CrafterProxyNetworkNode>(CRAFTER_PROXY_BLOCK_ENTITY) {
    private val cardCapability = LazyOptional.of<IItemHandler?> { node.cardInventory }

    init {
        dataManager.addWatchedParameter(MODE)
        dataManager.addParameter(HAS_ROOT)
    }

    override fun createNode(world: World, pos: BlockPos): CrafterProxyNetworkNode {
        return CrafterProxyNetworkNode(world, pos)
    }

    override fun <T> getCapability(cap: Capability<T>, direction: Direction?): LazyOptional<T> {
        return if (cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && direction != null && direction != this.node.direction) {
            cardCapability.cast()
        } else super.getCapability(cap, direction)
    }

    companion object {
        val MODE =
            TileDataParameter(DataSerializers.INT,
                CrafterProxyNetworkNode.CrafterMode.IGNORE.ordinal,
                { t: CrafterProxyBlockEntity -> t.node.getMode().ordinal }
            ) { t, v ->
                t.node.setMode(CrafterProxyNetworkNode.CrafterMode.getById(v!!))
            }
        private val HAS_ROOT =
            TileDataParameter(
                DataSerializers.BOOLEAN, false,
                { t: CrafterProxyBlockEntity -> t.node.getRootContainerNotSelf().isPresent },
                null
            ) { t, v ->
                CrafterProxyTileDataParameterClientListener().onChanged(t, v)
            }
    }
}
