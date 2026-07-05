package com.konqasasas;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class DelayPositionEditorScreen extends Screen {
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    public DelayPositionEditorScreen() {
        super(Component.literal("Delay HUD Editor"));
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float deltaTicks) {
        Minecraft client = Minecraft.getInstance();
        Component label = DelayModClient.getLabelPreviewText();
        Component value = DelayModClient.getValuePreviewText();

        int x = DelayModClient.getHudX();
        int y = DelayModClient.getHudY();
        int labelWidth = client.font.width(label);
        int totalWidth = DelayModClient.getHudTextWidth(client);
        int totalHeight = DelayModClient.getHudTextHeight(client);

        context.fill(x - 2, y - 2, x + totalWidth + 2, y + totalHeight + 2, 0x22000000);
        context.renderOutline(x - 2, y - 2, totalWidth + 4, totalHeight + 4, 0xFFFFFFFF);
        context.drawString(client.font, label, x, y, 0xFFFFFFFF, false);
        context.drawString(client.font, value, x + labelWidth + (labelWidth > 0 ? 4 : 0), y, 0xFFFFFFFF, false);

        context.drawCenteredString(client.font, Component.literal("Delay HUD Edit Mode"), this.width / 2, 20, 0xFFFFFF);
        context.drawCenteredString(client.font, Component.literal("Drag to move. Arrow keys: 1px. Esc: close"), this.width / 2, 36, 0xCCCCCC);

        super.render(context, mouseX, mouseY, deltaTicks);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() != 0) {
            return false;
        }

        Minecraft client = Minecraft.getInstance();
        int hudX = DelayModClient.getHudX();
        int hudY = DelayModClient.getHudY();
        int hudWidth = DelayModClient.getHudTextWidth(client);
        int hudHeight = DelayModClient.getHudTextHeight(client);

        boolean inside = event.x() >= hudX && event.x() <= hudX + hudWidth && event.y() >= hudY && event.y() <= hudY + hudHeight;
        if (inside) {
            this.dragging = true;
            this.dragOffsetX = (int) event.x() - hudX;
            this.dragOffsetY = (int) event.y() - hudY;
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (!this.dragging || event.button() != 0) {
            return false;
        }

        int nextX = (int) event.x() - this.dragOffsetX;
        int nextY = (int) event.y() - this.dragOffsetY;
        DelayModClient.setHudPosition(Minecraft.getInstance(), nextX, nextY);
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (event.button() == 0 && this.dragging) {
            this.dragging = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        Minecraft client = Minecraft.getInstance();
        int currentX = DelayModClient.getHudX();
        int currentY = DelayModClient.getHudY();

        switch (event.key()) {
            case GLFW.GLFW_KEY_LEFT -> {
                DelayModClient.setHudPosition(client, currentX - 1, currentY);
                return true;
            }
            case GLFW.GLFW_KEY_RIGHT -> {
                DelayModClient.setHudPosition(client, currentX + 1, currentY);
                return true;
            }
            case GLFW.GLFW_KEY_UP -> {
                DelayModClient.setHudPosition(client, currentX, currentY - 1);
                return true;
            }
            case GLFW.GLFW_KEY_DOWN -> {
                DelayModClient.setHudPosition(client, currentX, currentY + 1);
                return true;
            }
            default -> {
                return super.keyPressed(event);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
