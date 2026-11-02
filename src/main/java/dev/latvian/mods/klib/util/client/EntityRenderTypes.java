package dev.latvian.mods.klib.util.client;

import dev.latvian.mods.klib.color.TransparencyLevel;
import dev.latvian.mods.klib.render.KLibRenderPipelines;
import dev.latvian.mods.klib.util.Empty;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.function.Function;

public interface EntityRenderTypes {
	RenderType WHITE = RenderTypes.entitySolid(Empty.TEXTURE.texturePath());
	RenderType WHITE_NO_CULL = RenderTypes.entityCutout(Empty.TEXTURE.texturePath());
	RenderType WHITE_TRANSLUCENT = RenderTypes.entityTranslucentCull(Empty.TEXTURE.texturePath());
	RenderType WHITE_TRANSLUCENT_NO_CULL = RenderTypes.entityTranslucent(Empty.TEXTURE.texturePath());

	Function<Identifier, RenderType> ENTITY_SOLID_NO_CULL = Util.memoize(texture -> RenderType.create(
		"klib_entity_solid_no_cull",
		RenderSetup.builder(KLibRenderPipelines.ENTITY_SOLID_NO_CULL)
			.withTexture("Sampler0", texture)
			.useLightmap()
			.useOverlay()
			.affectsCrumbling()
			.setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
			.createRenderSetup()
	));

	static RenderType white(TransparencyLevel transparencyLevel) {
		return transparencyLevel == TransparencyLevel.TRANSLUCENT ? WHITE_TRANSLUCENT_NO_CULL : WHITE_NO_CULL;
	}

	static RenderType whiteCull(TransparencyLevel transparencyLevel) {
		return transparencyLevel == TransparencyLevel.TRANSLUCENT ? WHITE_TRANSLUCENT : WHITE;
	}

	static RenderType texture(Identifier texture, TransparencyLevel transparencyLevel) {
		if (transparencyLevel == TransparencyLevel.TRANSLUCENT) {
			return RenderTypes.entityTranslucent(texture);
		} else if (transparencyLevel == TransparencyLevel.TRANSPARENT) {
			return RenderTypes.entityCutout(texture);
		} else {
			return ENTITY_SOLID_NO_CULL.apply(texture);
		}
	}

	static RenderType texture(ClientAsset.ResourceTexture asset, TransparencyLevel transparencyLevel) {
		return texture(asset.texturePath(), transparencyLevel);
	}

	static RenderType textureCull(Identifier texture, TransparencyLevel transparencyLevel) {
		if (transparencyLevel == TransparencyLevel.TRANSLUCENT) {
			return RenderTypes.entityTranslucentCull(texture);
		} else if (transparencyLevel == TransparencyLevel.TRANSPARENT) {
			return RenderTypes.entityCutoutCull(texture);
		} else {
			return RenderTypes.entitySolid(texture);
		}
	}

	static RenderType textureCull(ClientAsset.ResourceTexture asset, TransparencyLevel transparencyLevel) {
		return textureCull(asset.texturePath(), transparencyLevel);
	}

	static RenderType texture(Identifier texture, TransparencyLevel transparencyLevel, boolean cull) {
		return cull ? textureCull(texture, transparencyLevel) : texture(texture, transparencyLevel);
	}

	static RenderType texture(ClientAsset.ResourceTexture asset, TransparencyLevel transparencyLevel, boolean cull) {
		return texture(asset.texturePath(), transparencyLevel, cull);
	}
}
