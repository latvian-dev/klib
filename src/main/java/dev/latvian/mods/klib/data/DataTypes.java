package dev.latvian.mods.klib.data;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.block.PositionedBlock;
import dev.latvian.mods.klib.codec.KLibCodecs;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.entity.PositionType;
import dev.latvian.mods.klib.gradient.Gradient;
import dev.latvian.mods.klib.interpolation.Interpolation;
import dev.latvian.mods.klib.math.ClipPosition;
import dev.latvian.mods.klib.math.InterpolatedDouble;
import dev.latvian.mods.klib.math.InterpolatedFloat;
import dev.latvian.mods.klib.math.Line;
import dev.latvian.mods.klib.math.MovementType;
import dev.latvian.mods.klib.math.Range;
import dev.latvian.mods.klib.math.Vec3f;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.shape.Shape;
import dev.latvian.mods.klib.util.Hex32;
import dev.latvian.mods.klib.util.Hex64;
import dev.latvian.mods.klib.util.ID;
import dev.latvian.mods.klib.util.IntOrUUID;
import dev.latvian.mods.klib.util.MD5;
import dev.latvian.mods.klib.util.ParsedEntitySelector;
import dev.latvian.mods.klib.util.ScreenCorner;
import dev.latvian.mods.klib.util.Timestamp;
import dev.latvian.mods.klib.util.UInt64;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.HexColorArgument;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.players.NameAndId;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.EasingType;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface DataTypes {
	DataType<Boolean> BOOL = DataType.of(Codec.BOOL, ByteBufCodecs.BOOL);
	DataType<Boolean> BOOL_TRUE = DataType.unit(Boolean.TRUE);
	DataType<Boolean> BOOL_FALSE = DataType.unit(Boolean.FALSE);
	DataType<Integer> INT = DataType.of(Codec.INT, ByteBufCodecs.INT);
	DataType<Integer> VAR_INT = DataType.of(Codec.INT, ByteBufCodecs.VAR_INT);
	DataType<Long> LONG = DataType.of(Codec.LONG, ByteBufCodecs.LONG);
	DataType<Long> VAR_LONG = DataType.of(Codec.LONG, ByteBufCodecs.VAR_LONG);
	DataType<Float> FLOAT = DataType.of(Codec.FLOAT, ByteBufCodecs.FLOAT);
	DataType<Double> DOUBLE = DataType.of(Codec.DOUBLE, ByteBufCodecs.DOUBLE);
	DataType<String> STRING = DataType.of(Codec.STRING, ByteBufCodecs.STRING_UTF8);
	DataType<List<String>> STRING_LIST = STRING.listOf();
	DataType<Set<String>> STRING_SET = STRING.setOf();
	DataType<UUID> UUID = DataType.of(KLibCodecs.UUID, KLibStreamCodecs.UUID);
	DataType<Set<UUID>> UUID_SET = UUID.setOf();
	DataType<byte[]> B64_BYTE_ARRAY = DataType.of(KLibCodecs.B64_BYTE_ARRAY, ByteBufCodecs.BYTE_ARRAY);
	DataType<Instant> ISO_INSTANT = DataType.of(KLibCodecs.ISO_INSTANT, KLibStreamCodecs.INSTANT);
	DataType<Instant> UINT64_INSTANT = DataType.of(KLibCodecs.UINT64_INSTANT, KLibStreamCodecs.INSTANT);
	DataType<Instant> INSTANT = DataType.of(KLibCodecs.INSTANT, KLibStreamCodecs.INSTANT);

	DataType<Component> TEXT_COMPONENT = DataType.of(ComponentSerialization.CODEC, ComponentSerialization.STREAM_CODEC);
	DataType<Mirror> MIRROR = DataType.of(Mirror.values());
	DataType<Rotation> CARDINAL_ROTATION = DataType.of(Rotation.values());
	DataType<LiquidSettings> LIQUID_SETTINGS = DataType.of(LiquidSettings.values());
	DataType<InteractionHand> HAND = DataType.of(InteractionHand.values());
	DataType<Holder<SoundEvent>> SOUND_EVENT = DataType.of(SoundEvent.CODEC, SoundEvent.STREAM_CODEC);
	DataType<SoundSource> SOUND_SOURCE = DataType.of(SoundSource.values());

	DataType<ItemStack> ITEM_STACK = DataType.of(ItemStack.OPTIONAL_CODEC, new StreamCodec<>() {
		@Override
		public ItemStack decode(RegistryFriendlyByteBuf buf) {
			if (KLib.writeSafeItemStacks) {
				var id = buf.readUtf();
				var item = id.isEmpty() ? null : BuiltInRegistries.ITEM.get(Identifier.parse(id)).orElse(null);
				return item == null || item.value() == Items.AIR ? ItemStack.EMPTY : new ItemStack(item, 1, DataComponentPatch.EMPTY);
			} else {
				return ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
			}
		}

		@Override
		public void encode(RegistryFriendlyByteBuf buf, ItemStack value) {
			if (KLib.writeSafeItemStacks) {
				if (value.isEmpty()) {
					buf.writeUtf("");
				} else {
					buf.writeUtf(value.typeHolder().unwrapKey().map(ResourceKey::identifier).map(Identifier::toString).orElse(""));
				}
			} else {
				ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, value);
			}
		}
	});

	DataType<List<ItemStack>> ITEM_STACK_LIST = ITEM_STACK.listOf();

	DataType<ParticleOptions> PARTICLE_OPTIONS = DataType.of(ParticleTypes.CODEC, ParticleTypes.STREAM_CODEC);
	DataType<BlockState> BLOCK_STATE = DataType.of(MCCodecs.BLOCK_STATE, MCStreamCodecs.BLOCK_STATE);
	DataType<FluidState> FLUID_STATE = DataType.of(MCCodecs.FLUID_STATE, MCStreamCodecs.FLUID_STATE);
	DataType<Vec3> VEC3 = DataType.of(MCCodecs.VEC3, MCStreamCodecs.VEC3);
	DataType<Vec3> VEC3S = DataType.of(MCCodecs.VEC3S, MCStreamCodecs.VEC3S);
	DataType<BlockPos> BLOCK_POS = DataType.of(BlockPos.CODEC, BlockPos.STREAM_CODEC);
	DataType<Integer> TICKS = DataType.of(KLibCodecs.TICKS, ByteBufCodecs.VAR_INT);
	DataType<NameAndId> NAME_AND_ID = DataType.of(NameAndId.CODEC, MCStreamCodecs.NAME_AND_ID);
	DataType<GameProfile> GAME_PROFILE = DataType.of(ExtraCodecs.AUTHLIB_GAME_PROFILE, ByteBufCodecs.GAME_PROFILE);
	DataType<ResourceKey<Level>> DIMENSION = DataType.of(MCCodecs.DIMENSION, MCStreamCodecs.DIMENSION);
	DataType<Util.OS> PLATFORM = DataType.of(MCCodecs.PLATFORM, MCStreamCodecs.PLATFORM);
	DataType<ClientAsset.ResourceTexture> RESOURCE_TEXTURE = DataType.of(ClientAsset.ResourceTexture.CODEC, ClientAsset.ResourceTexture.STREAM_CODEC);
	DataType<EasingType> EASING_TYPE = DataType.of(EasingType.CODEC, MCStreamCodecs.EASING_TYPE);

	static void register(CustomRegistryTypeCollector<ByteBuf, DataType<?>> registry) {
		registry.register("data_type", DataType.DATA_TYPE);

		registry.register("bool", BOOL);
		registry.register("int", INT);
		registry.register("var_int", VAR_INT);
		registry.register("long", LONG);
		registry.register("var_long", VAR_LONG);
		registry.register("float", FLOAT);
		registry.register("double", DOUBLE);
		registry.register("string", STRING);
		registry.register("string_list", STRING_LIST);
		registry.register("string_set", STRING_SET);
		registry.register("uuid", UUID);
		registry.register("uuid_set", UUID_SET);

		registry.register("b64_byte_array", B64_BYTE_ARRAY);
		registry.register("iso_instant", ISO_INSTANT);
		registry.register("uint64_instant", UINT64_INSTANT);
		registry.register("instant", INSTANT);

		registry.register("id", ID.DATA_TYPE);
		registry.register("text_component", TEXT_COMPONENT);
		registry.register("mirror", MIRROR);
		registry.register("cardinal_rotation", CARDINAL_ROTATION);
		registry.register("liquid_settings", LIQUID_SETTINGS);
		registry.register("hand", HAND);
		registry.register("sound_event", SOUND_EVENT);
		registry.register("sound_source", SOUND_SOURCE);
		registry.register("item_stack", ITEM_STACK);
		registry.register("item_stack_list", ITEM_STACK_LIST);
		registry.register("particle_options", PARTICLE_OPTIONS);
		registry.register("block_state", BLOCK_STATE);
		registry.register("fluid_state", FLUID_STATE);
		registry.register("vec3", VEC3);
		registry.register("vec3s", VEC3S);
		registry.register("block_pos", BLOCK_POS);
		registry.register("ticks", TICKS);
		registry.register("game_profile", GAME_PROFILE);
		registry.register("name_and_id", NAME_AND_ID);
		registry.register("dimension", DIMENSION);
		registry.register("platform", PLATFORM);
		registry.register("resource_texture", RESOURCE_TEXTURE);
		registry.register("easing_type", EASING_TYPE);

		registry.register("color", Color.DATA_TYPE);
		registry.register("solid_color", Color.SOLID_DATA_TYPE);
		registry.register("gradient", Gradient.DATA_TYPE);
		registry.register("shape", Shape.DATA_TYPE);
		registry.register("rotation", dev.latvian.mods.klib.math.Rotation.DATA_TYPE);
		registry.register("rotation_with_roll", dev.latvian.mods.klib.math.Rotation.DATA_TYPE_WITH_ROLL);
		registry.register("movement_type", MovementType.DATA_TYPE);
		registry.register("range", Range.DATA_TYPE);
		registry.register("entity_selector/entity", ParsedEntitySelector.ENTITY_DATA_TYPE);
		registry.register("entity_selector/player", ParsedEntitySelector.PLAYER_DATA_TYPE);
		registry.register("entity_selector/entities", ParsedEntitySelector.ENTITIES_DATA_TYPE);
		registry.register("entity_selector/players", ParsedEntitySelector.PLAYERS_DATA_TYPE);
		registry.register("entity_selector", ParsedEntitySelector.DATA_TYPE);
		registry.register("interpolation", Interpolation.DATA_TYPE);
		registry.register("int_or_uuid", IntOrUUID.DATA_TYPE);
		registry.register("line", Line.DATA_TYPE);
		registry.register("interpolated_float", InterpolatedFloat.DATA_TYPE);
		registry.register("interpolated_double", InterpolatedDouble.DATA_TYPE);
		registry.register("clip_position", ClipPosition.DATA_TYPE);
		registry.register("screen_corner", ScreenCorner.DATA_TYPE);
		registry.register("timestamp", Timestamp.DATA_TYPE);
		registry.register("md5", MD5.DATA_TYPE);
		registry.register("hex32", Hex32.DATA_TYPE);
		registry.register("hex64", Hex64.DATA_TYPE);
		registry.register("uint64", UInt64.DATA_TYPE);
		registry.register("vec3f", Vec3f.DATA_TYPE);
		registry.register("direction_vec3f", Vec3f.DIRECTION_DATA_TYPE);
		registry.register("positioned_block", PositionedBlock.DATA_TYPE);
		registry.register("positioned_block_list", PositionedBlock.LIST_DATA_TYPE);
		registry.register("position_type", PositionType.DATA_TYPE);
	}

	static void registerCommandInfos(DataTypeCommandInfoRegistry registry) {
		registry.register(BOOL, BoolArgumentType::bool, BoolArgumentType::getBool);
		registry.register(INT, () -> IntegerArgumentType.integer(), IntegerArgumentType::getInteger);
		registry.register(VAR_INT, () -> IntegerArgumentType.integer(), IntegerArgumentType::getInteger);
		registry.register(LONG, () -> LongArgumentType.longArg(), LongArgumentType::getLong);
		registry.register(VAR_LONG, () -> LongArgumentType.longArg(), LongArgumentType::getLong);
		registry.register(FLOAT, () -> FloatArgumentType.floatArg(), FloatArgumentType::getFloat);
		registry.register(DOUBLE, () -> DoubleArgumentType.doubleArg(), DoubleArgumentType::getDouble);
		registry.register(STRING, StringArgumentType::string, StringArgumentType::getString);
		registry.register(UUID, UuidArgument::uuid, UuidArgument::getUuid);
		registry.register(ID.DATA_TYPE, IdentifierArgument::id, IdentifierArgument::getId);
		registry.register(TEXT_COMPONENT, ComponentArgument::textComponent, ComponentArgument::getResolvedComponent);

		registry.register(ITEM_STACK, ItemArgument::item, (ctx, name) -> ItemArgument.getItem(ctx, name).createItemStack(1));
		registry.register(PARTICLE_OPTIONS, ParticleArgument::particle, ParticleArgument::getParticle);
		registry.register(BLOCK_STATE, BlockStateArgument::block, (ctx, name) -> BlockStateArgument.getBlock(ctx, name).getState());
		registry.register(FLUID_STATE, BlockStateArgument::block, (ctx, name) -> BlockStateArgument.getBlock(ctx, name).getState().getFluidState());
		registry.register(VEC3, () -> Vec3Argument.vec3(), Vec3Argument::getVec3);
		registry.register(VEC3S, () -> Vec3Argument.vec3(), Vec3Argument::getVec3);
		registry.register(BLOCK_POS, BlockPosArgument::blockPos, BlockPosArgument::getBlockPos);
		registry.register(TICKS, () -> KLibCodecs.TIME_ARGUMENT, IntegerArgumentType::getInteger);
		registry.register(GAME_PROFILE, GameProfileArgument::gameProfile, (ctx, name) -> {
			var profiles = GameProfileArgument.getGameProfiles(ctx, name);
			var profile = profiles.isEmpty() ? null : profiles.iterator().next();
			return profile == null ? null : new GameProfile(profile.id(), profile.name());
		});
		registry.register(NAME_AND_ID, GameProfileArgument::gameProfile, (ctx, name) -> {
			var profiles = GameProfileArgument.getGameProfiles(ctx, name);
			return profiles.isEmpty() ? null : profiles.iterator().next();
		});
		registry.register(DIMENSION, DimensionArgument::dimension, (ctx, name) -> ResourceKey.create(Registries.DIMENSION, ctx.getArgument(name, Identifier.class)));
		registry.register(Color.SOLID_DATA_TYPE, HexColorArgument::hexColor, (ctx, name) -> Color.ofRGB(HexColorArgument.getHexColor(ctx, name)));
		registry.register(dev.latvian.mods.klib.math.Rotation.DATA_TYPE, RotationArgument::rotation, (ctx, name) -> dev.latvian.mods.klib.math.Rotation.deg(RotationArgument.getRotation(ctx, name).getRotation(ctx.getSource())));
	}
}
