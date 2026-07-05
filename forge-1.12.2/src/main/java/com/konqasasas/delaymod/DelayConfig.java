package com.konqasasas.delaymod;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import net.minecraft.client.Minecraft;

public class DelayConfig {
    private static final String KEY_HUD_X = "hudX";
    private static final String KEY_HUD_Y = "hudY";
    private static final String KEY_LABEL_TEXT = "labelText";
    private static final String KEY_VALUE_TEMPLATE = "valueTemplate";
    private static final String KEY_LABEL_COLOR = "labelColor";
    private static final String KEY_VALUE_COLOR = "valueColor";

    public int hudX = 8;
    public int hudY = 8;
    public String labelText = "Delay:";
    public String valueTemplate = "{n}";
    public DelayColor labelColor = DelayColor.GOLD;
    public DelayColor valueColor = DelayColor.WHITE;

    public void load() {
        File file = this.getConfigFile();
        if (!file.exists()) {
            return;
        }

        Properties props = new Properties();
        try (FileInputStream stream = new FileInputStream(file)) {
            props.load(stream);
            this.hudX = parseInt(props.getProperty(KEY_HUD_X), 8);
            this.hudY = parseInt(props.getProperty(KEY_HUD_Y), 8);
            this.labelText = parseString(props.getProperty(KEY_LABEL_TEXT), "Delay:");
            this.valueTemplate = parseValueTemplate(props.getProperty(KEY_VALUE_TEMPLATE), "{n}");
            this.labelColor = DelayColor.fromName(props.getProperty(KEY_LABEL_COLOR)).orElse(DelayColor.GOLD);
            this.valueColor = DelayColor.fromName(props.getProperty(KEY_VALUE_COLOR)).orElse(DelayColor.WHITE);
        } catch (IOException ignored) {
        }
    }

    public void save() {
        File file = this.getConfigFile();
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            return;
        }

        Properties props = new Properties();
        props.setProperty(KEY_HUD_X, Integer.toString(Math.max(0, this.hudX)));
        props.setProperty(KEY_HUD_Y, Integer.toString(Math.max(0, this.hudY)));
        props.setProperty(KEY_LABEL_TEXT, parseString(this.labelText, "Delay:"));
        props.setProperty(KEY_VALUE_TEMPLATE, parseValueTemplate(this.valueTemplate, "{n}"));
        props.setProperty(KEY_LABEL_COLOR, (this.labelColor == null ? DelayColor.GOLD : this.labelColor).serializedName());
        props.setProperty(KEY_VALUE_COLOR, (this.valueColor == null ? DelayColor.WHITE : this.valueColor).serializedName());

        try (FileOutputStream stream = new FileOutputStream(file)) {
            props.store(stream, "Delay Mod config");
        } catch (IOException ignored) {
        }
    }

    private File getConfigFile() {
        return new File(Minecraft.getMinecraft().mcDataDir, "config/delaymod.cfg");
    }

    private static int parseInt(String value, int fallback) {
        if (value == null) {
            return fallback;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static String parseString(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        return value;
    }

    private static String parseValueTemplate(String value, String fallback) {
        if (value == null || value.isEmpty() || !value.contains("{n}")) {
            return fallback;
        }
        return value;
    }
}
