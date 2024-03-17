package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy

import com.mojang.blaze3d.vertex.PoseStack
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationClientListener
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton
import com.refinedmods.refinedstorage.util.RenderUtils
import dev.stevendoesstuffs.refinedcrafterproxy.RefinedCrafterProxy.MODID
import dev.stevendoesstuffs.refinedcrafterproxy.Registration.CRAFTER_PROXY_ID
import net.minecraft.ChatFormatting
import net.minecraft.client.resources.language.I18n
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class CrafterProxyScreen(container: CrafterProxyMenu?, inventory: Inventory?, title: Component?) :
        BaseScreen<CrafterProxyMenu>(container, 211, 137, inventory, title) {
    override fun onPostInit(x: Int, y: Int) {
        // NO OP
    }

    override fun tick(x: Int, y: Int) {
        // NO OP
    }

    override fun renderBackground(PoseStack: PoseStack, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(MODID, "gui/$CRAFTER_PROXY_ID.png")
        blit(PoseStack, x, y, 0, 0, xSize, ySize)
    }

    override fun renderForeground(PoseStack: PoseStack, mouseX: Int, mouseY: Int) {
        renderString(PoseStack, 7, 7, RenderUtils.shorten(title.string, 26))
        renderString(PoseStack, 7, 43, I18n.get("container.inventory"))
    }
}

class CrafterProxyBlockEntitySynchronizationParameterClientListener :
        BlockEntitySynchronizationClientListener<Boolean> {
    override fun onChanged(initial: Boolean, hasRoot: Boolean) {
        if (!hasRoot) {
            BaseScreen.executeLater(CrafterProxyScreen::class.java) { gui: CrafterProxyScreen ->
                gui.addSideButton(CrafterProxyModeSideButton(gui))
            }
        }
    }
}

class CrafterProxyModeSideButton(screen: BaseScreen<CrafterProxyMenu>?) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.get("sidebutton.refinedstorage.crafter_mode") +
                "\n" +
                ChatFormatting.GRAY +
                I18n.get(
                        "sidebutton.refinedstorage.crafter_mode." +
                                CrafterProxyBlockEntity.MODE.value
                )
    }

    override fun renderButtonIcon(PoseStack: PoseStack, x: Int, y: Int) {
        screen.blit(PoseStack, x, y, CrafterProxyBlockEntity.MODE.value * 16, 0, 16, 16)
    }

    override fun onPress() {
        BlockEntitySynchronizationManager.setParameter(
                CrafterProxyBlockEntity.MODE,
                CrafterProxyBlockEntity.MODE.value + 1
        )
    }
}
