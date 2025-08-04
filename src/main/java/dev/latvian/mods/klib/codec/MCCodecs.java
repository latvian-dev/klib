package dev.latvian.mods.klib.codec;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.math.KMath;
import net.minecraft.Util;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public interface MCCodecs {
	Codec<Vec3> VEC3 = Codec.DOUBLE.listOf(3, 3).xmap(l -> KMath.vec3(l.get(0), l.get(1), l.get(2)), v -> List.of(v.x, v.y, v.z));
	Codec<Vec3> VEC3S = Codec.either(Codec.DOUBLE, VEC3).xmap(either -> either.map(KMath::vec3, Function.identity()), v -> v.x == v.y && v.x == v.z ? Either.left(v.x) : Either.right(v));
	Codec<AABB> AABB = Codec.DOUBLE.listOf(6, 6).xmap(l -> new AABB(l.get(0), l.get(1), l.get(2), l.get(3), l.get(4), l.get(5)), v -> List.of(v.minX, v.minY, v.minZ, v.maxX, v.maxY, v.maxZ));
	Codec<SectionPos> SECTION_POS = Codec.INT_STREAM.comapFlatMap(intStream -> Util.fixedSize(intStream, 3).map(ints -> SectionPos.of(ints[0], ints[1], ints[2])), pos -> IntStream.of(pos.x(), pos.y(), pos.z()));
	Codec<ResourceKey<Level>> DIMENSION = ResourceKey.codec(Registries.DIMENSION);
	Codec<SoundSource> SOUND_SOURCE = KLibCodecs.anyEnumCodec(SoundSource.values(), SoundSource::getName);
	Codec<BlockState> BLOCK_STATE = Codec.either(BlockState.CODEC, BuiltInRegistries.BLOCK.byNameCodec()).xmap(either -> either.map(Function.identity(), Block::defaultBlockState), state -> state == state.getBlock().defaultBlockState() ? Either.right(state.getBlock()) : Either.left(state));
	Codec<FluidState> FLUID_STATE = Codec.either(FluidState.CODEC, BuiltInRegistries.FLUID.byNameCodec()).xmap(either -> either.map(Function.identity(), Fluid::defaultFluidState), state -> state == state.getType().defaultFluidState() ? Either.right(state.getType()) : Either.left(state));

	Codec<GameProfile> GAME_PROFILE = RecordCodecBuilder.create(instance -> instance.group(
		KLibCodecs.UUID.fieldOf("uuid").forGetter(GameProfile::getId),
		Codec.STRING.fieldOf("name").forGetter(GameProfile::getName)
	).apply(instance, GameProfile::new));
}
