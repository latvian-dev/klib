package dev.latvian.mods.klib.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.IdentifierException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface ID {
	Codec<Identifier> CODEC = KLibCodecs.commonIdentifier("minecraft");
	StreamCodec<ByteBuf, Identifier> STREAM_CODEC = KLibStreamCodecs.commonIdentifier("minecraft");
	DataType<Identifier> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC);
	StreamCodec<RegistryFriendlyByteBuf, Identifier> REGISTRY_STREAM_CODEC = KLibStreamCodecs.toRegistry(STREAM_CODEC);
	Identifier EMPTY_JAVA_ID = Identifier.fromNamespaceAndPath("java", "empty");
	Identifier EMPTY_KLIB_ID = Identifier.fromNamespaceAndPath(KLib.ID, "empty");
	Identifier EMPTY_VIDLIB_ID = Identifier.fromNamespaceAndPath("vidlib", "empty");
	Identifier EMPTY_VIDEO_ID = Identifier.fromNamespaceAndPath("video", "empty");
	Identifier EMPTY_JOML_ID = Identifier.fromNamespaceAndPath("joml", "empty");

	static Identifier mc(String path) {
		return Identifier.withDefaultNamespace(path);
	}

	static Identifier java(String path) {
		return EMPTY_JAVA_ID.withPath(path);
	}

	static Identifier klib(String path) {
		return EMPTY_KLIB_ID.withPath(path);
	}

	static Identifier vidlib(String path) {
		return EMPTY_VIDLIB_ID.withPath(path);
	}

	static Identifier video(String path) {
		return EMPTY_VIDEO_ID.withPath(path);
	}

	static Identifier joml(String path) {
		return EMPTY_JOML_ID.withPath(path);
	}

	static Identifier idFromString(String string) {
		return string.indexOf(':') == -1 ? Identifier.withDefaultNamespace(string) : Identifier.parse(string);
	}

	static String idToString(Identifier id) {
		return id.getNamespace().equals("minecraft") ? id.getPath() : id.toString();
	}

	static Identifier parse(StringReader reader) throws CommandSyntaxException {
		int i = reader.getCursor();

		while (reader.canRead() && Identifier.isAllowedInIdentifier(reader.peek())) {
			reader.skip();
		}

		var s = reader.getString().substring(i, reader.getCursor());

		try {
			return idFromString(s);
		} catch (IdentifierException resourcelocationexception) {
			reader.setCursor(i);
			throw Identifier.ERROR_INVALID.createWithContext(reader);
		}
	}

	static CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder, Supplier<Iterable<Identifier>> allIds) {
		var input = builder.getRemaining().toLowerCase(Locale.ROOT);
		boolean col = input.indexOf(':') > -1;

		for (var id : allIds.get()) {
			if (col) {
				var ids = idToString(id);

				if (SharedSuggestionProvider.matchesSubStr(input, ids)) {
					builder.suggest(ids);
				}
			} else if (SharedSuggestionProvider.matchesSubStr(input, id.getNamespace()) || SharedSuggestionProvider.matchesSubStr(input, id.getPath())) {
				builder.suggest(idToString(id));
			}
		}

		return builder.buildFuture();
	}

	static SuggestionProvider<CommandSourceStack> registerSuggestionProvider(Identifier id, Supplier<Iterable<Identifier>> allIds) {
		return SuggestionProviders.register(id, (ctx, builder) -> suggest(builder, allIds));
	}
}
