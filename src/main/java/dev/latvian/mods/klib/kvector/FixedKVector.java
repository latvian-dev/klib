package dev.latvian.mods.klib.kvector;

import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.knumber.KNumberContext;
import dev.latvian.mods.klib.registry.CustomRegistryType;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public record FixedKVector(@Nullable UnitType<RegistryFriendlyByteBuf, KVector> typeOverride, Vec3 vec) implements KVector {
	public static final DynamicType<RegistryFriendlyByteBuf, KVector> TYPE = DynamicType.create(
		"fixed",
		"vec",
		MCCodecs.VEC3S,
		MCStreamCodecs.VEC3,
		FixedKVector::new,
		FixedKVector::vec
	);

	public FixedKVector(Vec3 vec) {
		this(null, vec);
	}

	@Override
	public CustomRegistryType<RegistryFriendlyByteBuf, KVector> type() {
		return typeOverride == null ? TYPE : typeOverride;
	}

	@Override
	public Vec3 get(KNumberContext ctx) {
		return vec;
	}

	@Override
	public KVector offset(KVector other) {
		if (other instanceof FixedKVector v) {
			return KVector.of(
				vec.x + v.vec.x,
				vec.y + v.vec.y,
				vec.z + v.vec.z
			);
		}

		return KVector.super.offset(other);
	}

	@Override
	public KVector scale(KVector other) {
		if (other instanceof FixedKVector v) {
			return KVector.of(
				vec.x * v.vec.x,
				vec.y * v.vec.y,
				vec.z * v.vec.z
			);
		}

		return KVector.super.scale(other);
	}

	@Override
	public @NonNull String toString() {
		return vec.toString();
	}
}
