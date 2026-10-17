package net.giome.testmod.datagen;

import net.giome.testmod.TestMod;
import net.giome.testmod.block.ModBlocks;
import net.giome.testmod.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "";
        }
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, ModBlocks.RESONITE_BLOCK.get())
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.RESONITE_INGOT.get())
                .unlockedBy(getHasName(ModItems.RESONITE_INGOT.get()), has(ModItems.RESONITE_INGOT))
                .group("resonite")
                .save(output);

        shapeless(RecipeCategory.MISC, ModItems.RESONITE_INGOT.get(), 9)
                .requires(ModBlocks.RESONITE_BLOCK)
                .unlockedBy(getHasName(ModBlocks.RESONITE_BLOCK.get()), has(ModBlocks.RESONITE_BLOCK))
                .group("resonite")
                .save(output);

        shapeless(RecipeCategory.MISC, ModItems.RESONITE_INGOT.get(), 10)
                .requires(ModBlocks.RESONITE_BLOCK)
                .requires(Items.NETHER_STAR)
                .unlockedBy(getHasName(ModBlocks.RESONITE_BLOCK.get()), has(ModBlocks.RESONITE_BLOCK))
                .group("resonite")
                .save(output, "testmod:resonite_form_nether_star");


        shaped(RecipeCategory.MISC, ModBlocks.RAW_RESONITE_BLOCK.get())
                .pattern("AAA")
                .pattern("AAA")
                .pattern("AAA")
                .define('A', ModItems.RAW_RESONITE.get())
                .unlockedBy(getHasName(ModItems.RAW_RESONITE.get()), has(ModItems.RAW_RESONITE))
                .group("resonite")
                .save(output);

        shapeless(RecipeCategory.MISC, ModItems.RAW_RESONITE.get(), 9)
                .requires(ModBlocks.RAW_RESONITE_BLOCK)
                .unlockedBy(getHasName(ModBlocks.RAW_RESONITE_BLOCK.get()), has(ModBlocks.RAW_RESONITE_BLOCK))
                .group("resonite")
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.ENCAPSULATED_RESONITE.get())
                .pattern("AAA")
                .pattern("ABA")
                .pattern("AAA")
                .define('A', Items.GLASS)
                .define('B', ModItems.RESONITE_INGOT.get())
                .unlockedBy(getHasName(ModItems.RESONITE_INGOT.get()), has(ModItems.RESONITE_INGOT))
                .group("resonite")
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.RESONATOR.get())
                .pattern(" A ")
                .pattern(" B ")
                .pattern("CCC")
                .define('A', ModItems.ENCAPSULATED_RESONITE.get())
                .define('B', Items.STICK)
                .define('C', Items.OAK_SLAB)
                .unlockedBy(getHasName(ModItems.ENCAPSULATED_RESONITE.get()), has(ModItems.ENCAPSULATED_RESONITE))
                .group("resonite")
                .save(output);

        shaped(RecipeCategory.MISC, ModItems.IRON_ROD.get())
                .pattern("  A")
                .pattern(" A ")
                .pattern("A  ")
                .define('A', Items.IRON_INGOT)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .group("resonite")
                .save(output);

        shaped(RecipeCategory.MISC, ModBlocks.TERMINAL.get())
                .pattern(" A ")
                .pattern("CBC")
                .pattern("DDD")
                .define('A', ModItems.SCREEN.get())
                .define('B', Items.NETHER_STAR)
                .define('C', ModItems.IRON_ROD)
                .define('D', Items.IRON_BLOCK)
                .unlockedBy(getHasName(ModItems.SCREEN.get()), has(ModItems.SCREEN))
                .group("resonite")
                .save(output);

        List<ItemLike> RESONITE_ORE_SMELTABLES = List.of(ModItems.RAW_RESONITE, ModBlocks.RESONITE_ORE,
                ModBlocks.DEEPSLATE_RESONITE_ORE);

        oreSmelting(RESONITE_ORE_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.MISC, ModItems.RESONITE_INGOT.get(), 0.25f, 200, "resonite");
        oreBlasting(RESONITE_ORE_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.MISC, ModItems.RESONITE_INGOT.get(), 0.25f, 100, "resonite");

        List<ItemLike> RESONITE_SMELTABLES = List.of(ModItems.RESONITE_INGOT);

        oreBlasting(RESONITE_SMELTABLES, RecipeCategory.MISC, CookingBookCategory.MISC, ModItems.SCREEN.get(), 0f, 300, "resonite");
    }

    @Override
    protected <T extends AbstractCookingRecipe> void oreCooking(AbstractCookingRecipe.Factory<T> factory, List<ItemLike> smeltables,
                                                                RecipeCategory craftingCategory, CookingBookCategory cookingCategory, ItemLike result,
                                                                float experience, int cookingTime, String group, String fromDesc) {
        for (ItemLike itemlike : smeltables) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), craftingCategory, cookingCategory, result, experience, cookingTime, factory).group(group).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(output, TestMod.MODID + ":" + getItemName(result) + fromDesc + "_" + getItemName(itemlike));
        }
    }
}
