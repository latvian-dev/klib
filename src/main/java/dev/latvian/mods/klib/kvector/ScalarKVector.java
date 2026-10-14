package dev.latvian.mods.klib.kvector;

import dev.latvian.mods.klib.knumber.KNumber;
import dev.latvian.mods.klib.knumber.KNumberContext;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record ScalarKVector(Ref<KNumber> number) implements KVector {
	public static final DynamicType<RegistryFriendlyByteBuf, KVector> TYPE = DynamicType.create(
		"scalar",
		"number",
		KNumber.CODEC,
		KNumber.STREAM_CODEC,
		ScalarKVector::new,
		ScalarKVector::number
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KVector> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Vec3 get(KNumberContext ctx) {
		var n = number.value().get(ctx);

		if (n == null) {
			return null;
		}

		return KMath.vec3(n, n, n);
	}
}
