package dev.stevendoesstuffs.refinedcrafterproxy.generator

import com.refinedmods.refinedstorage.RSItems
import com.refinedmods.refinedstorage.item.ProcessorItem
import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import net.minecraft.data.DataGenerator
import net.minecraft.data.IFinishedRecipe
import net.minecraft.data.RecipeProvider
import net.minecraft.data.ShapedRecipeBuilder
import net.minecraft.item.Items
import net.minecraft.tags.ItemTags
import java.util.function.Consumer

class RecipeGenerator(gen: DataGenerator) : RecipeProvider(gen) {

    // definitely not shapeless
    override fun buildShapelessRecipes(consumer: Consumer<IFinishedRecipe>) {
        val improvedProcessor = RSItems.PROCESSORS[ProcessorItem.Type.IMPROVED]!!.get()
        val advancedProcessor = RSItems.PROCESSORS[ProcessorItem.Type.ADVANCED]!!.get()

        ShapedRecipeBuilder.shaped(Registration.CRAFTER_PROXY_CARD)
            .pattern("aba")
            .pattern(" c ")
            .pattern("aba")
            .define('a', RSItems.QUARTZ_ENRICHED_IRON.get())
            .define('b', Items.PAPER)
            .define('c', improvedProcessor)
            .unlockedBy("has_part", has(improvedProcessor))
            .save(consumer)

        ShapedRecipeBuilder.shaped(Registration.CRAFTER_PROXY_BLOCK_ITEM)
            .pattern("a a")
            .pattern("bdc")
            .pattern("a a")
            .define('a', RSItems.QUARTZ_ENRICHED_IRON.get())
            .define('b', improvedProcessor)
            .define('c', advancedProcessor)
            .define('d', ItemTags.bind("refinedstorage:crafter"))
            .unlockedBy("has_part", has(advancedProcessor))
            .save(consumer)
    }
}
