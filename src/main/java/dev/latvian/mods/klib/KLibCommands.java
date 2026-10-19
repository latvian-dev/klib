package dev.latvian.mods.klib;

import com.google.gson.JsonElement;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.klib.io.IOUtils;
import dev.latvian.mods.klib.platform.PlatformHelper;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.util.Cast;
import dev.latvian.mods.klib.util.CommandEvaluable;
import dev.latvian.mods.klib.util.StringUtils;
import io.netty.buffer.Unpooled;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

public class KLibCommands {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
		var regCommand = Commands.literal("custom-registry");

		for (var registry : CustomRegistry.ALL.values()) {
			regCommand.then(registryCommands(registry, context));
		}

		dispatcher.register(Commands.literal("klib")
			.requires(source -> source.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(regCommand)
		);
	}

	private static <T> LiteralArgumentBuilder<CommandSourceStack> registryCommands(CustomRegistry<?, T> registry, CommandBuildContext context) {
		var ops = context.createSerializationContext(JsonOps.INSTANCE);

		return Commands.literal(registry.registryId())
			.then(Commands.literal("list")
				.executes(ctx -> list(ctx.getSource(), registry, ops))
			)
			.then(Commands.literal("parse")
				.then(Commands.argument("value", registry.dataType().argument(context))
					.executes(ctx -> parse(ctx.getSource(), registry, registry.dataType().get(ctx, "value")))
				)
			);
	}

	private static <T> int list(CommandSourceStack source, CustomRegistry<?, T> registry, DynamicOps<JsonElement> ops) {
		var text = new ArrayList<Component>();
		text.add(Component.literal("Types:").withStyle(ChatFormatting.GRAY));
		var typesComponent = Component.empty();
		var typesFormat = "%0" + String.valueOf(registry.typeList().size() + 2).length() + "d ";

		typesComponent.append(typesFormat.formatted(0)).append(Component.literal("null").withStyle(ChatFormatting.GRAY));
		typesComponent.append(Component.literal("\n"));
		typesComponent.append(typesFormat.formatted(1)).append(Component.literal("direct").withStyle(ChatFormatting.GRAY));

		for (var type : registry.typeList()) {
			if (type.unit() == null) {
				typesComponent.append(Component.literal("\n"));

				var keys1 = type.mapCodec().keys(ops).map(JsonElement::getAsString).distinct().toList();

				var component1 = Component.empty();
				component1.append(typesFormat.formatted(registry.syncValues() ? registry.getTypeIndex(Cast.to(type)) : "-"));
				component1.append(Component.literal(type.key()).withStyle(ChatFormatting.GOLD));
				component1.append("(");

				for (int i = 0; i < keys1.size(); ++i) {
					if (i > 0) {
						component1.append(", ");
					}

					component1.append(Component.literal(keys1.get(i)).withStyle(ChatFormatting.GREEN));
				}

				component1.append(")");
				typesComponent.append(component1);
			}
		}

		text.add(typesComponent);
		text.add(Component.literal("Values:").withStyle(ChatFormatting.GRAY));

		var valuesComponent = Component.empty();
		var valuesFormat = registry.syncValues() ? ("%0" + String.valueOf(registry.values().size() + 2).length() + "d ") : "%s ";

		valuesComponent.append(valuesFormat.formatted(0)).append(Component.literal("null").withStyle(ChatFormatting.GRAY));
		valuesComponent.append(Component.literal("\n"));
		valuesComponent.append(valuesFormat.formatted(1)).append(Component.literal("custom").withStyle(ChatFormatting.GRAY));

		for (var ref : registry.values()) {
			valuesComponent.append(Component.literal("\n"));

			var color = registry.getType(ref) instanceof UnitType<?, T> ? ChatFormatting.GOLD : ChatFormatting.GREEN;
			int bytes = 0;

			try {
				var buf = PlatformHelper.CURRENT.createBuffer(Unpooled.buffer(), source.registryAccess());
				registry.streamCodec().encode(Cast.to(buf), ref);
				bytes = buf.readableBytes();
				buf.release();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			valuesComponent.append(Component.empty()
				.append(valuesFormat.formatted(registry.syncValues() ? registry.getValueIndex(ref.key()) : "-"))
				.append(Component.literal(ref.key()).withStyle(ChatFormatting.YELLOW))
				.append(": ")
				.append(Component.literal(String.valueOf(ref.value())).withStyle(color))
				.append(bytes == 0 ? " (Error writing bytes)" : (" (" + StringUtils.siByteSize(bytes) + ")"))
			);
		}

		text.add(valuesComponent);

		for (var component : text) {
			source.sendSuccess(() -> component, false);
		}

		return 1;
	}

	private static <T> int parse(CommandSourceStack source, CustomRegistry<?, T> registry, Ref<T> value) {
		source.sendSuccess(() -> Component.literal("==="), false);

		source.sendSuccess(() -> {
			var component = Component.literal("Value: ");

			if (registry.getType(value) instanceof UnitType<?, T> unit) {
				component.append(Component.literal(unit.key()).withStyle(ChatFormatting.GOLD));
			} else {
				component.append(Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GREEN));
			}

			return component;
		}, false);

		source.sendSuccess(() -> {
			var jsonComponent = Component.literal("SNBT: ");
			var ops = source.registryAccess().createSerializationContext(NbtOps.INSTANCE);
			var tag = registry.codec().encodeStart(ops, value).getOrThrow();
			jsonComponent.append(NbtUtils.toPrettyComponent(tag));
			return jsonComponent;
		}, false);

		try {
			var buf = PlatformHelper.CURRENT.createBuffer(Unpooled.buffer(), source.registryAccess());
			registry.streamCodec().encode(Cast.to(buf), value);
			var bytes = IOUtils.toByteArray(buf, true);

			source.sendSuccess(() -> {
				var netComponent = Component.literal("Network Size: ");
				netComponent.append(Component.literal(StringUtils.siByteSize(bytes.length)).withStyle(ChatFormatting.YELLOW));
				return netComponent;
			}, false);

			source.sendSuccess(() -> {
				var netComponent = Component.literal("Bytes:");
				var sb = new StringBuilder();

				for (byte b : bytes) {
					sb.append(" %02X".formatted(b & 0xFF));
				}

				netComponent.append(Component.literal(sb.toString()).withStyle(ChatFormatting.YELLOW));
				return netComponent;
			}, false);
		} catch (Exception ex) {
			KLib.LOGGER.error("Error writing bytes of " + value, ex);
			source.sendSuccess(() -> Component.literal("Network: Error " + ex), false);
		}

		if (value.value() instanceof CommandEvaluable val) {
			var component = val.evaluate(source);

			if (component != null) {
				source.sendSuccess(() -> Component.literal("Evaluated: ").append(component), false);
			}
		}

		return 1;
	}
}
