package com.konqasasas.delaymod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.IClientCommand;

public class DelayClientCommand extends CommandBase implements IClientCommand {
    private final DelayState state;

    public DelayClientCommand(DelayState state) {
        this.state = state;
    }

    @Override
    public String getName() {
        return "delay";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/delay <gui|text|color>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(new TextComponentString("Usage: " + this.getUsage(sender)));
            return;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "gui":
                this.state.requestOpenEditor();
                sender.sendMessage(new TextComponentString("Opened delay HUD editor."));
                return;
            case "text":
                this.handleText(sender, args);
                return;
            case "color":
                this.handleColor(sender, args);
                return;
            default:
                sender.sendMessage(new TextComponentString("Unknown subcommand. Usage: " + this.getUsage(sender)));
        }
    }

    private void handleText(ICommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(new TextComponentString("Usage: /delay text <label|value> <text>"));
            return;
        }

        String target = args[1].toLowerCase(Locale.ROOT);
        String value = joinArgs(args, 2);

        if ("label".equals(target)) {
            this.state.setLabelText(value);
            sender.sendMessage(new TextComponentString("Label text updated."));
            return;
        }
        if ("value".equals(target)) {
            if (!this.state.setValueTemplate(value)) {
                sender.sendMessage(new TextComponentString("Value template must include {n}."));
                return;
            }
            sender.sendMessage(new TextComponentString("Value template updated."));
            return;
        }

        sender.sendMessage(new TextComponentString("Usage: /delay text <label|value> <text>"));
    }

    private void handleColor(ICommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(new TextComponentString("Usage: /delay color <label|value> <color16>"));
            return;
        }

        String target = args[1].toLowerCase(Locale.ROOT);
        DelayColor color = DelayColor.fromName(args[2]).orElse(null);
        if (color == null) {
            sender.sendMessage(new TextComponentString("Unknown color. Valid colors: " + DelayColor.validNames()));
            return;
        }

        if ("label".equals(target)) {
            this.state.setLabelColor(color);
            sender.sendMessage(new TextComponentString("Label color updated to " + color.serializedName() + "."));
            return;
        }
        if ("value".equals(target)) {
            this.state.setValueColor(color);
            sender.sendMessage(new TextComponentString("Value color updated to " + color.serializedName() + "."));
            return;
        }

        sender.sendMessage(new TextComponentString("Usage: /delay color <label|value> <color16>"));
    }

    private static String joinArgs(String[] args, int startInclusive) {
        StringBuilder builder = new StringBuilder();
        for (int i = startInclusive; i < args.length; i++) {
            if (i > startInclusive) {
                builder.append(' ');
            }
            builder.append(args[i]);
        }
        return builder.toString();
    }

    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> getTabCompletions(
            MinecraftServer server,
            ICommandSender sender,
            String[] args,
            @Nullable BlockPos targetPos
    ) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, Arrays.asList("gui", "text", "color"));
        }
        if (args.length == 2 && "text".equalsIgnoreCase(args[0])) {
            return getListOfStringsMatchingLastWord(args, Arrays.asList("label", "value"));
        }
        if (args.length == 2 && "color".equalsIgnoreCase(args[0])) {
            return getListOfStringsMatchingLastWord(args, Arrays.asList("label", "value"));
        }
        if (args.length == 3 && "color".equalsIgnoreCase(args[0])) {
            return getListOfStringsMatchingLastWord(args, Arrays.stream(DelayColor.values()).map(DelayColor::serializedName).toArray(String[]::new));
        }
        return Collections.emptyList();
    }
}
