package dev.latvian.mods.klib.entity.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.latvian.mods.klib.codec.KLibCodecErrors;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.entity.EntityUtils;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.util.CommandEvaluable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.ToDoubleFunction;

public interface EntityNumber extends ToDoubleFunction<Entity>, CustomRegistryValue<RegistryFriendlyByteBuf, EntityNumber>, CommandEvaluable {
	CustomRegistry<RegistryFriendlyByteBuf, EntityNumber> REGISTRY = CustomRegistry.<RegistryFriendlyByteBuf, EntityNumber>builder("entity_number")
		.customCodec(directCodec -> KLibCodecs.or(List.of(
			Codec.DOUBLE.flatComapMap(EntityNumber::of, val -> {
				if (val instanceof FixedEntityNumber fixed) {
					return DataResult.success(fixed.number());
				} else {
					return KLibCodecErrors.NOT_A_DOUBLE;
				}
			}),
			directCodec
		)))
		.build();

	UnitType<RegistryFriendlyByteBuf, EntityNumber> ZERO = UnitType.create("zero", type -> new FixedEntityNumber(type, 0D));
	UnitType<RegistryFriendlyByteBuf, EntityNumber> ONE = UnitType.create("one", type -> new FixedEntityNumber(type, 1D));

	static UnitType<RegistryFriendlyByteBuf, EntityNumber> simple(String name, ToDoubleFunction<Entity> function) {
		return UnitType.create(name, type -> new SimpleEntityNumber(type, function));
	}

	UnitType<RegistryFriendlyByteBuf, EntityNumber> X = simple("x", Entity::getX);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> Y = simple("y", Entity::getY);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> Z = simple("z", Entity::getZ);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> PITCH = simple("pitch", Entity::getXRot);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> YAW = simple("yaw", Entity::getYRot);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> WIDTH = simple("width", Entity::getBbWidth);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> HEIGHT = simple("height", Entity::getBbHeight);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> HEAD_YAW = simple("head_yaw", Entity::getYHeadRot);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> BODY_YAW = simple("body_yaw", e -> e instanceof LivingEntity l ? l.yBodyRot : e.getYRot());
	UnitType<RegistryFriendlyByteBuf, EntityNumber> VISUAL_YAW = simple("visual_yaw", Entity::getVisualRotationYInDegrees);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> BLOCK_X = simple("block_x", Entity::getBlockX);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> BLOCK_Y = simple("block_y", Entity::getBlockY);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> BLOCK_Z = simple("block_z", Entity::getBlockZ);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> AIR_SUPPLY = simple("air_supply", Entity::getAirSupply);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> MAX_AIR_SUPPLY = simple("max_air_supply", Entity::getMaxAirSupply);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> HEALTH = simple("health", EntityUtils::getHealth);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> MAX_HEALTH = simple("max_health", EntityUtils::getMaxHealth);
	UnitType<RegistryFriendlyByteBuf, EntityNumber> RELATIVE_HEALTH = simple("relative_health", EntityUtils::getRelativeHealth);

	static EntityNumber of(double value) {
		return value == 0D ? ZERO.value() : value == 1D ? ONE.value() : new FixedEntityNumber(null, value);
	}

	Codec<Ref<EntityNumber>> CODEC = REGISTRY.codec();
	StreamCodec<RegistryFriendlyByteBuf, Ref<EntityNumber>> STREAM_CODEC = REGISTRY.streamCodec();

	static void builtInTypes(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, EntityNumber> registry) {
		registry.register(ZERO);
		registry.register(ONE);
		registry.register(X);
		registry.register(Y);
		registry.register(Z);
		registry.register(PITCH);
		registry.register(YAW);
		registry.register(WIDTH);
		registry.register(HEIGHT);
		registry.register(HEAD_YAW);
		registry.register(BODY_YAW);
		registry.register(VISUAL_YAW);
		registry.register(BLOCK_X);
		registry.register(BLOCK_Y);
		registry.register(BLOCK_Z);
		registry.register(AIR_SUPPLY);
		registry.register(MAX_AIR_SUPPLY);
		registry.register(HEALTH);
		registry.register(MAX_HEALTH);
		registry.register(RELATIVE_HEALTH);

		registry.register(FixedEntityNumber.TYPE);
	}

	@Override
	default CustomRegistry<RegistryFriendlyByteBuf, EntityNumber> getRegistry() {
		return REGISTRY;
	}

	@Override
	@Nullable
	default Component evaluate(CommandSourceStack source) {
		if (source.getEntity() == null) {
			return null;
		}

		var eval = applyAsDouble(source.getEntity());

		if (Double.isNaN(eval)) {
			return Component.literal("NaN").withStyle(ChatFormatting.YELLOW);
		} else {
			return Component.literal(Double.toString(eval)).withStyle(ChatFormatting.GREEN);
		}
	}
}
