package com.konqasasas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class DelayConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("delay-mod.json");

    public int hudX = 8;
    public int hudY = 8;
    public String labelText = "Delay:";
    public String valueTemplate = "{n}";
    public DelayColor labelColor = DelayColor.GOLD;
    public DelayColor valueColor = DelayColor.WHITE;

    public static DelayConfig load() {
        if (!Files.exists(CONFIG_PATH)) {
            return new DelayConfig();
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            DelayConfig loaded = GSON.fromJson(reader, DelayConfig.class);
            if (loaded == null) {
                return new DelayConfig();
            }
            loaded.normalize();
            return loaded;
        } catch (IOException | JsonSyntaxException e) {
            DelayMod.LOGGER.warn("Failed to load delay-mod config, using defaults", e);
            return new DelayConfig();
        }
    }

    public void save() {
        this.normalize();
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(this, writer);
            }
        } catch (IOException e) {
            DelayMod.LOGGER.warn("Failed to save delay-mod config", e);
        }
    }

    private void normalize() {
        if (this.labelText == null) {
            this.labelText = "Delay:";
        }
        if (this.valueTemplate == null || this.valueTemplate.isBlank() || !this.valueTemplate.contains("{n}")) {
            this.valueTemplate = "{n}";
        }
        if (this.labelColor == null) {
            this.labelColor = DelayColor.GOLD;
        }
        if (this.valueColor == null) {
            this.valueColor = DelayColor.WHITE;
        }
        this.hudX = Math.max(0, this.hudX);
        this.hudY = Math.max(0, this.hudY);
    }
}
