package dev.latvian.mods.klib.knumber;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.codec.KLibCodecErrors;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.StringPrefixList;
import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public interface KNumber extends CustomRegistryValue<RegistryFriendlyByteBuf, KNumber> {
	DataResult<Double> ERROR_NOT_A_NUMBER = KLibCodecErrors.error("Not a number");
	StringPrefixList<KNumber> PREFIX_LIST = new StringPrefixList<>(KNumber::isStringLiteral);

	CustomRegistry<RegistryFriendlyByteBuf, KNumber> REGISTRY = CustomRegistry.<RegistryFriendlyByteBuf, KNumber>builder("knumber")
		.customCodec(directCodec -> KLibCodecs.or(List.of(
			Codec.DOUBLE.flatComapMap(KNumber::of, knum -> {
				if (knum instanceof FixedKNumber num) {
					return DataResult.success(num.number());
				} else {
					return ERROR_NOT_A_NUMBER;
				}
			}),
			PREFIX_LIST.codec(),
			directCodec
		)))
		.build();

	static UnitType<RegistryFriendlyByteBuf, KNumber> simple(String id, Function<KNumberContext, Double> factory) {
		return UnitType.create(id, type -> new SimpleKNumber(type, factory));
	}

	UnitType<RegistryFriendlyByteBuf, KNumber> ZERO = UnitType.create("zero", type -> new FixedKNumber(type, 0D));
	UnitType<RegistryFriendlyByteBuf, KNumber> ONE = UnitType.create("one", type -> new FixedKNumber(type, 1D));
	UnitType<RegistryFriendlyByteBuf, KNumber> PROGRESS = simple("progress", ctx -> ctx.progress);
	UnitType<RegistryFriendlyByteBuf, KNumber> TICK = simple("tick", ctx -> ctx.tick);
	UnitType<RegistryFriendlyByteBuf, KNumber> MAX_TICK = simple("max_tick", ctx -> ctx.maxTick);
	UnitType<RegistryFriendlyByteBuf, KNumber> GAME_TIME = simple("game_time", ctx -> ctx.gameTime);
	UnitType<RegistryFriendlyByteBuf, KNumber> GAME_DAY = simple("game_day", ctx -> ctx.gameDay);
	UnitType<RegistryFriendlyByteBuf, KNumber> CLOCK = simple("clock", ctx -> ctx.clock);

	Codec<Ref<KNumber>> CODEC = REGISTRY.codec();
	StreamCodec<RegistryFriendlyByteBuf, Ref<KNumber>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<KNumber>> DATA_TYPE = REGISTRY.dataType();

	static KNumber of(double number) {
		if (number == 0D) {
			return ZERO.value();
		} else if (number == 1D) {
			return ONE.value();
		} else {
			return new FixedKNumber(number);
		}
	}

	static void builtInTypes(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, KNumber> registry) {
		registry.register(ZERO);
		registry.register(ONE);
		registry.register(PROGRESS);
		registry.register(TICK);
		registry.register(MAX_TICK);
		registry.register(GAME_TIME);
		registry.register(GAME_DAY);
		registry.register(CLOCK);

		registry.register(FixedKNumber.TYPE);
		registry.register(InterpolatedKNumber.TYPE);
		registry.register(OffsetKNumber.TYPE);
		registry.register(ScaledKNumber.TYPE);
		registry.register(VariableKNumber.TYPE);
		registry.register(IfKNumber.TYPE);

		registry.register(EntityKNumber.TYPE);

		registry.register(RandomKNumber.TYPE);
		registry.register(SinKNumber.TYPE);
		registry.register(CosKNumber.TYPE);
		registry.register(Atan2KNumber.TYPE);
		registry.register(ClampedKNumber.TYPE);

		PREFIX_LIST.addSimple("#", VariableKNumber::new);
	}

	@Override
	default CustomRegistry<RegistryFriendlyByteBuf, KNumber> getRegistry() {
		return REGISTRY;
	}

	@Nullable
	Double get(KNumberContext ctx);

	default boolean isStringLiteral() {
		return false;
	}

	default double getOr(KNumberContext ctx, double def) {
		Double value = get(ctx);
		return value == null ? def : value;
	}

	default double getOrNaN(KNumberContext ctx) {
		return getOr(ctx, Double.NaN);
	}

	default KNumber offset(KNumber other) {
		return new OffsetKNumber(ref(), other.ref());
	}

	default KNumber scale(KNumber other) {
		return new ScaledKNumber(ref(), other.ref());
	}
}
