package dev.latvian.mods.klib.util;

import com.mojang.datafixers.util.Either;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.data.DataTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

public sealed interface IntOrUUID permits IntOrUUID.IntImpl, IntOrUUID.UUIDImpl {
	DataType<IntOrUUID> DATA_TYPE = DataType.either(DataTypes.VAR_INT, DataTypes.UUID, IntImpl::new, UUIDImpl::new, IntOrUUID::either, IntOrUUID.class);

	Either<Integer, UUID> either();

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
