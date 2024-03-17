package dev.stevendoesstuffs.refinedcrafterproxy.generator

import com.refinedmods.refinedstorage.RSItems
import com.refinedmods.refinedstorage.item.ProcessorItem
import dev.stevendoesstuffs.refinedcrafterproxy.Registration
import java.util.function.Consumer
import net.minecraft.data.DataGenerator
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.item.Items

class RecipeGenerator(gen: DataGenerator) : RecipeProvider(gen) {

    override fun buildCraftingRecipes(consumer: Consumer<FinishedRecipe>) {
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
                .define('d', Registration.CRAFTER_PROXY_BLOCK_ITEM)
                .unlockedBy("has_part", has(advancedProcessor))
                .save(consumer)
    }
}
