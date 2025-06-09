package dev.latvian.mods.klib.data;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.KLibMod;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.color.Gradient;
import dev.latvian.mods.klib.math.MovementType;
import dev.latvian.mods.klib.math.Range;
import dev.latvian.mods.klib.shape.Shape;
import dev.latvian.mods.klib.util.ID;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public interface DataTypes {
	DataType<Boolean> BOOL = DataType.of(Codec.BOOL, ByteBufCodecs.BOOL, Boolean.class, v -> v ? 1 : 0);
	DataType<Integer> INT = DataType.of(Codec.INT, ByteBufCodecs.INT, Integer.class, v -> v);
	DataType<Integer> VAR_INT = DataType.of(Codec.INT, ByteBufCodecs.VAR_INT, Integer.class, v -> v);
	DataType<Long> LONG = DataType.of(Codec.LONG, ByteBufCodecs.LONG, Long.class, v -> v);
	DataType<Long> VAR_LONG = DataType.of(Codec.LONG, ByteBufCodecs.VAR_LONG, Long.class, v -> v);
	DataType<Float> FLOAT = DataType.of(Codec.FLOAT, ByteBufCodecs.FLOAT, Float.class, v -> v);
	DataType<Double> DOUBLE = DataType.of(Codec.DOUBLE, ByteBufCodecs.DOUBLE, Double.class, v -> v);
	DataType<String> STRING = DataType.of(Codec.STRING, ByteBufCodecs.STRING_UTF8, String.class, String::length);
	DataType<UUID> UUID = DataType.of(KLibCodecs.UUID, KLibStreamCodecs.UUID, UUID.class);

	DataType<Component> TEXT_COMPONENT = DataType.of(ComponentSerialization.CODEC, ComponentSerialization.STREAM_CODEC, Component.class, v -> v.getString().length());
	DataType<Mirror> MIRROR = DataType.of(Mirror.values());
	DataType<Rotation> BLOCK_ROTATION = DataType.of(Rotation.values());
	DataType<LiquidSettings> LIQUID_SETTINGS = DataType.of(LiquidSettings.values());
	DataType<InteractionHand> HAND = DataType.of(InteractionHand.values());
	DataType<SoundSource> SOUND_SOURCE = DataType.of(SoundSource.values());
	DataType<ItemStack> ITEM_STACK = DataType.of(ItemStack.OPTIONAL_CODEC, ItemStack.OPTIONAL_STREAM_CODEC, ItemStack.class);
	DataType<ParticleOptions> PARTICLE_OPTIONS = DataType.of(ParticleTypes.CODEC, ParticleTypes.STREAM_CODEC, ParticleOptions.class);
	DataType<BlockState> BLOCK_STATE = DataType.of(MCCodecs.BLOCK_STATE, MCStreamCodecs.BLOCK_STATE, BlockState.class);
	DataType<FluidState> FLUID_STATE = DataType.of(MCCodecs.FLUID_STATE, MCStreamCodecs.FLUID_STATE, FluidState.class);
	DataType<Vec3> VEC3 = DataType.of(Vec3.CODEC, Vec3.STREAM_CODEC, Vec3.class, Vec3::length);
	DataType<BlockPos> BLOCK_POS = DataType.of(BlockPos.CODEC, BlockPos.STREAM_CODEC, BlockPos.class, v -> Vec3.atLowerCornerOf(v).length());

	static void register() {
		DataType.register(ID.java("bool"), BOOL, (self, ctx) -> BoolArgumentType.bool(), BoolArgumentType::getBool);
		DataType.register(ID.java("int"), INT, (self, ctx) -> IntegerArgumentType.integer(), IntegerArgumentType::getInteger);
		DataType.register(ID.java("var_int"), VAR_INT, (self, ctx) -> IntegerArgumentType.integer(), IntegerArgumentType::getInteger);
		DataType.register(ID.java("long"), LONG, (self, ctx) -> LongArgumentType.longArg(), LongArgumentType::getLong);
		DataType.register(ID.java("var_long"), VAR_LONG, (self, ctx) -> LongArgumentType.longArg(), LongArgumentType::getLong);
		DataType.register(ID.java("float"), FLOAT, (self, ctx) -> FloatArgumentType.floatArg(), FloatArgumentType::getFloat);
		DataType.register(ID.java("double"), DOUBLE, (self, ctx) -> DoubleArgumentType.doubleArg(), DoubleArgumentType::getDouble);
		DataType.register(ID.java("string"), STRING, (self, ctx) -> StringArgumentType.string(), StringArgumentType::getString);
		DataType.register(ID.java("uuid"), UUID, (self, ctx) -> UuidArgument.uuid(), (ctx, name) -> ctx.getArgument(name, UUID.class));

		DataType.register(ID.mc("id"), ID.DATA_TYPE, (self, ctx) -> ResourceLocationArgument.id(), (ctx, name) -> ctx.getArgument(name, ResourceLocation.class));
		DataType.register(ID.mc("text_component"), TEXT_COMPONENT, (self, ctx) -> ComponentArgument.textComponent(ctx), (ctx, name) -> ctx.getArgument(name, Component.class));
		DataType.register(ID.mc("mirror"), MIRROR);
		DataType.register(ID.mc("rotation"), BLOCK_ROTATION);
		DataType.register(ID.mc("liquid_settings"), LIQUID_SETTINGS);
		DataType.register(ID.mc("hand"), HAND);
		DataType.register(ID.mc("sound_source"), SOUND_SOURCE);
		DataType.register(ID.mc("item_stack"), ITEM_STACK);
		DataType.register(ID.mc("particle_options"), PARTICLE_OPTIONS);
		DataType.register(ID.mc("block_state"), BLOCK_STATE);
		DataType.register(ID.mc("fluid_state"), FLUID_STATE);
		DataType.register(ID.mc("vec3"), VEC3);
		DataType.register(ID.mc("block_pos"), BLOCK_POS);

		DataType.register(KLibMod.id("color"), Color.DATA_TYPE);
		DataType.register(KLibMod.id("gradient"), Gradient.DATA_TYPE);
		DataType.register(KLibMod.id("shape"), Shape.DATA_TYPE);
		DataType.register(KLibMod.id("rotation"), dev.latvian.mods.klib.math.Rotation.DATA_TYPE);
		DataType.register(KLibMod.id("movement_type"), MovementType.DATA_TYPE);
		DataType.register(KLibMod.id("range"), Range.DATA_TYPE);
	}
}
