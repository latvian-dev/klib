package dev.latvian.mods.klib.math;

import dev.latvian.mods.klib.data.DataType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;

public enum MovementType implements StringRepresentable {
	ANGLED("angled"),
	CIRCULAR("circular"),
	SPHERICAL("spherical"),
	SQUARE("square"),
	CUBIC("cubic");

	public static final MovementType[] VALUES = values();
	public static final DataType<MovementType> DATA_TYPE = DataType.of(VALUES);

	private final String name;

	MovementType(String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return name;
	}

	public Vec3f delta(RandomSource random, float radius, float deviate, Rotation rotation) {
		if (this == SQUARE || this == CUBIC) {
			return new Vec3f(
				Mth.lerp(random.nextFloat(), -radius, radius),
				this == SQUARE ? 0F : Mth.lerp(random.nextFloat(), -radius, radius),
				Mth.lerp(random.nextFloat(), -radius, radius)
			);
		}

		var yaw = this == ANGLED ? rotation.yaw() : random.nextFloat() * 360F;
		var pitch = this == ANGLED ? rotation.pitch() : this == SPHERICAL ? (random.nextFloat() * 180F - 90F) : 0F;

		if (deviate != 0F) {
			yaw += random.nextFloat() * deviate - deviate / 2F;
			pitch += random.nextFloat() * deviate - deviate / 2F;
		}

		return Rotation.deg(yaw, pitch).lookVec3f(radius);
	}
}
