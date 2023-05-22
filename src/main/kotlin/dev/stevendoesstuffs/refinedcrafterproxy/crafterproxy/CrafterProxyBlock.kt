package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy

import com.refinedmods.refinedstorage.api.network.security.Permission
import com.refinedmods.refinedstorage.block.BlockDirection
import com.refinedmods.refinedstorage.block.NetworkNodeBlock
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider
import com.refinedmods.refinedstorage.util.BlockUtils
import com.refinedmods.refinedstorage.util.NetworkUtils
import net.minecraft.block.BlockState
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ActionResultType
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.world.IBlockReader
import net.minecraft.world.World
import net.minecraftforge.fml.network.NetworkHooks

class CrafterProxyBlock : NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES) {

    override fun getDirection(): BlockDirection {
        return BlockDirection.ANY_FACE_PLAYER
    }

    override fun createTileEntity(state: BlockState, world: IBlockReader): TileEntity {
        return CrafterProxyBlockEntity()
    }

    override fun setPlacedBy(
        worldIn: World,
        pos: BlockPos,
        state: BlockState,
        placer: LivingEntity?,
        stack: ItemStack
    ) {
        super.setPlacedBy(worldIn, pos, state, placer, stack)
        if (!worldIn.isClientSide()) {
            val tile: TileEntity? = worldIn.getBlockEntity(pos)
            if ((tile is CrafterProxyBlockEntity) && stack.hasCustomHoverName()) {
                tile.node.setDisplayName(stack.displayName)
                tile.node.markDirty()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun use(
        state: BlockState,
        worldIn: World,
        pos: BlockPos,
        player: PlayerEntity,
        handIn: Hand,
        hit: BlockRayTraceResult
    ): ActionResultType {
        if (worldIn.isClientSide()) return ActionResultType.SUCCESS

        return NetworkUtils.attempt(worldIn, pos, player, {
            NetworkHooks.openGui(
                player as ServerPlayerEntity,
                PositionalTileContainerProvider<CrafterProxyBlockEntity>(
                    (worldIn.getBlockEntity(pos) as CrafterProxyBlockEntity).node.name,
                    { tile, windowId, _, _ ->
                        CrafterProxyContainer(
                            windowId,
                            player,
                            tile
                        )
                    },
                    pos
                ),
                pos
            )
        }, Permission.MODIFY, Permission.AUTOCRAFTING)
    }

    override fun hasConnectedState(): Boolean {
        return true
    }
}
