package net.giome.testmod.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class ImageButton extends Button {
    private final Identifier texture;
    private final Identifier texturePressed;
    private boolean pressed = false;

    public ImageButton(int x, int y, int width, int height, Identifier texture, Identifier texturePressed, OnPress onPress) {
        super(x, y, width, height, net.minecraft.network.chat.Component.empty(), onPress, DEFAULT_NARRATION);
        this.texture = texture;
        this.texturePressed = texturePressed;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        pressed = true;
        super.onPress(input);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        pressed = false;
        return super.mouseReleased(event);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        Identifier tex = pressed ? texturePressed : texture;
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, getX(), getY(), 0, 0, width, height, width, height);
    }
}
