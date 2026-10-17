package net.giome.testmod.item;

import net.giome.testmod.TestMod;
import net.giome.testmod.item.custom.encapsulated_resonite;
import net.giome.testmod.item.custom.magic_stick;
import net.giome.testmod.item.missils.missil;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TestMod.MODID);

    public static final DeferredItem<Item> RESONITE_INGOT = ITEMS.registerSimpleItem("resonite_ingot");

    public static final DeferredItem<Item> RAW_RESONITE = ITEMS.registerSimpleItem("raw_resonite");

    public static final DeferredItem<Item> RESONATOR = ITEMS.registerSimpleItem("resonator");

    public static final DeferredItem<Item> ENCAPSULATED_RESONITE = ITEMS.registerItem("encapsulated_resonite", encapsulated_resonite::new);

    public static final DeferredItem<Item> SCREEN = ITEMS.registerSimpleItem("screen");

    public static final DeferredItem<Item> IRON_ROD = ITEMS.registerSimpleItem("iron_rod");

    public static final DeferredItem<Item> MAGIC_STICK = ITEMS.registerItem("magic_stick", magic_stick::new);

    /* Missils */

    public static final DeferredItem<Item> MISSIL = ITEMS.registerItem("missil",
            p -> new missil(p, 5f, 60));
    public static final DeferredItem<Item> RESONITE_MISSIL = ITEMS.registerItem("resonite_missil",
            p -> new missil(p, 10f, 60));
    public static final DeferredItem<Item> REFINED_RESONITE_MISSIL = ITEMS.registerItem("refined_resonite_missil",
            p -> new missil(p, 30f, 60));

    public static void register(IEventBus eventsBus){
        ITEMS.register(eventsBus);

    }

}
