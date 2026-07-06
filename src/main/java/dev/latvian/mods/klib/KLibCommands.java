package dev.latvian.mods.klib;

import com.google.gson.JsonElement;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.NoticeDialog;
import net.minecraft.server.dialog.body.DialogBody;
import net.minecraft.server.dialog.body.PlainMessage;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KLibCommands {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
		var regCommand = Commands.literal("custom-registry");

		for (var registry : CustomRegistry.ALL.values()) {
			regCommand.then(registryCommands(registry, context));
		}

		dispatcher.register(Commands.literal("klib")
			.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
			.then(regCommand)
		);
	}

	private static <T> LiteralArgumentBuilder<CommandSourceStack> registryCommands(CustomRegistry<?, T> registry, CommandBuildContext context) {
		var ops = context.createSerializationContext(JsonOps.INSTANCE);

		return Commands.literal(registry.registryId())
			.then(Commands.literal("list")
				.executes(ctx -> list(ctx.getSource().getPlayerOrException(), registry, ops))
			)
			.then(Commands.literal("parse")
				.then(Commands.argument("value", registry.dataType().argument(context))
					.executes(ctx -> parse(ctx.getSource(), registry, registry.dataType().get(ctx, "value")))
				)
			);
	}

	private static <T> int list(ServerPlayer player, CustomRegistry<?, T> registry, DynamicOps<JsonElement> ops) {
		var body = new ArrayList<DialogBody>();
		body.add(new PlainMessage(Component.literal("Types:").withStyle(ChatFormatting.GRAY), 350));
		var typesComponent = Component.empty().withStyle(ChatFormatting.GOLD);

		boolean first = true;

		for (var type : registry.typeList()) {
			if (type.unit() == null) {
				if (first) {
					first = false;
				} else {
					typesComponent.append(Component.literal("\n"));
				}

				var keys1 = type.codec().keys(ops).map(String::valueOf).distinct().toList();

				var component1 = Component.literal(type.key() + "[");

				for (int i = 0; i < keys1.size(); ++i) {
					if (i > 0) {
						component1.append(", ");
					}

					component1.append(Component.literal(keys1.get(i)).withStyle(ChatFormatting.GREEN));
				}

				component1.append("]");
				typesComponent.append(component1);
			}
		}

		body.add(new PlainMessage(typesComponent, 350));
		body.add(new PlainMessage(Component.literal("Values:").withStyle(ChatFormatting.GRAY), 350));

		var valuesComponent = Component.empty();

		var format = registry.syncValues() ? ("%0" + String.valueOf(registry.values().size()).length() + "d ") : "%s ";

		valuesComponent.append(format.formatted(0)).append(Component.literal("null").withStyle(ChatFormatting.GRAY));
		valuesComponent.append(Component.literal("\n"));
		valuesComponent.append(format.formatted(1)).append(Component.literal("custom").withStyle(ChatFormatting.GRAY));

		for (var ref : registry.values()) {
			valuesComponent.append(Component.literal("\n"));

			var color = registry.getType(ref) instanceof UnitType<?, T> ? ChatFormatting.GOLD : ChatFormatting.GREEN;

			valuesComponent.append(Component.empty()
				.append(format.formatted(registry.syncValues() ? registry.getValueIndex(ref.key()) : "-"))
				.append(Component.literal(ref.key()).withStyle(ChatFormatting.YELLOW))
				.append(": ")
				.append(Component.literal(String.valueOf(ref.value())).withStyle(color))
			);
		}

		body.add(new PlainMessage(valuesComponent, 350));

		player.openDialog(Holder.direct(new NoticeDialog(
			new CommonDialogData(
				Component.literal(registry.registryId()),
				Optional.empty(),
				true,
				true,
				DialogAction.CLOSE,
				body,
				List.of()
			),
			new ActionButton(new CommonButtonData(Component.literal("Close"), 150), Optional.empty())
		)));

		return 1;
	}

	private static <T> int parse(CommandSourceStack source, CustomRegistry<?, T> registry, Ref<T> value) {
		if (registry.getType(value) instanceof UnitType<?, T> unit) {
			source.sendSuccess(() -> Component.literal(unit.key()).withStyle(ChatFormatting.GOLD), false);
		} else {
			source.sendSuccess(() -> Component.literal(String.valueOf(value)).withStyle(ChatFormatting.GREEN), false);
		}

		return 1;
	}
}
