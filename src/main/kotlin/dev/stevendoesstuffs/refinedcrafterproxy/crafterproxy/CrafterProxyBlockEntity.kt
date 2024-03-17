package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter
import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import dev.stevendoesstuffs.refinedcrafterproxy.Registration.CRAFTER_PROXY_BLOCK_ENTITY
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler

class CrafterProxyBlockEntity(pos: BlockPos, state: BlockState?) :
        NetworkNodeBlockEntity<CrafterProxyNetworkNode>(
                Registration.CRAFTER_PROXY_BLOCK_ENTITY,
                pos,
                state,
                CrafterProxyNetworkNode::class.java
        ) {
    private val cardCapability = LazyOptional.of<IItemHandler?> { node.cardInventory }

    init {
        dataManager.addWatchedParameter(MODE)
        dataManager.addParameter(HAS_ROOT)
    }

    override fun createNode(level: Level, pos: BlockPos): CrafterProxyNetworkNode {
        return CrafterProxyNetworkNode(level, pos)
    }

    override fun <T> getCapability(cap: Capability<T>, direction: Direction?): LazyOptional<T> {
        return if (cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY &&
                        direction != null &&
                        direction != this.node.direction
        ) {
            cardCapability.cast()
        } else super.getCapability(cap, direction)
    }

    companion object {
        val MODE =
                BlockEntitySynchronizationParameter(
                        EntityDataSerializers.INT,
                        CrafterProxyNetworkNode.CrafterMode.IGNORE.ordinal,
                        { t: CrafterProxyBlockEntity -> t.node.getMode().ordinal }
                ) { t, v -> t.node.setMode(CrafterProxyNetworkNode.CrafterMode.getById(v!!)) }
        private val HAS_ROOT =
                BlockEntitySynchronizationParameter(
                        EntityDataSerializers.BOOLEAN,
                        false,
                        { t: CrafterProxyBlockEntity ->
                            t.node.getRootContainerNotSelf().isPresent
                        },
                        null
                ) { t, v ->
                    CrafterProxyBlockEntitySynchronizationParameterClientListener().onChanged(t, v)
                }
    }
}
