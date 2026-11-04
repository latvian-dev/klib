package dev.latvian.mods.klib.visual;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import dev.latvian.mods.klib.kvector.KVector;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.texture.CubeTexturePatch;
import dev.latvian.mods.klib.texture.LightUV;
import dev.latvian.mods.klib.texture.OverlayUV;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.phys.AABB;

public record TexturedCubeVisual(Ref<KVector> position, Ref<KVector> size, CubeTexturePatch textures, LightUV light, OverlayUV overlay) implements Visual {
	public static final DynamicType<RegistryFriendlyByteBuf, Visual> TYPE = DynamicType.create(
		"textured_cube",
		RecordCodecBuilder.mapCodec(i -> i.group(
			KVector.CODEC.fieldOf("position").forGetter(TexturedCubeVisual::position),
			KVector.CODEC.optionalFieldOf("size", KVector.ONE).forGetter(TexturedCubeVisual::size),
			CubeTexturePatch.CODEC.fieldOf("textures").forGetter(TexturedCubeVisual::textures),
			LightUV.CODEC.optionalFieldOf("light", LightUV.FULL_BRIGHT).forGetter(TexturedCubeVisual::light),
			OverlayUV.CODEC.optionalFieldOf("overlay", OverlayUV.NORMAL).forGetter(TexturedCubeVisual::overlay)
		).apply(i, TexturedCubeVisual::new)),
		CompositeStreamCodec.of(
			KVector.STREAM_CODEC, TexturedCubeVisual::position,
			KLibStreamCodecs.optional(KVector.STREAM_CODEC, KVector.ONE), TexturedCubeVisual::size,
			CubeTexturePatch.STREAM_CODEC, TexturedCubeVisual::textures,
			LightUV.STREAM_CODEC, TexturedCubeVisual::light,
			OverlayUV.STREAM_CODEC, TexturedCubeVisual::overlay,
			TexturedCubeVisual::new
		)
	);

	public TexturedCubeVisual(AABB box, CubeTexturePatch textures, LightUV light, OverlayUV overlay) {
		this(
			KVector.of(box.minX, box.minY, box.minZ).ref(),
			KVector.of(
				box.maxX - box.minX,
				box.maxY - box.minY,
				box.maxZ - box.minZ
			).ref(),
			textures,
			light,
			overlay
		);
	}

	@Override
	public DynamicType<RegistryFriendlyByteBuf, Visual> type() {
		return TYPE;
	}
}
