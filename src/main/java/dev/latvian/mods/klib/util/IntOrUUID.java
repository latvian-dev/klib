package dev.latvian.mods.klib.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

public sealed interface IntOrUUID permits IntOrUUID.IntImpl, IntOrUUID.UUIDImpl {
	Codec<IntOrUUID> CODEC = Codec.either(ExtraCodecs.NON_NEGATIVE_INT, KLibCodecs.UUID).xmap(e -> e.map(IntImpl::new, UUIDImpl::new), IntOrUUID::either);
	StreamCodec<ByteBuf, IntOrUUID> STREAM_CODEC = ByteBufCodecs.either(ByteBufCodecs.VAR_INT, KLibStreamCodecs.UUID).map(e -> e.map(IntImpl::new, UUIDImpl::new), IntOrUUID::either);
	DataType<IntOrUUID> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, IntOrUUID.class);

	static IntOrUUID of(int value) {
		return new IntImpl(value);
	}

	static IntOrUUID of(UUID value) {
		return new UUIDImpl(value);
	}

	Either<Integer, UUID> either();

	Optional<Integer> optionalInteger();

	OptionalInt optionalInt();

	Optional<UUID> optionalUUID();

	boolean testEntity(Entity entity);

	@Nullable
	Entity getEntity(Level level);

	record IntImpl(int value, OptionalInt optionalValue) implements IntOrUUID {
		public IntImpl(int value) {
			this(value, OptionalInt.of(value));
		}

		@Override
		public Either<Integer, UUID> either() {
			return Either.left(value);
		}

		@Override
		public Optional<Integer> optionalInteger() {
			return Optional.of(value);
		}

		@Override
		public OptionalInt optionalInt() {
			return optionalValue;
		}

		@Override
		public Optional<UUID> optionalUUID() {
			return Optional.empty();
		}

		@Override
		public boolean testEntity(Entity entity) {
			return entity.getId() == value;
		}

		@Override
		@Nullable
		public Entity getEntity(Level level) {
			return level.getEntity(value);
		}

		@Override
		public String toString() {
			return Integer.toString(value);
		}
	}

	record UUIDImpl(UUID value, Optional<UUID> optionalValue) implements IntOrUUID {
		public UUIDImpl(UUID value) {
			this(value, Optional.of(value));
		}

		@Override
		public Either<Integer, UUID> either() {
			return Either.right(value);
		}

		@Override
		public Optional<Integer> optionalInteger() {
			return Optional.empty();
		}

		@Override
		public OptionalInt optionalInt() {
			return OptionalInt.empty();
		}

		@Override
		public Optional<UUID> optionalUUID() {
			return optionalValue;
		}

		@Override
		public boolean testEntity(Entity entity) {
			return entity.getUUID().equals(value);
		}

		@Override
		@Nullable
		public Entity getEntity(Level level) {
			return level.getEntity(value);
		}

		@Override
		public @NotNull String toString() {
			return value.toString();
		}
	}
}
