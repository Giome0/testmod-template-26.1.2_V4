package net.giome.testmod.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;
import java.util.function.Consumer;


public class encapsulated_resonite extends Item {

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide()) {
            BlockPos pos = context.getClickedPos();
            ItemStack stack = context.getItemInHand();

            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(
                    new CompoundTag()
            ));

            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.putInt("x", pos.getX());
            tag.putInt("y", pos.getY());
            tag.putInt("z", pos.getZ());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (!data.isEmpty()) {
            CompoundTag tag = data.copyTag();
            if (tag.contains("x")) {
                int x = tag.getInt("x").orElse(0);
                int y = tag.getInt("y").orElse(0);
                int z = tag.getInt("z").orElse(0);
                consumer.accept(Component.literal("X: " + x + " Y: " + y + " Z: " + z));
            }
        }
    }

    public encapsulated_resonite(Properties properties) {
        super(properties);
    }
}
