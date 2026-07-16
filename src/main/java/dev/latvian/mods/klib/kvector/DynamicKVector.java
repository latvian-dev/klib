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

public record DynamicKVector(Ref<KNumber> x, Ref<KNumber> y, Ref<KNumber> z) implements KVector {
	public static final DynamicType<RegistryFriendlyByteBuf, KVector> TYPE = DynamicType.create(
		"dynamic",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KNumber.CODEC.fieldOf("x").forGetter(DynamicKVector::x),
			KNumber.CODEC.fieldOf("y").forGetter(DynamicKVector::y),
			KNumber.CODEC.fieldOf("z").forGetter(DynamicKVector::z)
		).apply(instance, DynamicKVector::new)),
		CompositeStreamCodec.of(
			KNumber.STREAM_CODEC, DynamicKVector::x,
			KNumber.STREAM_CODEC, DynamicKVector::y,
			KNumber.STREAM_CODEC, DynamicKVector::z,
			DynamicKVector::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KVector> type() {
		return TYPE;
	}

	@Override
	public Vec3 get(KNumberContext ctx) {
		var px = x.value().get(ctx);
		var py = y.value().get(ctx);
		var pz = z.value().get(ctx);

		if (px == null || py == null || pz == null) {
			return null;
		}

		return KMath.vec3(px, py, pz);
	}
}
