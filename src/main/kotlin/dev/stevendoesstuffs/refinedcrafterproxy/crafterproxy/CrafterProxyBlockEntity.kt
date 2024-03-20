package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec
import dev.stevendoesstuffs.refinedcrafterproxy.RefinedCrafterProxy.MODID
import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import dev.stevendoesstuffs.refinedcrafterproxy.Registration.CRAFTER_PROXY_BLOCK_ENTITY
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler

class CrafterProxyBlockEntity(pos: BlockPos, state: BlockState?) :
        NetworkNodeBlockEntity<CrafterProxyNetworkNode>(
                Registration.CRAFTER_PROXY_BLOCK_ENTITY,
                pos,
                state,
                SPEC,
                CrafterProxyNetworkNode::class.java
        ) {
    private val cardCapability = LazyOptional.of<IItemHandler?> { node.cardInventory }

    override fun createNode(level: Level, pos: BlockPos): CrafterProxyNetworkNode {
        return CrafterProxyNetworkNode(level, pos)
    }

    override fun <T> getCapability(cap: Capability<T>, direction: Direction?): LazyOptional<T> {
        return if (cap === ForgeCapabilities.ITEM_HANDLER &&
                        direction != null &&
                        direction != this.node.direction
        ) {
            cardCapability.cast()
        } else super.getCapability(cap, direction)
    }

    companion object {
        val MODE =
                BlockEntitySynchronizationParameter(
                        ResourceLocation(MODID, "crafter_mode"),
                        EntityDataSerializers.INT,
                        CrafterProxyNetworkNode.CrafterMode.IGNORE.ordinal,
                        { t: CrafterProxyBlockEntity -> t.node.getMode().ordinal }
                ) { t, v -> t.node.setMode(CrafterProxyNetworkNode.CrafterMode.getById(v!!)) }
        private val HAS_ROOT =
                BlockEntitySynchronizationParameter(
                        ResourceLocation(MODID, "crafter_has_root"),
                        EntityDataSerializers.BOOLEAN,
                        false,
                        { t: CrafterProxyBlockEntity ->
                            t.node.getRootContainerNotSelf().isPresent
                        },
                        null
                ) { t, v ->
                    CrafterProxyBlockEntitySynchronizationParameterClientListener().onChanged(t, v)
                }
        public val SPEC: BlockEntitySynchronizationSpec =
                BlockEntitySynchronizationSpec.builder()
                        .addWatchedParameter(REDSTONE_MODE)
                        .addWatchedParameter(MODE)
                        .addParameter(HAS_ROOT)
                        .build()
    }
}
