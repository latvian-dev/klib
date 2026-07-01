package dev.latvian.mods.klib;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class KLibCommands {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
		var regCommand = Commands.literal("custom-registry");

		for (var registry : CustomRegistry.ALL.values()) {
			if (registry.side().isServer()) {
				regCommand.then(registryCommands(registry, context));
			}
		}

		dispatcher.register(Commands.literal("klib")
			.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(regCommand)
		);
	}

	private static <T> LiteralArgumentBuilder<CommandSourceStack> registryCommands(CustomRegistry<?, T> registry, CommandBuildContext context) {
		return Commands.literal(registry.registryKeys().root().identifier().toString())
			.then(Commands.literal("print")
				.executes(ctx -> {
					ctx.getSource().sendSuccess(() -> Component.literal("=== " + registry.registryKeys().root().identifier() + " ==="), false);
					ctx.getSource().sendSuccess(() -> Component.literal("Types:").withStyle(ChatFormatting.GRAY), false);

					for (var type : registry.typeMap().entrySet()) {
						ctx.getSource().sendSuccess(() -> Component.literal(registry.registryKeys().encode(type.getKey())).withStyle(type.getValue().instance() == null ? ChatFormatting.GOLD : ChatFormatting.GREEN), false);
					}

					ctx.getSource().sendSuccess(() -> Component.literal("Values:").withStyle(ChatFormatting.GRAY), false);

					var format = "%0" + String.valueOf(registry.valueMap().size()).length() + "d ";

					ctx.getSource().sendSuccess(() -> Component.empty().append(format.formatted(0)).append(Component.literal("null").withStyle(ChatFormatting.GRAY)), false);
					ctx.getSource().sendSuccess(() -> Component.empty().append(format.formatted(1)).append(Component.literal("custom").withStyle(ChatFormatting.GRAY)), false);

					for (var entry : registry.valueMap().entrySet()) {
						var color = registry.getType(entry.getValue()) instanceof CustomRegistryType.Unit<?, T> ? ChatFormatting.GOLD : ChatFormatting.GREEN;

						ctx.getSource().sendSuccess(() -> Component.empty()
							.append(format.formatted(registry.getValueIndex(entry.getValue())))
							.append(Component.literal(registry.registryKeys().encode(entry.getKey())).withStyle(ChatFormatting.YELLOW))
							.append(": ")
							.append(Component.literal(String.valueOf(entry.getValue())).withStyle(color)), false);
					}

					return 1;
				})
			)
			.then(Commands.literal("parse")
				.then(Commands.argument("value", registry.dataType().argument(context))
					.executes(ctx -> {
						var value = registry.dataType().get(ctx, "value");

						if (registry.getType(value) instanceof CustomRegistryType.Unit<?, T> unit) {
							ctx.getSource().sendSuccess(() -> Component.literal(registry.registryKeys().encode(unit.key())).withStyle(ChatFormatting.GOLD), false);
						} else {
							ctx.getSource().sendSuccess(() -> Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GREEN), false);
						}

						return 1;
					})
				)
			);
	}
}
