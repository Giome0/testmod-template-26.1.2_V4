package net.giome.testmod.creativemodtab;

import net.giome.testmod.TestMod;
import net.giome.testmod.block.ModBlocks;
import net.giome.testmod.item.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TestMod.MODID);

    public static final Supplier<CreativeModeTab> RESONITE_ITEM_TAB = CREATIVE_MODE_TABS.register("resonite_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.RESONITE_INGOT.get()))
                    .title(Component.translatable("creativetab.testmod.resonite_items"))
                    .withTabsAfter(Identifier.fromNamespaceAndPath(TestMod.MODID, "resonite_blocks_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.RESONITE_INGOT);
                        output.accept(ModItems.RAW_RESONITE);
                        output.accept(ModItems.RESONATOR);
                        output.accept(ModItems.ENCAPSULATED_RESONITE);
                        output.accept(ModItems.SCREEN);
                        output.accept(ModItems.IRON_ROD);
                        output.accept(ModItems.MAGIC_STICK);


                    }).build());

    public static final Supplier<CreativeModeTab> MISSILS_ITEM_TAB = CREATIVE_MODE_TABS.register("resonite_missils_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.MISSIL.get()))
                    .title(Component.translatable("creativetab.testmod.resonite_missils"))
                    .withTabsBefore(Identifier.fromNamespaceAndPath(TestMod.MODID, "resonite_items_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.MISSIL);
                        output.accept(ModItems.RESONITE_MISSIL);
                        output.accept(ModItems.REFINED_RESONITE_MISSIL);


                    }).build());

    public static final Supplier<CreativeModeTab> RESONITE_BLOCKS_TAB = CREATIVE_MODE_TABS.register("resonite_blocks_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.RESONITE_BLOCK.get()))
                    .title(Component.translatable("creativetab.testmod.resonite_blocks"))
                    .withTabsBefore(Identifier.fromNamespaceAndPath(TestMod.MODID, "resonite_missils_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModBlocks.RESONITE_BLOCK);
                        output.accept(ModBlocks.RAW_RESONITE_BLOCK);
                        output.accept(ModBlocks.RESONITE_ORE);
                        output.accept(ModBlocks.DEEPSLATE_RESONITE_ORE);
                        output.accept(ModBlocks.TERMINAL);

                    }).build());

    public static final Supplier<CreativeModeTab> SACRED_ITEM_TAB = CREATIVE_MODE_TABS.register("sacred_item",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.BLANK_CODEX.get()))
                    .title(Component.translatable("creativetab.testmod.sacred_item"))
                    .withTabsBefore(Identifier.fromNamespaceAndPath(TestMod.MODID, "scred_item_tab"))
                    .displayItems((itemDisplayParameters, output) -> {
                                output.accept(ModItems.CURSE_CODEX.get());
                                output.accept(ModItems.BLANK_CODEX.get());
                                output.accept(ModItems.HOLY_CODEX.get());
                                output.accept(ModItems.BURNED_CODEX.get());
                            })
                            .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
