package dev.stevendoesstuffs.refinedcrafterproxy

import com.electronwill.nightconfig.core.CommentedConfig
import com.electronwill.nightconfig.core.Config
import com.electronwill.nightconfig.core.ConfigSpec
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraftforge.common.ForgeConfigSpec

class Config(builder: ForgeConfigSpec.Builder) {
    companion object {
        private const val DEFAULT_TIER: String = "default"
        private const val TIERS: String = "tiers"
        private const val DISPLAY_NAME: String = "displayName"
        private const val CRAFTER_ENERGY_USAGE: String = "crafterEnergyUsage"
        private const val PATTERNS_ENERGY_USAGE: String = "patternsEnergyUsage"
        private const val UPGRADES_ENERGY_MULTIPLIER: String = "upgradesEnergyMultiplier"
        private const val UPDATE_INTERVALS: String = "updateIntervals"
        private const val MAXIMUM_SUCCESSFUL_CRAFTING_UPDATES: String =
                "maximumSuccessfulCraftingUpdates"

        @JvmStatic val CONFIG: dev.stevendoesstuffs.refinedcrafterproxy.Config

        @JvmStatic val CONFIG_SPEC: ForgeConfigSpec

        init {
            val (config, configSpec) =
                    ForgeConfigSpec.Builder().configure { builder: ForgeConfigSpec.Builder ->
                        Config(builder)
                    }
            CONFIG = config
            CONFIG_SPEC = configSpec
        }
    }

    private val tiers: ForgeConfigSpec.ConfigValue<Config>

    init {
        builder.comment(
                "Note: for technical reasons, if anything in the tiers section is invalid, it will reset the entire section.",
                "Luckily, forge backs up invalid configs in .toml.bak files, so look there if everything got reset."
        )
        tiers =
                builder.define(
                        TIERS,
                        {
                            val defaultTier = CommentedConfig.inMemory()

                            defaultTier.setComment(
                                    DISPLAY_NAME,
                                    """
                        The display name of the tier. 
                        If nonempty, the name of the block will be formatted as `Crafter Proxy [{displayName}]`.
                    """.trimIndent()
                            )
                            defaultTier.add(DISPLAY_NAME, "")

                            defaultTier.setComment(
                                    CRAFTER_ENERGY_USAGE,
                                    """
                        The energy used for the crafter. 
                        Must be an integer >= 0.
                    """.trimIndent()
                            )
                            defaultTier.add(CRAFTER_ENERGY_USAGE, 4)

                            defaultTier.setComment(
                                    PATTERNS_ENERGY_USAGE,
                                    """
                        The energy used for every pattern in the crafter. 
                        Must be an integer >= 0.
                    """.trimIndent()
                            )
                            defaultTier.add(PATTERNS_ENERGY_USAGE, 1)

                            defaultTier.setComment(
                                    UPGRADES_ENERGY_MULTIPLIER,
                                    """
                        The multiplier for the energy usage of the upgrades in the crafter.
                        This does *not* multiply the total energy usage, only the portion used by upgrades.
                        See refinedstorage-server.toml#upgrades for the energy usage of specific upgrades.
                        Must be a float >= 0.
                    """.trimIndent()
                            )
                            defaultTier.add(UPGRADES_ENERGY_MULTIPLIER, 1.0)

                            defaultTier.setComment(
                                    UPDATE_INTERVALS,
                                    """
                        The update interval of the crafter, 
                        where the (zero-indexed) index in the list is the number of speed upgrades.
                        Must be a list of integers of length 5 with elements > 0.
                    """.trimIndent()
                            )
                            defaultTier.add(UPDATE_INTERVALS, listOf(10, 8, 6, 4, 2))

                            defaultTier.setComment(
                                    MAXIMUM_SUCCESSFUL_CRAFTING_UPDATES,
                                    """
                        The maximum number of successful crafting updates per crafter update, 
                        where the (zero-indexed) index in the list is the number of speed upgrades.
                        Must be a list of integers of length 5 with elements > 0.
                    """.trimIndent()
                            )
                            defaultTier.add(
                                    MAXIMUM_SUCCESSFUL_CRAFTING_UPDATES,
                                    listOf(1, 2, 3, 4, 5)
                            )

                            val config = CommentedConfig.inMemory()
                            config.setComment(
                                    DEFAULT_TIER,
                                    """
                        Special tier for crafters without a tier tag.
                        For values other than `default`, this is the value that appears within the tier tag.
                    """.trimIndent()
                            )
                            config.add(DEFAULT_TIER, defaultTier)
                            config
                        }
                ) {
                    if (it !is Config || !it.contains(DEFAULT_TIER)) return@define false

                    val spec = ConfigSpec()
                    // note: the defaults listed here have no effect, only the validators
                    spec.define(DISPLAY_NAME, "")
                    spec.defineInRange(CRAFTER_ENERGY_USAGE, 4, 0, Int.MAX_VALUE)
                    spec.defineInRange(PATTERNS_ENERGY_USAGE, 1, 0, Int.MAX_VALUE)
                    spec.defineInRange(
                            UPGRADES_ENERGY_MULTIPLIER,
                            1.0,
                            0.0,
                            Int.MAX_VALUE.toDouble()
                    )
                    spec.defineList(UPDATE_INTERVALS, listOf(10, 8, 6, 4, 2)) { x ->
                        if (x !is Int) false else x > 0
                    }
                    spec.defineList(MAXIMUM_SUCCESSFUL_CRAFTING_UPDATES, listOf(1, 2, 3, 4, 5)) { x
                        ->
                        if (x !is Int) false else x > 0
                    }

                    for (entry in it.valueMap()) {
                        val config = entry.value
                        println(config)
                        if (config !is Config) return@define false
                        if (!spec.isCorrect(config)) return@define false
                        if (config.get<List<Int>>(UPDATE_INTERVALS).size != 5 ||
                                        config.get<List<Int>>(MAXIMUM_SUCCESSFUL_CRAFTING_UPDATES)
                                                .size != 5
                        )
                                return@define false
                    }
                    true
                }
    }

    fun getCustomTiers(): Set<String> {
        val set = HashSet(tiers.get().valueMap().keys)
        set.remove(DEFAULT_TIER)
        return set
    }

    private fun getTier(tier: String?): Config {
        return tiers.get().get(tier ?: DEFAULT_TIER) ?: tiers.get().get(DEFAULT_TIER)
    }

    fun getDisplayName(tier: String?): Component? {
        if (tier == null || tier == DEFAULT_TIER) return null
        return tiers.get().get<Config?>(tier)?.let { TextComponent(it.get(DISPLAY_NAME)) }
                ?: TextComponent("ERROR").withStyle(ChatFormatting.RED)
    }

    fun getCrafterEnergyUsage(tier: String?): Int {
        return getTier(tier).get(CRAFTER_ENERGY_USAGE)
    }

    fun getPatternsEnergyUsage(tier: String?): Int {
        return getTier(tier).get(PATTERNS_ENERGY_USAGE)
    }

    fun getUpgradesEnergyMultiplier(tier: String?): Double {
        return getTier(tier).get(UPGRADES_ENERGY_MULTIPLIER)
    }

    fun getUpdateInterval(tier: String?, upgrades: Int): Int {
        return getTier(tier).get<List<Int>>(UPDATE_INTERVALS)[upgrades.coerceIn(0, 4)]
    }

    fun getMaximumSuccessfulCraftingUpdates(tier: String?, upgrades: Int): Int {
        return getTier(tier).get<List<Int>>(MAXIMUM_SUCCESSFUL_CRAFTING_UPDATES)[
                upgrades.coerceIn(0, 4)]
    }
}
