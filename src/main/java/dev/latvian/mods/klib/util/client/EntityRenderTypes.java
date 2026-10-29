package dev.latvian.mods.klib.util.client;

import dev.latvian.mods.klib.util.Empty;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;

public interface EntityRenderTypes {
	RenderType WHITE = RenderTypes.entitySolid(Empty.TEXTURE.texturePath());
	RenderType WHITE_NO_CULL = RenderTypes.entityCutout(Empty.TEXTURE.texturePath());
	RenderType WHITE_TRANSLUCENT = RenderTypes.entityTranslucentCull(Empty.TEXTURE.texturePath());
	RenderType WHITE_TRANSLUCENT_NO_CULL = RenderTypes.entityTranslucent(Empty.TEXTURE.texturePath());

	static RenderType white(boolean translucent) {
		return translucent ? WHITE_TRANSLUCENT_NO_CULL : WHITE_NO_CULL;
	}

	static RenderType whiteCull(boolean translucent) {
		return translucent ? WHITE_TRANSLUCENT : WHITE;
	}

	static RenderType texture(Identifier texture, boolean translucent) {
		return translucent ? RenderTypes.entityTranslucent(texture) : RenderTypes.entityCutout(texture);
	}

	static RenderType texture(ClientAsset.ResourceTexture asset, boolean translucent) {
		return texture(asset.texturePath(), translucent);
	}

	static RenderType textureCull(Identifier texture, boolean translucent) {
		return translucent ? RenderTypes.entityTranslucentCull(texture) : RenderTypes.entityCutoutCull(texture);
	}

	static RenderType textureCull(ClientAsset.ResourceTexture asset, boolean translucent) {
		return textureCull(asset.texturePath(), translucent);
	}
}
