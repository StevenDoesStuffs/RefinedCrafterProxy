package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy

import com.refinedmods.refinedstorage.container.BaseContainerMenu
import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.items.SlotItemHandler

class CrafterProxyMenu(windowId: Int, player: Player, tile: CrafterProxyBlockEntity) :
        BaseContainerMenu(Registration.CRAFTER_PROXY_CONTAINER, tile, player, windowId) {
    init {
        addSlot(SlotItemHandler(tile.node.cardInventory, 0, 8 + 18 * 4, 20))
        for (i in 0..3) {
            addSlot(SlotItemHandler(tile.node.getUpgrades(), i, 187, 6 + i * 18))
        }
        addPlayerInventory(8, 55)
        transferManager.addBiTransfer(player.inventory, tile.node.getUpgrades())
        transferManager.addBiTransfer(player.inventory, tile.node.cardInventory)
    }
}
