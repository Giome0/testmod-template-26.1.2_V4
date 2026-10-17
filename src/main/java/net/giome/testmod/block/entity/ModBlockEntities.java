package net.giome.testmod.block.entity;

import net.giome.testmod.TestMod;
import net.giome.testmod.block.ModBlocks;
import net.giome.testmod.block.entity.custom.terminalEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, TestMod.MODID);

    public static final Supplier<BlockEntityType<terminalEntity>> TERMINAL_BE =
            BLOCK_ENTITIES.register("terminal_be", () -> new BlockEntityType<>(
                    terminalEntity::new, ModBlocks.TERMINAL.get()));


    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }

}
