
package net.giome.testmod.item.custom;

import net.giome.testmod.menu.custom.BlankCodexScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class blank_codex extends Item {

    public blank_codex(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        if (level.isClientSide()) {
            Minecraft.getInstance().setScreen(
                    new BlankCodexScreen()
            );
        }

        return InteractionResult.SUCCESS;
    }
}

