package net.giome.testmod.block.entity.render;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.level.Level;

public class terminalEntityRenderState extends BlockEntityRenderState {
    public Level level;

    public final ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
}
