package dev.stevendoesstuffs.refinedcrafterproxy.generator

import dev.stevendoesstuffs.refinedcrafterproxy.RefinedCrafterProxy.MODID
import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import dev.stevendoesstuffs.refinedcrafterproxy.Registration.CRAFTER_PROXY_CARD_ID
import dev.stevendoesstuffs.refinedcrafterproxy.Registration.CREATIVE_TAB_GENERAL_ID
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider

class LanguageGenerator(gen: PackOutput) : LanguageProvider(gen, MODID, "en_us") {
    override fun addTranslations() {
        add(Registration.CRAFTER_PROXY_CARD, "Crafter Proxy Link Card")
        add("$MODID.$CRAFTER_PROXY_CARD_ID.select", "Block selected")
        add("$MODID.$CRAFTER_PROXY_CARD_ID.clear", "Selection cleared")
        add(
                "$MODID.$CRAFTER_PROXY_CARD_ID.tooltip",
                "Linked to a block in the %4\$s at (%1\$d, %2\$d, %3\$d)"
        )
        add("$MODID.$CRAFTER_PROXY_CARD_ID.status.prefix", "Last status: ")
        add("$MODID.$CRAFTER_PROXY_CARD_ID.status.disconnected", "Disconnected")
        add("$MODID.$CRAFTER_PROXY_CARD_ID.status.invalid_crafter", "Invalid target crafter")
        add("$MODID.$CRAFTER_PROXY_CARD_ID.status.connected", "Connected")

        add(Registration.CRAFTER_PROXY_BLOCK_ITEM, "Crafter Proxy")

        add("item_group.$MODID.$CREATIVE_TAB_GENERAL_ID", "Refined Crafter Proxy")
    }
}
