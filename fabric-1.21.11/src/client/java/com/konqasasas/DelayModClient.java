package com.konqasasas;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class DelayModClient implements ClientModInitializer {
	private static DelayConfig config;
	private static int groundTicks;
	private static boolean wasOnGround;

	@Override
	public void onInitializeClient() {
		config = DelayConfig.load();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) {
				groundTicks = 0;
				wasOnGround = false;
				return;
			}

			boolean nowOnGround = client.player.onGround();
			boolean prevOnGround = wasOnGround;
			boolean isAir = !prevOnGround || !nowOnGround;
			boolean isGround = !isAir; // equivalent to prevOnGround && nowOnGround
			boolean groundStarted = !prevOnGround && nowOnGround;

			if (groundStarted) {
				groundTicks = 0;
			}
			if (isGround) {
				groundTicks++;
			}
			wasOnGround = nowOnGround;
		});

		HudRenderCallback.EVENT.register(DelayModClient::renderHud);
		registerCommands();
	}

	private static void renderHud(GuiGraphics drawContext, DeltaTracker renderTickCounter) {
		Minecraft client = Minecraft.getInstance();
		if (client.options.hideGui || client.player == null) {
			return;
		}

		Component label = Component.literal(config.labelText).withStyle(config.labelColor.formatting());
		Component value = Component.literal(resolveValueText()).withStyle(config.valueColor.formatting());

		int labelWidth = client.font.width(label);
		int valueWidth = client.font.width(value);
		int totalWidth = labelWidth + valueWidth + (labelWidth > 0 ? 4 : 0);
		int totalHeight = client.font.lineHeight;

		int x = clamp(config.hudX, 0, Math.max(0, drawContext.guiWidth() - totalWidth));
		int y = clamp(config.hudY, 0, Math.max(0, drawContext.guiHeight() - totalHeight));

		drawContext.drawString(client.font, label, x, y, config.labelColor.colorValue(), true);
		drawContext.drawString(client.font, value, x + labelWidth + (labelWidth > 0 ? 4 : 0), y, config.valueColor.colorValue(), true);
	}

	private static void registerCommands() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(
				ClientCommandManager.literal("delay")
					.then(ClientCommandManager.literal("gui")
						.executes(context -> {
							context.getSource().getClient().execute(() -> {
								Minecraft client = context.getSource().getClient();
								client.setScreen(new DelayPositionEditorScreen());
							});
							context.getSource().sendFeedback(Component.literal("Opened delay HUD editor."));
							return 1;
						})
					)
					.then(ClientCommandManager.literal("text")
						.then(ClientCommandManager.literal("label")
							.then(ClientCommandManager.argument("text", greedyString())
								.executes(context -> {
									config.labelText = getString(context, "text");
									config.save();
									context.getSource().sendFeedback(Component.literal("Label text updated."));
									return 1;
								})
							)
						)
						.then(ClientCommandManager.literal("value")
							.then(ClientCommandManager.argument("template", greedyString())
								.executes(context -> {
									String template = getString(context, "template");
									if (!template.contains("{n}")) {
										context.getSource().sendError(Component.literal("Value template must include {n}."));
										return 0;
									}
									config.valueTemplate = template;
									config.save();
									context.getSource().sendFeedback(Component.literal("Value template updated."));
									return 1;
								})
							)
						)
					)
					.then(ClientCommandManager.literal("color")
						.then(ClientCommandManager.literal("label")
							.then(ClientCommandManager.argument("color", word())
								.suggests(DelayModClient::suggestColors)
								.executes(context -> setColor(context.getSource(), getString(context, "color"), true))
							)
						)
						.then(ClientCommandManager.literal("value")
							.then(ClientCommandManager.argument("color", word())
								.suggests(DelayModClient::suggestColors)
								.executes(context -> setColor(context.getSource(), getString(context, "color"), false))
							)
						)
					)
			);
		});
	}

	private static CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestColors(
		com.mojang.brigadier.context.CommandContext<FabricClientCommandSource> context,
		SuggestionsBuilder builder
	) {
		Arrays.stream(DelayColor.values()).forEach(color -> builder.suggest(color.serializedName()));
		return builder.buildFuture();
	}

	private static int setColor(FabricClientCommandSource source, String colorName, boolean label) {
		DelayColor color = DelayColor.fromName(colorName).orElse(null);
		if (color == null) {
			source.sendError(Component.literal("Unknown color. Valid colors: " + DelayColor.validNames()));
			return 0;
		}

		if (label) {
			config.labelColor = color;
			source.sendFeedback(Component.literal("Label color updated to ").append(Component.literal(color.serializedName()).withStyle(color.formatting())));
		} else {
			config.valueColor = color;
			source.sendFeedback(Component.literal("Value color updated to ").append(Component.literal(color.serializedName()).withStyle(color.formatting())));
		}
		config.save();
		return 1;
	}

	public static int getGroundTicks() {
		return groundTicks;
	}

	public static String resolveValueText() {
		return config.valueTemplate.replace("{n}", Integer.toString(groundTicks));
	}

	public static int getHudTextWidth(Minecraft client) {
		Component label = Component.literal(config.labelText);
		Component value = Component.literal(resolveValueText());
		int labelWidth = client.font.width(label);
		return labelWidth + client.font.width(value) + (labelWidth > 0 ? 4 : 0);
	}

	public static int getHudTextHeight(Minecraft client) {
		return client.font.lineHeight;
	}

	public static int getHudX() {
		return config.hudX;
	}

	public static int getHudY() {
		return config.hudY;
	}

	public static void setHudPosition(Minecraft client, int x, int y) {
		int maxX = Math.max(0, client.getWindow().getGuiScaledWidth() - getHudTextWidth(client));
		int maxY = Math.max(0, client.getWindow().getGuiScaledHeight() - getHudTextHeight(client));
		config.hudX = clamp(x, 0, maxX);
		config.hudY = clamp(y, 0, maxY);
		config.save();
	}

	public static Component getLabelPreviewText() {
		return Component.literal(config.labelText).withStyle(config.labelColor.formatting());
	}

	public static Component getValuePreviewText() {
		return Component.literal(resolveValueText()).withStyle(config.valueColor.formatting());
	}

	private static int clamp(int value, int min, int max) {
		if (value < min) {
			return min;
		}
		return Math.min(value, max);
	}
}
