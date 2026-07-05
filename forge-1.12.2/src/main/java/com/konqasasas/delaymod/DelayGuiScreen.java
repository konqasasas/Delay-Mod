package com.konqasasas.delaymod;

import java.io.IOException;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

public class DelayGuiScreen extends GuiScreen {
    private final DelayState state;
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;

    public DelayGuiScreen(DelayState state) {
        this.state = state;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int x = this.state.getHudX();
        int y = this.state.getHudY();
        String label = this.state.getLabelText();
        String value = this.state.getValueText();

        int labelWidth = this.fontRenderer.getStringWidth(label);
        int totalWidth = this.state.getHudWidth();
        int totalHeight = this.state.getHudHeight();

        drawRect(x - 2, y - 2, x + totalWidth + 2, y + totalHeight + 2, 0x22000000);
        drawHorizontalLine(x - 2, x + totalWidth + 1, y - 2, 0xFFFFFFFF);
        drawHorizontalLine(x - 2, x + totalWidth + 1, y + totalHeight + 2, 0xFFFFFFFF);
        drawVerticalLine(x - 2, y - 2, y + totalHeight + 2, 0xFFFFFFFF);
        drawVerticalLine(x + totalWidth + 2, y - 2, y + totalHeight + 2, 0xFFFFFFFF);

        this.fontRenderer.drawString(label, x, y, 0xFFFFFF);
        this.fontRenderer.drawString(value, x + labelWidth + (labelWidth > 0 ? 4 : 0), y, 0xFFFFFF);

        drawCenteredString(this.fontRenderer, "Delay HUD Edit Mode", this.width / 2, 20, 0xFFFFFF);
        drawCenteredString(this.fontRenderer, "Drag to move. Arrow keys: 1px. Esc: close", this.width / 2, 34, 0xCCCCCC);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton != 0) {
            return;
        }

        int hudX = this.state.getHudX();
        int hudY = this.state.getHudY();
        int hudWidth = this.state.getHudWidth();
        int hudHeight = this.state.getHudHeight();

        boolean inside = mouseX >= hudX && mouseX <= hudX + hudWidth && mouseY >= hudY && mouseY <= hudY + hudHeight;
        if (inside) {
            this.dragging = true;
            this.dragOffsetX = mouseX - hudX;
            this.dragOffsetY = mouseY - hudY;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

        if (!this.dragging || clickedMouseButton != 0) {
            return;
        }

        this.state.setHudPosition(mouseX - this.dragOffsetX, mouseY - this.dragOffsetY, this.width, this.height);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (state == 0) {
            this.dragging = false;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        switch (keyCode) {
            case Keyboard.KEY_LEFT:
                this.state.moveHud(-1, 0, this.width, this.height);
                return;
            case Keyboard.KEY_RIGHT:
                this.state.moveHud(1, 0, this.width, this.height);
                return;
            case Keyboard.KEY_UP:
                this.state.moveHud(0, -1, this.width, this.height);
                return;
            case Keyboard.KEY_DOWN:
                this.state.moveHud(0, 1, this.width, this.height);
                return;
            case Keyboard.KEY_ESCAPE:
                this.mc.displayGuiScreen(null);
                return;
            default:
                super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
