package net.giome.testmod.datagen;

import net.giome.testmod.TestMod;
import net.giome.testmod.block.ModBlocks;
import net.giome.testmod.block.custom.terminal;
import net.giome.testmod.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import org.jline.terminal.Terminal;

import static net.giome.testmod.block.custom.terminal.ACTIVATED;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, TestMod.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        itemModels.generateFlatItem(ModItems.RESONITE_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.RAW_RESONITE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.RESONATOR.get(), ModelTemplates.FLAT_ITEM);
        //itemModels.generateFlatItem(ModItems.ENCAPSULATED_RESONITE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.SCREEN.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.IRON_ROD.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.MAGIC_STICK.get(), ModelTemplates.FLAT_ITEM);

        itemModels.itemModelOutput.accept(
                ModItems.ENCAPSULATED_RESONITE.get(),
                ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(TestMod.MODID, "item/encapsulated_resonite"))
        );

        /* MISSILS */

        itemModels.itemModelOutput.accept(ModItems.MISSIL.get(), ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(TestMod.MODID, "item/missil"))
        );

        itemModels.itemModelOutput.accept(ModItems.RESONITE_MISSIL.get(), ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(TestMod.MODID, "item/resonite_missil"))
        );

        itemModels.itemModelOutput.accept(ModItems.REFINED_RESONITE_MISSIL.get(), ItemModelUtils.plainModel(Identifier.fromNamespaceAndPath(TestMod.MODID, "item/refined_resonite_missil"))
        );

        /* CODEX */

        itemModels.generateFlatItem(ModItems.CURSE_CODEX.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.BLANK_CODEX.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.HOLY_CODEX.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.BURNED_CODEX.get(), ModelTemplates.FLAT_ITEM);

        /* BLOCKS */

        blockModels.createTrivialCube(ModBlocks.RESONITE_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.RAW_RESONITE_BLOCK.get());
        blockModels.createTrivialCube(ModBlocks.RESONITE_ORE.get());
        blockModels.createTrivialCube(ModBlocks.DEEPSLATE_RESONITE_ORE.get());

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ModBlocks.TERMINAL.get())
                        .with(PropertyDispatch.initial(ACTIVATED)
                                .select(true, BlockModelGenerators.plainVariant(
                                        Identifier.fromNamespaceAndPath(TestMod.MODID, "block/terminal_on")))
                                .select(false, BlockModelGenerators.plainVariant(
                                        Identifier.fromNamespaceAndPath(TestMod.MODID, "block/terminal"))))
        );
    }
}
