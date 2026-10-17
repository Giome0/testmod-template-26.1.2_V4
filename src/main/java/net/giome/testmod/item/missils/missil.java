package net.giome.testmod.item.missils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;

public class missil extends Item implements IMissil {
    private final float power;
    private final int delay;

    public missil(Properties properties, float power, int delay) {
        super(properties);
        this.power = power;
        this.delay = delay;
    }

    @Override
    public float getPower() { return power; }

    @Override
    public int getDelay() { return delay; }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide()) {
            BlockPos pos = context.getClickedPos();
            ItemStack stack = context.getItemInHand();

            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.putInt("x", pos.getX());
            tag.putInt("y", pos.getY());
            tag.putInt("z", pos.getZ());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return InteractionResult.SUCCESS;
    }
}
