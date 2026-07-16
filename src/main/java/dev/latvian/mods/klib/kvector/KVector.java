package dev.latvian.mods.klib.kvector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.codec.KLibCodecErrors;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.entity.PositionType;
import dev.latvian.mods.klib.entity.filter.EntityFilter;
import dev.latvian.mods.klib.entity.filter.ExactEntityFilter;
import dev.latvian.mods.klib.knumber.FixedKNumber;
import dev.latvian.mods.klib.knumber.KNumber;
import dev.latvian.mods.klib.knumber.KNumberContext;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.StringPrefixList;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.util.IntOrUUID;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;
import org.joml.Vector3fc;

import java.util.List;
import java.util.function.Function;

public interface KVector extends CustomRegistryValue<RegistryFriendlyByteBuf, KVector> {
	DataResult<Vec3> ERROR_NOT_A_VECTOR = KLibCodecErrors.error("Not a vector");
	DataResult<Ref<KNumber>> ERROR_NOT_A_NUMBER = KLibCodecErrors.error("Not a number");
	StringPrefixList<KVector> PREFIX_LIST = new StringPrefixList<>(KVector::isStringLiteral);

	CustomRegistry<RegistryFriendlyByteBuf, KVector> REGISTRY = CustomRegistry.<RegistryFriendlyByteBuf, KVector>builder("kvector")
		.customCodec(directCodec -> KLibCodecs.or(List.of(
			MCCodecs.VEC3.<KVector>flatComapMap(KVector::of, kvec -> {
				if (kvec instanceof FixedKVector v) {
					return DataResult.success(v.vec());
				} else {
					return ERROR_NOT_A_VECTOR;
				}
			}),
			PREFIX_LIST.codec(),
			directCodec,
			KNumber.CODEC.<KVector>flatComapMap(ScalarKVector::new, kvec -> {
				if (kvec instanceof ScalarKVector v) {
					return DataResult.success(v.number());
				} else {
					return ERROR_NOT_A_NUMBER;
				}
			})
		)))
		.build();

	static UnitType<RegistryFriendlyByteBuf, KVector> simple(String id, Function<KNumberContext, Vec3> factory) {
		return UnitType.create(id, type -> new SimpleKVector(type, factory));
	}

	UnitType<RegistryFriendlyByteBuf, KVector> ZERO = UnitType.create("zero", new FixedKVector(Vec3.ZERO));
	UnitType<RegistryFriendlyByteBuf, KVector> ONE = UnitType.create("one", new FixedKVector(KMath.ONE_VEC3));
	UnitType<RegistryFriendlyByteBuf, KVector> ORIGIN = simple("origin", ctx -> ctx.originPos);
	UnitType<RegistryFriendlyByteBuf, KVector> SOURCE = simple("source", ctx -> ctx.sourcePos);
	UnitType<RegistryFriendlyByteBuf, KVector> TARGET = simple("target", ctx -> ctx.targetPos);

	Codec<Ref<KVector>> CODEC = REGISTRY.codec();
	StreamCodec<RegistryFriendlyByteBuf, Ref<KVector>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<KVector>> DATA_TYPE = REGISTRY.dataType();

	static FixedKVector of(Vec3 vec) {
		if (vec.x == 0D && vec.y == 0D && vec.z == 0D) {
			return (FixedKVector) ZERO.value();
		} else if (vec.x == 1D && vec.y == 1D && vec.z == 1D) {
			return (FixedKVector) ONE.value();
		} else {
			return new FixedKVector(vec);
		}
	}

	static KVector of(Position position) {
		return of(position instanceof Vec3 v ? v : new Vec3(position.x(), position.y(), position.z()));
	}

	static KVector of(double x, double y, double z) {
		return of(KMath.vec3(x, y, z));
	}

	static KVector of(Vec3i pos) {
		return of(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}

	static KVector of(Vector3dc pos) {
		return of(pos.x(), pos.y(), pos.z());
	}

	static KVector of(Vector3fc pos) {
		return of(pos.x(), pos.y(), pos.z());
	}

	static void builtInTypes(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, KVector> registry) {
		registry.register(ZERO);
		registry.register(ONE);
		registry.register(ORIGIN);
		registry.register(SOURCE);
		registry.register(TARGET);

		registry.register(FixedKVector.TYPE);
		registry.register(InterpolatedKVector.TYPE);
		registry.register(DynamicKVector.TYPE);
		registry.register(ScalarKVector.TYPE);
		registry.register(OffsetKVector.TYPE);
		registry.register(ScaledKVector.TYPE);
		registry.register(FollowingEntityKVector.TYPE);
		registry.register(VariableKVector.TYPE);
		registry.register(IfKVector.TYPE);
		registry.register(PivotingKVector.TYPE);
		registry.register(YRotatedKVector.TYPE);
		registry.register(GroundKVector.TYPE);

		PREFIX_LIST.addSimple("#", VariableKVector::new);
	}

	static KVector ofRotation(double yaw, double pitch) {
		var yc = Math.cos(Math.toRadians(-yaw) - Math.PI);
		var ys = Math.sin(Math.toRadians(-yaw) - Math.PI);
		var pc = -Math.cos(Math.toRadians(-pitch));
		var ps = Math.sin(Math.toRadians(-pitch));
		return of(ys * pc * 8D, ps * 8D, yc * pc * 8D);
	}

	static KVector following(Ref<EntityFilter> entityFilter, PositionType type) {
		return new FollowingEntityKVector(entityFilter, type);
	}

	static KVector following(Entity entity, PositionType type) {
		return following(new ExactEntityFilter(IntOrUUID.of(entity.getId())).ref(), type);
	}

	static KVector scalar(KNumber number) {
		if (number instanceof FixedKNumber n) {
			return of(KMath.vec3(n.number()));
		}

		return new ScalarKVector(number.ref());
	}

	@Override
	default CustomRegistry<RegistryFriendlyByteBuf, KVector> getRegistry() {
		return REGISTRY;
	}

	@Nullable
	Vec3 get(KNumberContext ctx);

	default boolean isStringLiteral() {
		return false;
	}

	default KVector offset(KVector other) {
		return new OffsetKVector(ref(), other.ref());
	}

	default KVector scale(KVector other) {
		return new ScaledKVector(ref(), other.ref());
	}
}
