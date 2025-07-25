package dev.latvian.mods.klib.core;

import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.Optional;

public interface KLibStreamCodec<B, V> {
	default StreamCodec<B, Optional<V>> optional() {
		return ByteBufCodecs.optional((StreamCodec) this);
	}

	default StreamCodec<B, V> optional(V defaultValue) {
		return KLibStreamCodecs.optional((StreamCodec) this, defaultValue);
	}

	default StreamCodec<B, V> nullable() {
		return optional(null);
	}

	default StreamCodec<B, List<V>> listOf() {
		return KLibStreamCodecs.listOf((StreamCodec) this);
	}

	default StreamCodec<B, List<V>> setOf() {
		return KLibStreamCodecs.setOf((StreamCodec) this);
	}

	default StreamCodec<B, List<V>> linkedSetOf() {
		return KLibStreamCodecs.linkedSetOf((StreamCodec) this);
	}
}
