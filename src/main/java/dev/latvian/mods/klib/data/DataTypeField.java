package dev.latvian.mods.klib.data;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;
import dev.latvian.mods.klib.util.Cast;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public interface DataTypeField<C, T> {
	record RequiredField<C, T>(DataType<T> type, String name, Function<C, T> getter) implements DataTypeField<C, T> {
		@Override
		public Class<T> typeClass() {
			return type.typeClass();
		}

		@Override
		public T get(C input) {
			return getter.apply(input);
		}

		@Override
		public boolean canBeOptional() {
			return false;
		}

		@Override
		public boolean shouldEncode(T value) {
			return false;
		}

		@Override
		public <O> void encode(DynamicOps<O> ops, RecordBuilder<O> builder, T value) {
			builder.add(name, type.codec().encodeStart(ops, value));
		}

		@Override
		public <O> DataResult<T> decode(DynamicOps<O> ops, @Nullable O value) {
			if (value == null) {
				return DataResult.error(() -> "Missing key '" + name + "'");
			}

			return type.codec().parse(ops, value);
		}

		@Override
		public void encode(ByteBuf buf, T value) {
			type.streamCodec().encode(Cast.to(buf), value);
		}

		@Override
		public T decode(ByteBuf buf, boolean present) {
			return type.streamCodec().decode(Cast.to(buf));
		}
	}

	record OptionalField<C, T>(DataType<T> type, String name, Function<C, Optional<T>> getter) implements DataTypeField<C, Optional<T>> {
		@Override
		public Class<Optional<T>> typeClass() {
			return Cast.to(Optional.class);
		}

		@Override
		@Nullable
		public Optional<T> get(C input) {
			return getter.apply(input);
		}

		@Override
		public boolean canBeOptional() {
			return true;
		}

		@Override
		public boolean shouldEncode(Optional<T> value) {
			return value.isEmpty();
		}

		@Override
		public <O> void encode(DynamicOps<O> ops, RecordBuilder<O> builder, Optional<T> value) {
			builder.add(name, type.codec().encodeStart(ops, value.get()));
		}

		@Override
		public <O> DataResult<Optional<T>> decode(DynamicOps<O> ops, @Nullable O value) {
			if (value == null) {
				return DataResult.success(Optional.empty());
			} else {
				return type.codec().parse(ops, value).map(Optional::ofNullable);
			}
		}

		@Override
		public void encode(ByteBuf buf, Optional<T> value) {
			type.streamCodec().encode(Cast.to(buf), value.get());
		}

		@Override
		public Optional<T> decode(ByteBuf buf, boolean present) {
			if (present) {
				return Optional.of(type.streamCodec().decode(Cast.to(buf)));
			} else {
				return Optional.empty();
			}
		}
	}

	record OptionalDefaultField<C, T>(DataType<T> type, String name, Function<C, T> getter, T defaultValue) implements DataTypeField<C, T> {
		@Override
		public Class<T> typeClass() {
			return type.typeClass();
		}

		@Override
		@Nullable
		public T get(C input) {
			return getter.apply(input);
		}

		@Override
		public boolean canBeOptional() {
			return true;
		}

		@Override
		public boolean shouldEncode(T value) {
			return Objects.equals(value, defaultValue);
		}

		@Override
		public <O> void encode(DynamicOps<O> ops, RecordBuilder<O> builder, T value) {
			builder.add(name, type.codec().encodeStart(ops, value));
		}

		@Override
		public <O> DataResult<T> decode(DynamicOps<O> ops, @Nullable O value) {
			if (value == null) {
				return DataResult.success(defaultValue);
			} else {
				return type.codec().parse(ops, value);
			}
		}

		@Override
		public void encode(ByteBuf buf, T value) {
			type.streamCodec().encode(Cast.to(buf), value);
		}

		@Override
		public T decode(ByteBuf buf, boolean present) {
			if (present) {
				return type.streamCodec().decode(Cast.to(buf));
			} else {
				return defaultValue;
			}
		}
	}

	Class<T> typeClass();

	String name();

	T get(C input);

	boolean canBeOptional();

	boolean shouldEncode(T value);

	<O> void encode(DynamicOps<O> ops, RecordBuilder<O> builder, T value);

	<O> DataResult<T> decode(DynamicOps<O> ops, @Nullable O value);

	void encode(ByteBuf buf, T value);

	T decode(ByteBuf buf, boolean present);
}
