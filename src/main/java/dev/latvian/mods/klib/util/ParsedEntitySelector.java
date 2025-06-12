package dev.latvian.mods.klib.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public record ParsedEntitySelector(String input, EntitySelector selector, boolean single, boolean playersOnly) {
	public static DataResult<ParsedEntitySelector> parse(String input, boolean single, boolean playersOnly) {
		try {
			var selector = new EntitySelectorParser(new StringReader(input), true).parse();

			if (single && selector.getMaxResults() > 1) {
				if (playersOnly) {
					return DataResult.error(() -> "Only one player is allowed, but the provided selector allows more than one");
				} else {
					return DataResult.error(() -> "Only one entity is allowed, but the provided selector allows more than one");
				}
			} else if (selector.includesEntities() && playersOnly && !selector.isSelfSelector()) {
				return DataResult.error(() -> "Only players are allowed, but the provided selector includes entities");
			}

			return DataResult.success(new ParsedEntitySelector(input, selector, single, playersOnly));
		} catch (CommandSyntaxException ex) {
			return DataResult.error(ex::getMessage);
		}
	}

	public static final Codec<ParsedEntitySelector> CODEC_ENTITY = Codec.STRING.flatXmap(s -> parse(s, true, false), s -> DataResult.success(s.input()));
	public static final Codec<ParsedEntitySelector> CODEC_PLAYER = Codec.STRING.flatXmap(s -> parse(s, true, true), s -> DataResult.success(s.input()));
	public static final Codec<ParsedEntitySelector> CODEC_ENTITIES = Codec.STRING.flatXmap(s -> parse(s, false, false), s -> DataResult.success(s.input()));
	public static final Codec<ParsedEntitySelector> CODEC_PLAYERS = Codec.STRING.flatXmap(s -> parse(s, false, true), s -> DataResult.success(s.input()));

	public static final StreamCodec<ByteBuf, ParsedEntitySelector> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public ParsedEntitySelector decode(ByteBuf buf) {
			int flags = buf.readByte() & 0xFF;
			boolean single = (flags & 1) != 0;
			boolean playersOnly = (flags & 2) != 0;
			return parse(Utf8String.read(buf, Short.MAX_VALUE), single, playersOnly).getOrThrow();
		}

		@Override
		public void encode(ByteBuf buf, ParsedEntitySelector value) {
			int flags = (value.single ? 1 : 0) | (value.playersOnly ? 2 : 0);
			buf.writeByte(flags);
			Utf8String.write(buf, value.input, Short.MAX_VALUE);
		}
	};

	public static final DataType<ParsedEntitySelector> ENTITY_DATA_TYPE = DataType.of(CODEC_ENTITY, STREAM_CODEC, ParsedEntitySelector.class);
	public static final DataType<ParsedEntitySelector> PLAYER_DATA_TYPE = DataType.of(CODEC_PLAYER, STREAM_CODEC, ParsedEntitySelector.class);
	public static final DataType<ParsedEntitySelector> ENTITIES_DATA_TYPE = DataType.of(CODEC_ENTITIES, STREAM_CODEC, ParsedEntitySelector.class);
	public static final DataType<ParsedEntitySelector> PLAYERS_DATA_TYPE = DataType.of(CODEC_PLAYERS, STREAM_CODEC, ParsedEntitySelector.class);

	@Override
	@NotNull
	public String toString() {
		return input;
	}
}
