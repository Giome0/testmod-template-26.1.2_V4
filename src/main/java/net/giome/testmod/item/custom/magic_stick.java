package net.giome.testmod.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class magic_stick extends Item {

    public magic_stick(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        // Exécuter l'explosion uniquement côté serveur
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            var pos = context.getClickedPos();

            // Créer une explosion au centre du bloc visé
            serverLevel.explode(
                    null,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    4.0f,
                    Level.ExplosionInteraction.TNT
            );
        }

        return InteractionResult.SUCCESS;
    }
}