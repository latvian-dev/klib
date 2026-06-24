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
import dev.latvian.mods.klib.data.DataTypeRegistry;
import dev.latvian.mods.klib.util.Cast;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public record EnumDataTypeArgument<T>(DataType<T> dataType) implements ArgumentType<T> {
	private static final Dynamic2CommandExceptionType INVALID_ENUM = new Dynamic2CommandExceptionType((found, constants) -> Component.translatable("commands.neoforge.arguments.enum.invalid", constants, found));

	@Override
	public T parse(StringReader reader) throws CommandSyntaxException {
		var name = reader.readUnquotedString();

		for (var entry : dataType.enumValues()) {
			if (entry.getKey().equalsIgnoreCase(name)) {
				return entry.getValue();
			}
		}

		throw INVALID_ENUM.createWithContext(reader, name, dataType.enumValues().stream().map(Map.Entry::getKey).collect(Collectors.joining(", ", "[", "]")));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(dataType.enumValues().stream().map(Map.Entry::getKey), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return dataType.enumValues().stream().map(Map.Entry::getKey).toList();
	}

	public static class EnumDataTypeArgumentInfo<T> implements ArgumentTypeInfo<EnumDataTypeArgument<T>, EnumDataTypeArgumentInfo.EnumDataTypeArgumentTemplate<T>> {
		@Override
		public void serializeToNetwork(EnumDataTypeArgumentTemplate<T> template, FriendlyByteBuf buffer) {
			DataType.REGISTRY_KEYS.streamCodec().encode(buffer, template.dataType.requireKey());
		}

		@Override
		public EnumDataTypeArgumentTemplate<T> deserializeFromNetwork(FriendlyByteBuf buffer) {
			var key = DataType.REGISTRY_KEYS.streamCodec().decode(buffer);

			try {
				return new EnumDataTypeArgumentTemplate<>(this, Cast.to(DataTypeRegistry.require(key)));
			} catch (NullPointerException _) {
				return null;
			}
		}

		@Override
		public void serializeToJson(EnumDataTypeArgumentTemplate<T> template, JsonObject json) {
			json.addProperty("data_type", template.dataType.requireKey().identifier().toString());
		}

		@Override
		public EnumDataTypeArgumentTemplate<T> unpack(EnumDataTypeArgument<T> argument) {
			return new EnumDataTypeArgumentTemplate<>(this, argument.dataType);
		}

		public record EnumDataTypeArgumentTemplate<T>(EnumDataTypeArgumentInfo<T> info, DataType<T> dataType) implements Template<EnumDataTypeArgument<T>> {
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
