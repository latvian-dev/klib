package dev.latvian.mods.klib.kvector;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.knumber.KNumberContext;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record OffsetKVector(Ref<KVector> a, Ref<KVector> b) implements KVector {
	public static final DynamicType<RegistryFriendlyByteBuf, KVector> TYPE = DynamicType.create(
		"offset",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			KVector.CODEC.fieldOf("a").forGetter(OffsetKVector::a),
			KVector.CODEC.fieldOf("b").forGetter(OffsetKVector::b)
		).apply(instance, OffsetKVector::new)),
		CompositeStreamCodec.of(
			KVector.STREAM_CODEC, OffsetKVector::a,
			KVector.STREAM_CODEC, OffsetKVector::b,
			OffsetKVector::new
		)
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KVector> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Vec3 get(KNumberContext ctx) {
		var a = this.a.value().get(ctx);
		var b = this.b.value().get(ctx);

		if (a == null || b == null) {
			return null;
		}

		return new Vec3(a.x + b.x, a.y + b.y, a.z + b.z);
	}
}
