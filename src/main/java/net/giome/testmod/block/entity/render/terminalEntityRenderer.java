package net.giome.testmod.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.giome.testmod.block.entity.custom.terminalEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class terminalEntityRenderer implements BlockEntityRenderer<terminalEntity, terminalEntityRenderState> {
    private final ItemModelResolver itemModelResolver;

    public terminalEntityRenderer(BlockEntityRendererProvider.Context context) {
        itemModelResolver = context.itemModelResolver();
    }

    @Override
    public terminalEntityRenderState createRenderState() {
        return new terminalEntityRenderState();
    }

    @Override
    public void extractRenderState(terminalEntity blockEntity, terminalEntityRenderState state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        state.level = blockEntity.getLevel();

        itemModelResolver.updateForTopItem(state.itemStackRenderState,
                blockEntity.inventory.getResource(0).toStack(), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
    }

    @Override
    public void submit(terminalEntityRenderState state, PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        //poseStack.pushPose();

        //poseStack.translate(0.5f, 0.3f, 0.5f);
        //poseStack.scale(0.5f, 0.5f, 0.5f);

        //state.itemStackRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        //poseStack.popPose();
    }
}
