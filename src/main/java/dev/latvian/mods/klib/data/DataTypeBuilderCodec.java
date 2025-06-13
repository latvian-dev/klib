package dev.latvian.mods.klib.data;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import dev.latvian.mods.klib.util.Cast;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class DataTypeBuilderCodec<C> extends MapCodec<C> {
	private final List<DataTypeField<C, ?>> fields;
	private final Function<Object[], C> constructor;

	public DataTypeBuilderCodec(List<DataTypeField<C, ?>> fields, Function<Object[], C> constructor) {
		this.fields = fields;
		this.constructor = constructor;
	}

	@Override
	public <O> Stream<O> keys(DynamicOps<O> ops) {
		return Stream.empty();
	}

	@Override
	public <O> DataResult<C> decode(DynamicOps<O> ops, MapLike<O> input) {
		var args = new Object[fields.size()];

		for (int i = 0; i < fields.size(); i++) {
			var field = fields.get(i);
			var v = input.get(field.name());
			var r = field.decode(ops, v);

			if (r.isError()) {
				return DataResult.error(() -> "Failed to decode field: " + field.name() + " - " + r.error().get().message());
			}

			args[i] = r.getOrThrow();
		}

		try {
			return DataResult.success(constructor.apply(args));
		} catch (Throwable ex) {
			return DataResult.error(ex::getMessage);
		}
	}

	@Override
	public <O> RecordBuilder<O> encode(C input, DynamicOps<O> ops, RecordBuilder<O> builder) {
		for (var c : fields) {
			var v = c.get(input);

			if (c.shouldEncode(Cast.to(v))) {
				c.encode(ops, builder, Cast.to(v));
			}
		}

		return builder;
	}
}
