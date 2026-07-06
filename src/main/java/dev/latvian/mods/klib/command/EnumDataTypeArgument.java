package dev.latvian.mods.klib.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public record EnumDataTypeArgument<T>(Ref<DataType<?>> dataType) implements ArgumentType<T> {
	private static final Dynamic2CommandExceptionType INVALID_ENUM = new Dynamic2CommandExceptionType((found, constants) -> Component.translatable("commands.neoforge.arguments.enum.invalid", constants, found));

	@Override
	public T parse(StringReader reader) throws CommandSyntaxException {
		var dataTypeValue = dataType.value();
		var name = reader.readUnquotedString();

		for (var entry : dataTypeValue.enumValues()) {
			if (entry.getKey().equalsIgnoreCase(name)) {
				//noinspection unchecked
				return (T) entry.getValue();
			}
		}

		throw INVALID_ENUM.createWithContext(reader, name, dataTypeValue.enumValues().stream().map(Map.Entry::getKey).collect(Collectors.joining(", ", "[", "]")));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(dataType.value().enumValues().stream().map(Map.Entry::getKey), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return dataType.value().enumValues().stream().map(Map.Entry::getKey).toList();
	}

	public static class Info<T> implements ArgumentTypeInfo<EnumDataTypeArgument<T>, Info.ArgumentTemplate<T>> {
		@Override
		public void serializeToNetwork(ArgumentTemplate<T> template, FriendlyByteBuf buf) {
			DataType.REGISTRY.streamCodec().encode(buf, template.dataType);
		}

		@Override
		public ArgumentTemplate<T> deserializeFromNetwork(FriendlyByteBuf buf) {
			var ref = DataType.REGISTRY.streamCodec().decode(buf);

			try {
				return new ArgumentTemplate(this, ref);
			} catch (NullPointerException _) {
				return null;
			}
		}

		@Override
		public void serializeToJson(ArgumentTemplate<T> template, JsonObject json) {
			json.addProperty("data_type", template.dataType.key());
		}

		@Override
		public ArgumentTemplate<T> unpack(EnumDataTypeArgument<T> argument) {
			return new ArgumentTemplate<>(this, argument.dataType);
		}

		public record ArgumentTemplate<T>(Info<T> info, Ref<DataType<?>> dataType) implements Template<EnumDataTypeArgument<T>> {
			@Override
			public EnumDataTypeArgument<T> instantiate(CommandBuildContext ctx) {
				return new EnumDataTypeArgument<>(dataType);
			}

			@Override
			public ArgumentTypeInfo<EnumDataTypeArgument<T>, ?> type() {
				return info;
			}
		}
	}
}
