package dev.latvian.mods.klib.particle;

import com.mojang.serialization.MapCodec;
import dev.latvian.mods.klib.util.ID;
import dev.latvian.mods.klib.util.Lazy;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

public interface KLibParticles {
	List<Pair<Identifier, Supplier<ParticleType<?>>>> PARTICLES = new LinkedList<>();

	static <T extends ParticleOptions> Supplier<ParticleType<T>> register(Identifier name, Supplier<MapCodec<T>> codec, Supplier<StreamCodec<? super RegistryFriendlyByteBuf, T>> streamCodec) {
		Supplier<ParticleType<T>> supplier = Lazy.of(() -> new ParticleType<>(true) {
			@Override
			public MapCodec<T> codec() {
				return codec.get();
			}

			@Override
			public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec() {
				return streamCodec.get();
			}
		});

		PARTICLES.add(Pair.of(name, (Supplier) supplier));
		return supplier;
	}

	static Supplier<SimpleParticleType> register(Identifier name) {
		Supplier<SimpleParticleType> supplier = Lazy.of(() -> new SimpleParticleType(true));
		PARTICLES.add(Pair.of(name, (Supplier) supplier));
		return supplier;
	}

	static void init() {
	}

	Supplier<ParticleType<ShapeParticleOptions>> SHAPE = register(ID.klib("shape"), () -> ShapeParticleOptions.MAP_CODEC, () -> ShapeParticleOptions.STREAM_CODEC);
	// Supplier<ParticleType<LineParticleOptions>> LINE = register(ID.klib("line"), () -> LineParticleOptions.MAP_CODEC, () -> LineParticleOptions.STREAM_CODEC);
	// Supplier<ParticleType<TextParticleOptions>> TEXT = register(ID.klib("text"), () -> TextParticleOptions.MAP_CODEC, () -> TextParticleOptions.STREAM_CODEC);
	// Supplier<ParticleType<ItemParticleOptions>> ITEM = register(ID.klib("item"), () -> ItemParticleOptions.MAP_CODEC, () -> ItemParticleOptions.STREAM_CODEC);
}
