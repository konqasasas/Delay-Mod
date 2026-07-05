package com.konqasasas.delaymod;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.util.text.TextFormatting;

public enum DelayColor {
    BLACK("black", TextFormatting.BLACK, 0x000000),
    DARK_BLUE("dark_blue", TextFormatting.DARK_BLUE, 0x0000AA),
    DARK_GREEN("dark_green", TextFormatting.DARK_GREEN, 0x00AA00),
    DARK_AQUA("dark_aqua", TextFormatting.DARK_AQUA, 0x00AAAA),
    DARK_RED("dark_red", TextFormatting.DARK_RED, 0xAA0000),
    DARK_PURPLE("dark_purple", TextFormatting.DARK_PURPLE, 0xAA00AA),
    GOLD("gold", TextFormatting.GOLD, 0xFFAA00),
    GRAY("gray", TextFormatting.GRAY, 0xAAAAAA),
    DARK_GRAY("dark_gray", TextFormatting.DARK_GRAY, 0x555555),
    BLUE("blue", TextFormatting.BLUE, 0x5555FF),
    GREEN("green", TextFormatting.GREEN, 0x55FF55),
    AQUA("aqua", TextFormatting.AQUA, 0x55FFFF),
    RED("red", TextFormatting.RED, 0xFF5555),
    LIGHT_PURPLE("light_purple", TextFormatting.LIGHT_PURPLE, 0xFF55FF),
    YELLOW("yellow", TextFormatting.YELLOW, 0xFFFF55),
    WHITE("white", TextFormatting.WHITE, 0xFFFFFF);

    private static final String NAMES = Arrays.stream(values())
            .map(DelayColor::serializedName)
            .collect(Collectors.joining(", "));

    private final String serializedName;
    private final TextFormatting formatting;
    private final int rgb;

    DelayColor(String serializedName, TextFormatting formatting, int rgb) {
        this.serializedName = serializedName;
        this.formatting = formatting;
        this.rgb = rgb;
    }

    public String serializedName() {
        return this.serializedName;
    }

    public TextFormatting formatting() {
        return this.formatting;
    }

    public int rgb() {
        return this.rgb;
    }

    public static Optional<DelayColor> fromName(String name) {
        if (name == null) {
            return Optional.empty();
        }

        String normalized = name.toLowerCase(Locale.ROOT);
        for (DelayColor color : values()) {
            if (color.serializedName.equals(normalized)) {
                return Optional.of(color);
            }
        }
        return Optional.empty();
    }

    public static String validNames() {
        return NAMES;
    }
}
