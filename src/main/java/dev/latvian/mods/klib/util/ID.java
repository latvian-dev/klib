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
import net.minecraft.ResourceLocationException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface ID {
	ResourceLocation EMPTY = ResourceLocation.withDefaultNamespace("empty");
	ResourceLocation EMPTY_JAVA_ID = ResourceLocation.fromNamespaceAndPath("java", "empty");
	ResourceLocation EMPTY_KLIB_ID = ResourceLocation.fromNamespaceAndPath(KLib.ID, "empty");
	ResourceLocation EMPTY_VIDLIB_ID = ResourceLocation.fromNamespaceAndPath("vidlib", "empty");
	ResourceLocation EMPTY_VIDEO_ID = ResourceLocation.fromNamespaceAndPath("video", "empty");
	ResourceLocation EMPTY_JOML_ID = ResourceLocation.fromNamespaceAndPath("joml", "empty");

	Codec<ResourceLocation> CODEC = KLibCodecs.commonIdentifier("minecraft");
	StreamCodec<ByteBuf, ResourceLocation> STREAM_CODEC = KLibStreamCodecs.commonIdentifier("minecraft");
	DataType<ResourceLocation> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, ResourceLocation.class);

	static ResourceLocation mc(String path) {
		return EMPTY.withPath(path);
	}

	static ResourceLocation java(String path) {
		return EMPTY_JAVA_ID.withPath(path);
	}

	static ResourceLocation klib(String path) {
		return EMPTY_KLIB_ID.withPath(path);
	}

	static ResourceLocation vidlib(String path) {
		return EMPTY_VIDLIB_ID.withPath(path);
	}

	static ResourceLocation video(String path) {
		return EMPTY_VIDEO_ID.withPath(path);
	}

	static ResourceLocation joml(String path) {
		return EMPTY_JOML_ID.withPath(path);
	}

	static ResourceLocation idFromString(String string) {
		return ResourceLocation.tryParse(string);
	}

	static String idToString(ResourceLocation id) {
		return id.getNamespace().equals("minecraft") ? id.getPath() : id.toString();
	}

	static ResourceLocation parse(StringReader reader) throws CommandSyntaxException {
		int i = reader.getCursor();

		while (reader.canRead() && ResourceLocation.isAllowedInResourceLocation(reader.peek())) {
			reader.skip();
		}

		var s = reader.getString().substring(i, reader.getCursor());

		try {
			return idFromString(s);
		} catch (ResourceLocationException resourcelocationexception) {
			reader.setCursor(i);
			throw ResourceLocation.ERROR_INVALID.createWithContext(reader);
		}
	}

	static CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder, Supplier<Iterable<ResourceLocation>> allIds) {
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

	static SuggestionProvider<CommandSourceStack> registerSuggestionProvider(ResourceLocation id, Supplier<Iterable<ResourceLocation>> allIds) {
		return SuggestionProviders.register(id, (ctx, builder) -> suggest(builder, allIds));
	}
}
