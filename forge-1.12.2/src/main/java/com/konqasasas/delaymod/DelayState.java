package com.konqasasas.delaymod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;

public class DelayState {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final DelayConfig config = new DelayConfig();
    private int groundTicks;
    private boolean wasOnGround;
    private int openEditorRetryTicks;

    public void load() {
        this.config.load();
    }

    public void onClientTick() {
        this.tryOpenEditorScreen();

        EntityPlayerSP player = this.mc.player;
        if (player == null || this.mc.world == null) {
            this.groundTicks = 0;
            this.wasOnGround = false;
            return;
        }

        boolean nowOnGround = player.onGround;
        boolean prevOnGround = this.wasOnGround;
        boolean isAir = !prevOnGround || !nowOnGround;
        boolean isGround = !isAir;
        boolean groundStarted = !prevOnGround && nowOnGround;

        if (groundStarted) {
            this.groundTicks = 0;
        }
        if (isGround) {
            this.groundTicks++;
        }

        this.wasOnGround = nowOnGround;
    }

    public void drawHud() {
        if (this.mc.player == null || this.mc.gameSettings.hideGUI) {
            return;
        }

        String label = this.config.labelText;
        String value = this.getValueText();

        ScaledResolution resolution = new ScaledResolution(this.mc);
        int hudWidth = this.getHudWidth();
        int hudHeight = this.getHudHeight();
        int x = clamp(this.config.hudX, 0, Math.max(0, resolution.getScaledWidth() - hudWidth));
        int y = clamp(this.config.hudY, 0, Math.max(0, resolution.getScaledHeight() - hudHeight));
        int labelWidth = this.mc.fontRenderer.getStringWidth(label);

        this.mc.fontRenderer.drawStringWithShadow(label, x, y, this.config.labelColor.rgb());
        this.mc.fontRenderer.drawStringWithShadow(value, x + labelWidth + (labelWidth > 0 ? 4 : 0), y, this.config.valueColor.rgb());
    }

    public String getValueText() {
        return this.config.valueTemplate.replace("{n}", Integer.toString(this.groundTicks));
    }

    public String getLabelText() {
        return this.config.labelText;
    }

    public int getHudX() {
        return this.config.hudX;
    }

    public int getHudY() {
        return this.config.hudY;
    }

    public int getGroundTicks() {
        return this.groundTicks;
    }

    public int getHudWidth() {
        String label = this.config.labelText;
        int labelWidth = this.mc.fontRenderer.getStringWidth(label);
        return labelWidth + this.mc.fontRenderer.getStringWidth(this.getValueText()) + (labelWidth > 0 ? 4 : 0);
    }

    public int getHudHeight() {
        return this.mc.fontRenderer.FONT_HEIGHT;
    }

    public void setHudPosition(int x, int y, int screenWidth, int screenHeight) {
        this.config.hudX = clamp(x, 0, Math.max(0, screenWidth - this.getHudWidth()));
        this.config.hudY = clamp(y, 0, Math.max(0, screenHeight - this.getHudHeight()));
        this.config.save();
    }

    public void moveHud(int dx, int dy, int screenWidth, int screenHeight) {
        this.setHudPosition(this.config.hudX + dx, this.config.hudY + dy, screenWidth, screenHeight);
    }

    public void setLabelText(String labelText) {
        this.config.labelText = labelText == null ? "Delay:" : labelText;
        this.config.save();
    }

    public boolean setValueTemplate(String valueTemplate) {
        if (valueTemplate == null || valueTemplate.isEmpty() || !valueTemplate.contains("{n}")) {
            return false;
        }
        this.config.valueTemplate = valueTemplate;
        this.config.save();
        return true;
    }

    public void setLabelColor(DelayColor color) {
        this.config.labelColor = color == null ? DelayColor.GOLD : color;
        this.config.save();
    }

    public void setValueColor(DelayColor color) {
        this.config.valueColor = color == null ? DelayColor.WHITE : color;
        this.config.save();
    }

    public DelayColor getLabelColor() {
        return this.config.labelColor;
    }

    public DelayColor getValueColor() {
        return this.config.valueColor;
    }

    public void requestOpenEditor() {
        this.openEditorRetryTicks = 20;
    }

    private void tryOpenEditorScreen() {
        if (this.openEditorRetryTicks <= 0) {
            return;
        }

        if (!(this.mc.currentScreen instanceof DelayGuiScreen)) {
            this.mc.displayGuiScreen(new DelayGuiScreen(this));
        }

        this.openEditorRetryTicks--;
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }
}
