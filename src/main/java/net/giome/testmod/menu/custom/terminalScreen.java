package net.giome.testmod.menu.custom;

import net.giome.testmod.TestMod;
import net.giome.testmod.client.ImageButton;
import net.giome.testmod.network.ExplodePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.client.gui.components.Button;

public class terminalScreen extends AbstractContainerScreen<terminalMenu> {
    private static final Identifier GUI_TEXTURE =
            Identifier.fromNamespaceAndPath(TestMod.MODID, "textures/gui/terminal/terminal_gui.png");

    public terminalScreen(terminalMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 176, 200);
        this.inventoryLabelY = 80;
    }

    Button explodeButton;

    @Override
    protected void init() {
        super.init();
        explodeButton = addRenderableWidget(new ImageButton(
                leftPos + 180, topPos + 35, 30, 30,
                Identifier.fromNamespaceAndPath(TestMod.MODID, "textures/gui/terminal/bouton.png"),
                Identifier.fromNamespaceAndPath(TestMod.MODID, "textures/gui/terminal/bouton_appuye.png"),
                button -> {
                    Minecraft.getInstance().getConnection().send(new ExplodePacket(menu.blockEntity.getBlockPos()));
                    button.setFocused(false);
                }
        ));
    }


    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);

        ItemStack stack = menu.slots.get(36).getItem();
        if (!stack.isEmpty()) {
            CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            if (!data.isEmpty()) {
                CompoundTag tag = data.copyTag();
                if (tag.contains("x")) {
                    int bx = tag.getInt("x").orElse(0);
                    int by = tag.getInt("y").orElse(0);
                    int bz = tag.getInt("z").orElse(0);
                    graphics.pose().pushMatrix();
                    graphics.pose().scale(0.8f, 0.8f);
                    graphics.text(font, "X: " + bx + " Y: " + by + " Z: " + bz, 110, 92, 0xFF0FFF00, false);
                    graphics.pose().popMatrix();
                }
            }
        }
    }



    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, GUI_TEXTURE, x, y, 0, 0,
                imageWidth, imageHeight, 256, 256);
    }
}
