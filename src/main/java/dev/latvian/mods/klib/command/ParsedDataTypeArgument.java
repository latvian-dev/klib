package dev.latvian.mods.klib.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public record ParsedDataTypeArgument<T>(DynamicOps<Tag> ops, TagParser<Tag> parser, Ref<DataType<?>> dataType) implements ArgumentType<T> {
	private static final DynamicCommandExceptionType ERROR_PARSING = new DynamicCommandExceptionType(arg -> Component.literal(String.valueOf(arg)));

	@Override
	public T parse(StringReader reader) throws CommandSyntaxException {
		var tag = parser.parseAsArgument(reader);
		var decoded = dataType.value().codec().parse(ops, tag);

		try {
			//noinspection unchecked
			return (T) decoded.getOrThrow();
		} catch (Exception ex) {
			throw ERROR_PARSING.create(ex.getMessage());
		}
	}

	public static class Info<T> implements ArgumentTypeInfo<ParsedDataTypeArgument<T>, Info.ArgumentTemplate<T>> {
		@Override
		public void serializeToNetwork(ArgumentTemplate<T> template, FriendlyByteBuf buf) {
			DataType.REGISTRY.streamCodec().encode(buf, template.dataType);
		}

		@Override
		public ArgumentTemplate<T> deserializeFromNetwork(FriendlyByteBuf buf) {
			var key = DataType.REGISTRY.streamCodec().decode(buf);

			try {
				return new ArgumentTemplate<>(this, key);
			} catch (NullPointerException _) {
				return null;
			}
		}

		@Override
		public void serializeToJson(ArgumentTemplate<T> template, JsonObject json) {
			json.addProperty("data_type", template.dataType.key());
		}

		@Override
		public ArgumentTemplate<T> unpack(ParsedDataTypeArgument<T> argument) {
			return new ArgumentTemplate<>(this, argument.dataType);
		}

		public record ArgumentTemplate<T>(Info<T> info, Ref<DataType<?>> dataType) implements Template<ParsedDataTypeArgument<T>> {
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
