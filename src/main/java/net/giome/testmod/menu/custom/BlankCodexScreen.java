package net.giome.testmod.menu.custom;

import net.giome.testmod.TestMod;
import net.giome.testmod.effect.ModEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

import java.util.ArrayList;
import java.util.List;

public class BlankCodexScreen extends Screen {

    private static final Identifier BACKGROUND =
            Identifier.fromNamespaceAndPath(
                    TestMod.MODID,
                    "textures/gui/blank_codex/blank_codex_gui.png"
            );

    private static final int GUI_WIDTH = 300;
    private static final int GUI_HEIGHT = 357;

    private static final int TEXTURE_WIDTH = 367;
    private static final int TEXTURE_HEIGHT = 357;

    private static final int ICON_SIZE = 30;
    private static final int ICON_GAP = 60;
    private static final int ICONS_X = 50;
    private static final int ICONS_Y = 60;
    private static final int ICONS_PER_ROW = 3;

    private int leftPos;
    private int topPos;

    private record EffectIcon(
            Holder<MobEffect> effect,
            Identifier texture
    ) {}

    private static EffectIcon createIcon(
            Holder<MobEffect> effect,
            String name
    ) {
        return new EffectIcon(
                effect,
                Identifier.fromNamespaceAndPath(
                        TestMod.MODID,
                        "textures/mob_effect/" + name + ".png"
                )
        );
    }

    private static final List<EffectIcon> ALL_EFFECTS = List.of(
            createIcon(ModEffect.CURSE_OF_DEATH, "curse_of_death"),
            createIcon(ModEffect.CURSE_OF_HATRED, "curse_of_hatred"),
            createIcon(ModEffect.CURSE_OF_LONELINESS, "curse_of_loneliness"),
            createIcon(ModEffect.CURSE_OF_DISGUST, "curse_of_disgust"),
            createIcon(ModEffect.CURSE_OF_JUDGING, "curse_of_judging"),
            createIcon(ModEffect.BLESSING_OF_LOVE, "blessing_of_love"),
            createIcon(ModEffect.BLESSING_OF_THE_MOON, "blessing_of_the_moon"),
            createIcon(ModEffect.BLESSING_OF_THE_SUN, "blessing_of_the_sun"),
            createIcon(ModEffect.BLESSING_OF_REBIRTH, "blessing_of_rebirth")
    );

    public BlankCodexScreen() {
        super(Component.translatable("item.testmod.blank_codex"));
    }

    @Override
    protected void init() {
        super.init();

        leftPos = (width - GUI_WIDTH) / 2;
        topPos = (height - GUI_HEIGHT) / 2;
    }

    private List<EffectIcon> getActiveIcons() {
        var player = Minecraft.getInstance().player;

        if (player == null) {
            return List.of();
        }

        List<EffectIcon> active = new ArrayList<>();

        for (EffectIcon icon : ALL_EFFECTS) {
            if (player.hasEffect(icon.effect())) {
                active.add(icon);
            }
        }

        return active;
    }

    private int getIconX(int index) {
        return leftPos + ICONS_X
                + (index % ICONS_PER_ROW) * ICON_GAP;
    }

    private int getIconY(int index) {
        return topPos + ICONS_Y
                + (index / ICONS_PER_ROW) * ICON_GAP;
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

        // Fond du codex
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

        // Icônes des effets actifs
        List<EffectIcon> activeIcons = getActiveIcons();

        for (int i = 0; i < activeIcons.size(); i++) {
            EffectIcon icon = activeIcons.get(i);

            int x = getIconX(i);
            int y = getIconY(i);

            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    icon.texture(),
                    x,
                    y,
                    0,
                    0,
                    ICON_SIZE,
                    ICON_SIZE,
                    ICON_SIZE,
                    ICON_SIZE
            );

            // Contour noir au survol
            boolean hovered =
                    mouseX >= x
                            && mouseX < x + ICON_SIZE
                            && mouseY >= y
                            && mouseY < y + ICON_SIZE;

            if (hovered) {
                graphics.outline(
                        x - 1,
                        y - 1,
                        ICON_SIZE + 2,
                        ICON_SIZE + 2,
                        0xFF000000
                );
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
