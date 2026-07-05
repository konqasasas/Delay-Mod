package com.konqasasas;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;

public enum DelayColor {
    BLACK("black", ChatFormatting.BLACK),
    DARK_BLUE("dark_blue", ChatFormatting.DARK_BLUE),
    DARK_GREEN("dark_green", ChatFormatting.DARK_GREEN),
    DARK_AQUA("dark_aqua", ChatFormatting.DARK_AQUA),
    DARK_RED("dark_red", ChatFormatting.DARK_RED),
    DARK_PURPLE("dark_purple", ChatFormatting.DARK_PURPLE),
    GOLD("gold", ChatFormatting.GOLD),
    GRAY("gray", ChatFormatting.GRAY),
    DARK_GRAY("dark_gray", ChatFormatting.DARK_GRAY),
    BLUE("blue", ChatFormatting.BLUE),
    GREEN("green", ChatFormatting.GREEN),
    AQUA("aqua", ChatFormatting.AQUA),
    RED("red", ChatFormatting.RED),
    LIGHT_PURPLE("light_purple", ChatFormatting.LIGHT_PURPLE),
    YELLOW("yellow", ChatFormatting.YELLOW),
    WHITE("white", ChatFormatting.WHITE);

    private static final String NAMES = Arrays.stream(values())
        .map(DelayColor::serializedName)
        .collect(Collectors.joining(", "));

    private final String serializedName;
    private final ChatFormatting formatting;

    DelayColor(String serializedName, ChatFormatting formatting) {
        this.serializedName = serializedName;
        this.formatting = formatting;
    }

    public String serializedName() {
        return this.serializedName;
    }

    public ChatFormatting formatting() {
        return this.formatting;
    }

    public int colorValue() {
        Integer value = this.formatting.getColor();
        int rgb = value == null ? 0xFFFFFF : value;
        return 0xFF000000 | rgb;
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
