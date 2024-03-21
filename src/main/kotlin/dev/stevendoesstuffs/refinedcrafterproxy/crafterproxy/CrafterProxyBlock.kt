package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy

import com.refinedmods.refinedstorage.api.network.security.Permission
import com.refinedmods.refinedstorage.block.BlockDirection
import com.refinedmods.refinedstorage.block.NetworkNodeBlock
import com.refinedmods.refinedstorage.container.factory.BlockEntityMenuProvider
import com.refinedmods.refinedstorage.util.BlockUtils
import com.refinedmods.refinedstorage.util.NetworkUtils
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyNetworkNode.Companion.NBT_TIER
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class CrafterProxyBlock : NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES) {

    override fun getDirection(): BlockDirection {
        return BlockDirection.ANY_FACE_PLAYER
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return CrafterProxyBlockEntity(pos, state)
    }

    override fun setPlacedBy(
            level: Level,
            pos: BlockPos,
            state: BlockState,
            placer: LivingEntity?,
            stack: ItemStack
    ) {
        super.setPlacedBy(level, pos, state, placer, stack)
        if (!level.isClientSide()) {
            val blockEntity: BlockEntity? = level.getBlockEntity(pos)
            if (blockEntity is CrafterProxyBlockEntity) {
                if (stack.hasCustomHoverName()) {
                    blockEntity.node.setDisplayName(stack.hoverName.copy())
                    blockEntity.node.markDirty()
                }

                val tag = stack.tag
                if (tag?.contains(NBT_TIER) == true) {
                    blockEntity.node.tier = tag.getString(NBT_TIER)
                    blockEntity.node.markDirty()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun use(
            state: BlockState,
            level: Level,
            pos: BlockPos,
            player: Player,
            handIn: InteractionHand,
            hit: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide()) return InteractionResult.SUCCESS

        return NetworkUtils.attempt(
                level,
                pos,
                player,
                {
                    player.openMenu(
                            BlockEntityMenuProvider<CrafterProxyBlockEntity>(
                                    (level.getBlockEntity(pos) as CrafterProxyBlockEntity)
                                            .node
                                            .name,
                                    { blockEntity, windowId, _, _ ->
                                        CrafterProxyMenu(windowId, player, blockEntity)
                                    },
                                    pos
                            ),
                            pos
                    )
                },
                Permission.MODIFY,
                Permission.AUTOCRAFTING
        )
    }

    override fun hasConnectedState(): Boolean {
        return true
    }
}
