package dev.latvian.mods.klib.kvector;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.knumber.KNumberContext;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.registry.DynamicType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record VariableKVector(String name) implements KVector {
	public static final DynamicType<RegistryFriendlyByteBuf, KVector> TYPE = DynamicType.create(
		"variable",
		"name",
		Codec.STRING,
		ByteBufCodecs.STRING_UTF8,
		VariableKVector::new,
		VariableKVector::name
	);

	@Override
	public DynamicType<RegistryFriendlyByteBuf, KVector> type() {
		return TYPE;
	}

	@Override
	@Nullable
	public Vec3 get(KNumberContext ctx) {
		var pos = ctx.getVector(name);

		if (pos == null) {
			var num = ctx.getNumber(name);

			if (num != null) {
				var d = num.value().get(ctx);

				if (d == null) {
					return null;
				}

				return KMath.vec3(d, d, d);
			}
		}

		return pos == null ? null : pos.value().get(ctx);
	}

	@Override
	@NotNull
	public String toString() {
		return "#" + name;
	}

	@Override
	public boolean isStringLiteral() {
		return true;
	}
}