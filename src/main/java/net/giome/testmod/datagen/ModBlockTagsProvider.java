package net.giome.testmod.datagen;

import net.giome.testmod.TestMod;
import net.giome.testmod.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, TestMod.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.RESONITE_BLOCK.get())
                .add(ModBlocks.DEEPSLATE_RESONITE_ORE.get())
                .add(ModBlocks.RESONITE_ORE.get())
                .add(ModBlocks.RAW_RESONITE_BLOCK.get())
                .add(ModBlocks.TERMINAL.get());

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.RESONITE_BLOCK.get())
                .add(ModBlocks.DEEPSLATE_RESONITE_ORE.get())
                .add(ModBlocks.RESONITE_ORE.get())
                .add(ModBlocks.RAW_RESONITE_BLOCK.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.TERMINAL.get());
    }
}
