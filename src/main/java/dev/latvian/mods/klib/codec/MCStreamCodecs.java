package dev.latvian.mods.klib.codec;

import com.mojang.authlib.GameProfile;
import dev.latvian.mods.klib.math.KMath;
import dev.latvian.mods.klib.util.Empty;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public interface MCStreamCodecs {
	StreamCodec<ByteBuf, Vec3> VEC3 = new StreamCodec<>() {
		@Override
		public Vec3 decode(ByteBuf buf) {
			return KMath.vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
		}

		@Override
		public void encode(ByteBuf buf, Vec3 value) {
			buf.writeDouble(value.x);
			buf.writeDouble(value.y);
			buf.writeDouble(value.z);
		}
	};

	StreamCodec<ByteBuf, Vec3> VEC3S = new StreamCodec<>() {
		@Override
		public Vec3 decode(ByteBuf buf) {
			if (buf.readBoolean()) {
				return KMath.vec3(buf.readDouble());
			} else {
				return KMath.vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
			}
		}

		@Override
		public void encode(ByteBuf buf, Vec3 value) {
			if (value.x == value.y && value.x == value.z) {
				buf.writeBoolean(true);
				buf.writeDouble(value.x);
			} else {
				buf.writeBoolean(false);
				buf.writeDouble(value.x);
				buf.writeDouble(value.y);
				buf.writeDouble(value.z);
			}
		}
	};

	StreamCodec<ByteBuf, AABB> AABB = StreamCodec.composite(
		ByteBufCodecs.DOUBLE, b -> b.minX,
		ByteBufCodecs.DOUBLE, b -> b.minY,
		ByteBufCodecs.DOUBLE, b -> b.minZ,
		ByteBufCodecs.DOUBLE, b -> b.maxX,
		ByteBufCodecs.DOUBLE, b -> b.maxY,
		ByteBufCodecs.DOUBLE, b -> b.maxZ,
		AABB::new
	);

	StreamCodec<ByteBuf, SectionPos> SECTION_POS = ByteBufCodecs.LONG.map(SectionPos::of, SectionPos::asLong);

	StreamCodec<ByteBuf, CompoundTag> COMPOUND_TAG = new StreamCodec<>() {
		@Override
		public CompoundTag decode(ByteBuf buf) {
			var size = VarInt.read(buf);

			if (size == 0) {
				return Empty.COMPOUND_TAG;
			}

			var tag = new CompoundTag();

			for (int i = 0; i < size; i++) {
				tag.put(ByteBufCodecs.STRING_UTF8.decode(buf), ByteBufCodecs.TAG.decode(buf));
			}

			return tag;
		}

		@Override
		public void encode(ByteBuf buf, CompoundTag value) {
			VarInt.write(buf, value.size());

			if (!value.isEmpty()) {
				for (var key : value.keySet()) {
					ByteBufCodecs.STRING_UTF8.encode(buf, key);
					ByteBufCodecs.TAG.encode(buf, value.get(key));
				}
			}
		}
	};

	StreamCodec<ByteBuf, BlockState> BLOCK_STATE = ByteBufCodecs.VAR_INT.map(Block::stateById, Block::getId);
	StreamCodec<ByteBuf, FluidState> FLUID_STATE = ByteBufCodecs.VAR_INT.map(Fluid.FLUID_STATE_REGISTRY::byId, Fluid.FLUID_STATE_REGISTRY::getId);

	StreamCodec<ByteBuf, ResourceKey<Level>> DIMENSION = KLibStreamCodecs.resourceKey(Registries.DIMENSION);

	StreamCodec<ByteBuf, GameProfile> GAME_PROFILE = CompositeStreamCodec.of(
		KLibStreamCodecs.UUID, GameProfile::getId,
		ByteBufCodecs.STRING_UTF8, GameProfile::getName,
		GameProfile::new
	);

}
