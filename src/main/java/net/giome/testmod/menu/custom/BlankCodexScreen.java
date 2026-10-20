package net.giome.testmod.menu.custom;

import net.giome.testmod.TestMod;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Component;

public class BlankCodexScreen extends Screen {

    private static final Identifier BACKGROUND =
            Identifier.fromNamespaceAndPath(
                    TestMod.MODID,
                    "textures/gui/blank_codex/blank_codex_GUI.png"
            );

    // Taille d'affichage de l'interface.
    // À ajuster selon ta texture.
    private static final int GUI_WIDTH = 220;
    private static final int GUI_HEIGHT = 180;

    // Dimensions réelles du PNG source :
    // remplace ces valeurs par celles de ton fichier.
    private static final int TEXTURE_WIDTH = 367;
    private static final int TEXTURE_HEIGHT = 357;

    private int leftPos;
    private int topPos;

    public BlankCodexScreen() {
        super(Component.translatable("item.testmod.blank_codex"));
    }

    @Override
    protected void init() {
        super.init();

        leftPos = (this.width - GUI_WIDTH) / 2;
        topPos = (this.height - GUI_HEIGHT) / 2;
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        super.extractRenderState(
                graphics, mouseX, mouseY, partialTick
        );

        // Dessiner le fond personnalisé.
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND,
                leftPos,
                topPos,
                0,
                0,
                GUI_WIDTH,
                GUI_HEIGHT,
                TEXTURE_WIDTH,
                TEXTURE_HEIGHT
        );

        // Exemple de case interactive.
        int slotX = leftPos + 30;
        int slotY = topPos + 40;
        int slotSize = 18;

        boolean hovered =
                mouseX >= slotX
                        && mouseX < slotX + slotSize
                        && mouseY >= slotY
                        && mouseY < slotY + slotSize;

        if (hovered) {
            graphics.outline(
                    slotX,
                    slotY,
                    slotSize,
                    slotSize,
                    0xFFFFFFFF
            );
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
