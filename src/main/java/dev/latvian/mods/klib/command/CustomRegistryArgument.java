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
import dev.latvian.mods.klib.registry.CustomRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public record CustomRegistryArgument<T>(DynamicOps<Tag> ops, TagParser<Tag> parser, CustomRegistry<?, T> registry) implements ArgumentType<T> {
	private static final Dynamic2CommandExceptionType INVALID_ENUM = new Dynamic2CommandExceptionType((found, constants) -> Component.translatable("commands.neoforge.arguments.enum.invalid", constants, found));
	private static final DynamicCommandExceptionType INVALID_DATA = new DynamicCommandExceptionType(error -> Component.literal("Parsing error: " + error));

	@Override
	public T parse(StringReader reader) throws CommandSyntaxException {
		if (!registry.valueMap().isEmpty()) {
			int cursor = reader.getCursor();

			try {
				var id = Identifier.read(reader);

				for (var entry : registry.valueMap().entrySet()) {
					if (entry.getKey().identifier().equals(id)) {
						return entry.getValue();
					}
				}

				throw INVALID_ENUM.createWithContext(reader, id, registry.registryKeys().root().identifier());
			} catch (Exception ignore) {
			}

			reader.setCursor(cursor);
		}

		var tag = parser.parseAsArgument(reader);
		return registry.dataType().codec().parse(ops, tag).getOrThrow(INVALID_DATA::create);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return SharedSuggestionProvider.suggestResource(registry.sortedKeys().stream().map(ResourceKey::identifier), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return registry.sortedKeys().stream().map(ResourceKey::identifier).map(Identifier::toString).toList();
	}

	public static class Info<T> implements ArgumentTypeInfo<CustomRegistryArgument<T>, Info.ArgumentTemplate<T>> {
		@Override
		public void serializeToNetwork(ArgumentTemplate<T> template, FriendlyByteBuf buf) {
			buf.writeResourceKey(template.registry.registryKeys().root());
		}

		@Override
		public ArgumentTemplate<T> deserializeFromNetwork(FriendlyByteBuf buf) {
			var key = buf.readRegistryKey();
			var registry = CustomRegistry.ALL.get(key);

			if (registry != null) {
				return new ArgumentTemplate(this, registry);
			}

			return null;
		}

		@Override
		public void serializeToJson(ArgumentTemplate<T> template, JsonObject json) {
			json.addProperty("registry", template.registry.registryKeys().root().identifier().toString());
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
