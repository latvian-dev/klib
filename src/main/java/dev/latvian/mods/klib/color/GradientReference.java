package dev.latvian.mods.klib.color;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public record GradientReference(ResourceLocation id) implements Gradient {
	public static Map<ResourceLocation, Gradient> MAP = Map.of();

	public static final Codec<GradientReference> CODEC = ResourceLocation.CODEC.xmap(GradientReference::new, GradientReference::id);
	public static final StreamCodec<ByteBuf, GradientReference> STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(GradientReference::new, GradientReference::id);

	@Override
	public Color get(float delta) {
		return optimize().get(delta);
	}

	@Override
	public Gradient optimize() {
		return MAP.getOrDefault(id, Color.TRANSPARENT).optimize();
	}

	@Override
	public List<PositionedColor> getPositionedColors() {
		var g = MAP.get(id);
		return g == null ? List.of() : g.getPositionedColors();
	}
}
