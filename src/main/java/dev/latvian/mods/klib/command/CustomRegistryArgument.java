package dev.latvian.mods.klib.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.DynamicOps;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public record CustomRegistryArgument<T>(DynamicOps<Tag> ops, TagParser<Tag> parser, CustomRegistry<?, T> registry) implements ArgumentType<Ref<T>> {
	private static final Dynamic2CommandExceptionType INVALID_ENUM = new Dynamic2CommandExceptionType((found, constants) -> Component.translatable("commands.neoforge.arguments.enum.invalid", constants, found));
	private static final DynamicCommandExceptionType INVALID_DATA = new DynamicCommandExceptionType(error -> Component.literal("Parsing error: " + error));

	@Override
	public Ref<T> parse(StringReader reader) throws CommandSyntaxException {
		if (!registry.values().isEmpty()) {
			int cursor = reader.getCursor();

			try {
				var id = KLibCodecs.readInternPath(reader);

				var ref = registry.get(id);

				if (ref != null) {
					return ref;
				}

				throw INVALID_ENUM.createWithContext(reader, id, registry.registryId());
			} catch (Exception ignore) {
			}

			reader.setCursor(cursor);
		}

		var tag = parser.parseAsArgument(reader);
		return registry.codec().parse(ops, tag).getOrThrow(INVALID_DATA::create);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggest(registry.values().stream().map(Ref::key), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return registry.values().stream().map(Ref::key).toList();
	}

	public static class Info<T> implements ArgumentTypeInfo<CustomRegistryArgument<T>, Info.ArgumentTemplate<T>> {
		@Override
		public void serializeToNetwork(ArgumentTemplate<T> template, FriendlyByteBuf buf) {
			KLibStreamCodecs.INTERN_STRING.encode(buf, template.registry.registryId());
		}

		@Override
		public ArgumentTemplate<T> deserializeFromNetwork(FriendlyByteBuf buf) {
			var key = KLibStreamCodecs.INTERN_STRING.decode(buf);
			var registry = CustomRegistry.ALL.get(key);

			if (registry != null) {
				return new ArgumentTemplate(this, registry);
			}

			return null;
		}

		@Override
		public void serializeToJson(ArgumentTemplate<T> template, JsonObject json) {
			json.addProperty("registry", template.registry.registryId());
		}

		@Override
		public ArgumentTemplate<T> unpack(CustomRegistryArgument<T> argument) {
			return new ArgumentTemplate<>(this, argument.registry);
		}

		public record ArgumentTemplate<T>(Info<T> info, CustomRegistry<?, T> registry) implements Template<CustomRegistryArgument<T>> {
			@Override
			public CustomRegistryArgument<T> instantiate(CommandBuildContext ctx) {
				var ops = ctx.createSerializationContext(NbtOps.INSTANCE);
				return new CustomRegistryArgument<>(ops, TagParser.create(ops), registry);
			}

			@Override
			public ArgumentTypeInfo<CustomRegistryArgument<T>, ?> type() {
				return info;
			}
		}
	}
}
