package net.giome.testmod.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class magic_stick extends Item {

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);
            CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);

            if (!data.isEmpty()) {
                CompoundTag tag = data.copyTag();
                if (tag.contains("x") && tag.contains("power") && tag.contains("delay")) {
                    int x = tag.getInt("x").orElse(0);
                    int y = tag.getInt("y").orElse(0);
                    int z = tag.getInt("z").orElse(0);
                    float power = tag.getFloat("power").orElse(10f);
                    int delay = tag.getInt("delay").orElse(60);

                    net.giome.testmod.network.DelayedExplosion.schedule(
                            (net.minecraft.server.level.ServerLevel) level,
                            x + 0.5, y, z + 0.5, power, delay
                    );
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        return use(context.getLevel(), context.getPlayer(), context.getHand());
    }

    public magic_stick(Properties properties) {
        super(properties);
    }
}
