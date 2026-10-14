package dev.latvian.mods.klib.data;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Consumer;

public interface DataTypeCommandInfoRegistry {
	Map<DataType<?>, DataTypeCommandInfo<?>> MAP = new Reference2ObjectOpenHashMap<>();

	static void registerAll(Consumer<DataTypeCommandInfoRegistry> callback) {
		MAP.clear();
		callback.accept(info -> MAP.put(info.dataType(), info));
	}

	@Nullable
	static DataTypeCommandInfo<?> lookup(DataType<?> dataType) {
		return MAP.get(dataType);
	}

	static DataTypeCommandInfo<?> require(DataType<?> dataType) {
		var commandInfo = lookup(dataType);

		if (commandInfo == null) {
			throw new NullPointerException("DataType '" + dataType.toString() + "' not registered");
		}

		return commandInfo;
	}

	void register(DataTypeCommandInfo<?> info);

	default <T> void register(DataType<T> type, ArgumentTypeProvider<T> argumentType, @Nullable ArgumentGetter<T> argumentGetter) {
		register(new DataTypeCommandInfo<>(type, argumentType, argumentGetter));
	}

	default <T> void register(DataType<T> type, ArgumentTypeProvider.NS<T> argumentType, @Nullable ArgumentGetter<T> argumentGetter) {
		register(type, (ArgumentTypeProvider<T>) argumentType, argumentGetter);
	}

	default <T> void register(DataType<T> type, ArgumentTypeProvider.NSNCTX<T> argumentType, @Nullable ArgumentGetter<T> argumentGetter) {
		register(type, (ArgumentTypeProvider<T>) argumentType, argumentGetter);
	}
}
