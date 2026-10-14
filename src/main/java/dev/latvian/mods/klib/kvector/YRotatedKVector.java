package dev.latvian.mods.klib.kvector;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.knumber.KNumber;
import dev.latvian.mods.klib.knumber.KNumberContext;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record YRotatedKVector(Ref<KVector> vector, Ref<KNumber> angle) implements KVector {
	public static final DynamicType<RegistryFriendlyByteBuf, KVector> TYPE = DynamicType.create(
		"y_rotated",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KVector.CODEC.fieldOf("vector").forGetter(YRotatedKVector::vector),
			KNumber.CODEC.fieldOf("angle").forGetter(YRotatedKVector::angle)
		).apply(instance, YRotatedKVector::new)),
		CompositeStreamCodec.of(
			KVector.STREAM_CODEC, YRotatedKVector::vector,
			KNumber.STREAM_CODEC, YRotatedKVector::angle,
			YRotatedKVector::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KVector> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Vec3 get(KNumberContext ctx) {
		var vector = this.vector.value().get(ctx);

		if (vector == null) {
			return null;
		}

		var angle = this.angle.value().get(ctx);

		if (angle == null) {
			return null;
		}

		var a = Math.toRadians(angle);
		var sin = org.joml.Math.sin(a);
		var cos = org.joml.Math.cosFromSin(sin, a);
		return KMath.vec3(vector.x * cos + vector.z * sin, vector.y, -vector.x * sin + vector.z * cos);
	}
}
