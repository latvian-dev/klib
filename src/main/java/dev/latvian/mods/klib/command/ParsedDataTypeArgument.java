package dev.latvian.mods.klib.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.data.DataTypeRegistry;
import dev.latvian.mods.klib.util.Cast;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public record ParsedDataTypeArgument<T>(DynamicOps<Tag> ops, TagParser<Tag> parser, DataType<T> dataType) implements ArgumentType<T> {
	private static final DynamicCommandExceptionType ERROR_PARSING = new DynamicCommandExceptionType(arg -> Component.literal(String.valueOf(arg)));

	@Override
	public T parse(StringReader reader) throws CommandSyntaxException {
		var tag = parser.parseAsArgument(reader);
		var decoded = dataType.codec().parse(ops, tag);

		try {
			return decoded.getOrThrow();
		} catch (Exception ex) {
			throw ERROR_PARSING.create(ex.getMessage());
		}
	}

	public static class ParsedDataTypeArgumentInfo<T> implements ArgumentTypeInfo<ParsedDataTypeArgument<T>, ParsedDataTypeArgumentInfo.ParsedDataTypeArgumentTemplate<T>> {
		@Override
		public void serializeToNetwork(ParsedDataTypeArgumentTemplate<T> template, FriendlyByteBuf buffer) {
			DataType.REGISTRY_KEYS.streamCodec().encode(buffer, template.dataType.requireKey());
		}

		@Override
		public ParsedDataTypeArgumentTemplate<T> deserializeFromNetwork(FriendlyByteBuf buffer) {
			var key = DataType.REGISTRY_KEYS.streamCodec().decode(buffer);

			try {
				return new ParsedDataTypeArgumentTemplate<>(this, Cast.to(DataTypeRegistry.require(key)));
			} catch (NullPointerException _) {
				return null;
			}
		}

		@Override
		public void serializeToJson(ParsedDataTypeArgumentTemplate<T> template, JsonObject json) {
			json.addProperty("data_type", template.dataType.requireKey().identifier().toString());
		}

		@Override
		public ParsedDataTypeArgumentTemplate<T> unpack(ParsedDataTypeArgument<T> argument) {
			return new ParsedDataTypeArgumentTemplate<>(this, argument.dataType);
		}

		public record ParsedDataTypeArgumentTemplate<T>(ParsedDataTypeArgumentInfo<T> info, DataType<T> dataType) implements Template<ParsedDataTypeArgument<T>> {
			@Override
			public ParsedDataTypeArgument<T> instantiate(CommandBuildContext ctx) {
				var ops = ctx.createSerializationContext(NbtOps.INSTANCE);
				return new ParsedDataTypeArgument<>(ops, TagParser.create(ops), dataType);
			}

			@Override
			public ArgumentTypeInfo<ParsedDataTypeArgument<T>, ?> type() {
				return info;
			}
		}
	}
}
