package dev.latvian.mods.klib.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ParsedEntitySelector {
	public static final Codec<ParsedEntitySelector> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.STRING.fieldOf("selector").forGetter(ParsedEntitySelector::getInput),
		Codec.BOOL.optionalFieldOf("single", false).forGetter(ParsedEntitySelector::isSingle),
		Codec.BOOL.optionalFieldOf("players_only", false).forGetter(ParsedEntitySelector::isPlayersOnly)
	).apply(instance, ParsedEntitySelector::new));

	public static final Codec<ParsedEntitySelector> CODEC_ENTITY = Codec.STRING.xmap(s -> new ParsedEntitySelector(s, true, false), ParsedEntitySelector::getInput);
	public static final Codec<ParsedEntitySelector> CODEC_PLAYER = Codec.STRING.xmap(s -> new ParsedEntitySelector(s, true, true), ParsedEntitySelector::getInput);
	public static final Codec<ParsedEntitySelector> CODEC_ENTITIES = Codec.STRING.xmap(s -> new ParsedEntitySelector(s, false, false), ParsedEntitySelector::getInput);
	public static final Codec<ParsedEntitySelector> CODEC_PLAYERS = Codec.STRING.xmap(s -> new ParsedEntitySelector(s, false, true), ParsedEntitySelector::getInput);

	public static final Codec<ParsedEntitySelector> CODEC = KLibCodecs.or(CODEC_ENTITIES, DIRECT_CODEC);

	public static final StreamCodec<ByteBuf, ParsedEntitySelector> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public ParsedEntitySelector decode(ByteBuf buf) {
			int flags = buf.readByte() & 0xFF;
			boolean single = (flags & 1) != 0;
			boolean playersOnly = (flags & 2) != 0;
			return new ParsedEntitySelector(Utf8String.read(buf, Short.MAX_VALUE), single, playersOnly);
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

	public static final DataType<ParsedEntitySelector> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, ParsedEntitySelector.class);

	private final String input;
	private final boolean single;
	private final boolean playersOnly;
	private final Lazy<DataResult<EntitySelector>> selector;

	public ParsedEntitySelector(String input, boolean single, boolean playersOnly) {
		this.input = input;
		this.single = single;
		this.playersOnly = playersOnly;
		this.selector = Lazy.of(this::parse);
	}

	@Override
	@NotNull
	public String toString() {
		return input;
	}

	public String getInput() {
		return input;
	}

	public boolean isSingle() {
		return single;
	}

	public boolean isPlayersOnly() {
		return playersOnly;
	}

	private DataResult<EntitySelector> parse() {
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

			return DataResult.success(selector);
		} catch (CommandSyntaxException ex) {
			return DataResult.error(ex::getMessage);
		}
	}

	public Lazy<DataResult<EntitySelector>> getSelectorLazy() {
		return selector;
	}

	@Nullable
	public EntitySelector getSelector() {
		return selector.get().result().orElse(null);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		var that = (ParsedEntitySelector) obj;
		return Objects.equals(this.input, that.input) && this.single == that.single && this.playersOnly == that.playersOnly;
	}

	@Override
	public int hashCode() {
		return Objects.hash(input, single, playersOnly);
	}
}
