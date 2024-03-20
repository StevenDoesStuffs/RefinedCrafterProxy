package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy

import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem
import dev.stevendoesstuffs.refinedcrafterproxy.Config
import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyNetworkNode.Companion.NBT_TIER
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class CrafterProxyBlockItem :
        BaseBlockItem(Registration.CRAFTER_PROXY_BLOCK, Properties().stacksTo(64)) {
    override fun appendHoverText(
            stack: ItemStack,
            level: Level?,
            information: MutableList<Component>,
            flag: TooltipFlag
    ) {
        val tag = stack.tag
        if (tag?.contains(NBT_TIER) == true) {
            Config.CONFIG.getDisplayName(tag.getString(NBT_TIER))?.let {
                information.add(Component.literal("Tier: ").append(it))
            }
        }
    }
}
