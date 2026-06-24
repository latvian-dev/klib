package dev.latvian.mods.klib.data;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

public interface DataTypeRegistry {
	record Data(
		Map<ResourceKey<DataType<?>>, DataType<?>> byKey,
		Map<DataType<?>, ResourceKey<DataType<?>>> byType,
		Map<DataType<?>, DataTypeCommandInfo> commandInfo
	) {
	}

	Mutable<Data> DATA = new MutableObject<>(new Data(Map.of(), Map.of(), Map.of()));

	static void registerAll(Consumer<DataTypeRegistry> consumer) {
		var byKey = new Object2ObjectOpenHashMap<ResourceKey<DataType<?>>, DataType<?>>();
		var byType = new Reference2ObjectOpenHashMap<DataType<?>, ResourceKey<DataType<?>>>();
		var commandInfo = new Reference2ObjectOpenHashMap<DataType<?>, DataTypeCommandInfo>();

		consumer.accept(new DataTypeRegistry() {
			@Override
			public void register(Identifier id, DataType<?> type) {
				var key = DataType.REGISTRY_KEYS.create(id);
				byKey.put(key, type);
				byType.put(type, key);
			}

			@Override
			public void registerCommandInfo(DataType<?> type, DataTypeCommandInfo info) {
				commandInfo.put(type, info);
			}
		});

		DATA.setValue(new Data(
			Collections.unmodifiableMap(new Object2ObjectOpenHashMap<>(byKey)),
			Collections.unmodifiableMap(new Reference2ObjectOpenHashMap<>(byType)),
			Collections.unmodifiableMap(new Reference2ObjectOpenHashMap<>(commandInfo))
		));
	}

	@Nullable
	static DataType<?> lookup(ResourceKey<DataType<?>> key) {
		return DATA.get().byKey.get(key);
	}

	static DataType<?> require(ResourceKey<DataType<?>> key) {
		var dataType = lookup(key);

		if (dataType == null) {
			throw new NullPointerException("DataType '" + key.identifier() + "' not registered");
		}

		return dataType;
	}

	void register(Identifier id, DataType<?> type);

	void registerCommandInfo(DataType<?> type, DataTypeCommandInfo info);

	default <T> void registerCommandInfo(DataType<T> type, ArgumentTypeProvider argumentType, ArgumentGetter<T> argumentGetter) {
		registerCommandInfo(type, new DataTypeCommandInfo(argumentType, argumentGetter));
	}

	default <T> void registerCommandInfo(DataType<T> type, ArgumentTypeProvider.NS argumentType, ArgumentGetter<T> argumentGetter) {
		registerCommandInfo(type, (ArgumentTypeProvider) argumentType, argumentGetter);
	}

	default <T> void registerCommandInfo(DataType<T> type, ArgumentTypeProvider.NSNCTX argumentType, ArgumentGetter<T> argumentGetter) {
		registerCommandInfo(type, (ArgumentTypeProvider) argumentType, argumentGetter);
	}
}
