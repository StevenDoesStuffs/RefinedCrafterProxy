package dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy

import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem
import dev.stevendoesstuffs.refinedcrafterproxy.Config
import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import dev.stevendoesstuffs.refinedcrafterproxy.crafterproxy.CrafterProxyNetworkNode.Companion.NBT_TIER
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.world.World

class CrafterProxyBlockItem : BaseBlockItem(
    Registration.CRAFTER_PROXY_BLOCK, Properties().stacksTo(64).tab(
        Registration.CRAFTER_PROXY_TAB
    )
) {
    override fun appendHoverText(
        stack: ItemStack,
        world: World?,
        information: MutableList<ITextComponent>,
        flag: ITooltipFlag
    ) {
        val tag = stack.tag
        if (tag?.contains(NBT_TIER) == true) {
            Config.CONFIG.getDisplayName(tag.getString(NBT_TIER))?.let {
                information.add(StringTextComponent("Tier: ").append(it))
            }
        }
    }

    override fun fillItemCategory(itemGroup: ItemGroup, list: NonNullList<ItemStack>) {
        if (!this.allowdedIn(itemGroup)) return
        list.add(ItemStack(this))
        for (tier in Config.CONFIG.getCustomTiers()) {
            val stack = ItemStack(this)
            stack.orCreateTag.putString(NBT_TIER, tier)
            list.add(stack)
        }
    }
}
