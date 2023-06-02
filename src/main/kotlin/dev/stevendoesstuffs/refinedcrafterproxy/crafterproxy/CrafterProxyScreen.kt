package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameterClientListener
import com.refinedmods.refinedstorage.util.RenderUtils
import dev.stevendoesstuffs.refinedcrafterproxy.RefinedCrafterProxy.MODID
import dev.stevendoesstuffs.refinedcrafterproxy.Registration.CRAFTER_PROXY_ID
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextFormatting

class CrafterProxyScreen(container: CrafterProxyContainer?, inventory: PlayerInventory?, title: ITextComponent?) :
    BaseScreen<CrafterProxyContainer?>(container, 211, 137, inventory, title) {
    override fun onPostInit(x: Int, y: Int) {
        // NO OP
    }

    override fun tick(x: Int, y: Int) {
        // NO OP
    }

    override fun renderBackground(matrixStack: MatrixStack, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(MODID, "gui/$CRAFTER_PROXY_ID.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
    }

    override fun renderForeground(matrixStack: MatrixStack, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, RenderUtils.shorten(title.string, 26))
        renderString(matrixStack, 7, 43, I18n.get("container.inventory"))
    }
}

class CrafterProxyTileDataParameterClientListener : TileDataParameterClientListener<Boolean> {
    override fun onChanged(initial: Boolean, hasRoot: Boolean) {
        if (!hasRoot) {
            BaseScreen.executeLater(
                CrafterProxyScreen::class.java
            ) { gui: CrafterProxyScreen ->
                gui.addSideButton(
                    CrafterProxyModeSideButton(gui)
                )
            }
        }
    }
}

class CrafterProxyModeSideButton(screen: BaseScreen<CrafterProxyContainer?>?) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.get("sidebutton.refinedstorage.crafter_mode") + "\n" + TextFormatting.GRAY + I18n.get("sidebutton.refinedstorage.crafter_mode." + CrafterProxyBlockEntity.MODE.value)
    }

    override fun renderButtonIcon(matrixStack: MatrixStack, x: Int, y: Int) {
        screen.blit(matrixStack, x, y, CrafterProxyBlockEntity.MODE.value * 16, 0, 16, 16)
    }

    override fun onPress() {
        TileDataManager.setParameter(CrafterProxyBlockEntity.MODE, CrafterProxyBlockEntity.MODE.value + 1)
    }
}
