package dev.latvian.mods.kmath.tex;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kmath.KStore;
import dev.latvian.mods.kmath.num.KNumber;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public interface KTexture {
	Number[] DEFAULT_UVS = new Number[]{KNumber.ZEROF, KNumber.ZEROF, KNumber.ONEF, KNumber.ONEF};
	KTexture WHITE = new KPathTexture(new Identifier("textures/misc/white.png"), DEFAULT_UVS);

	static KTexture fromJson(KStore parent, JsonElement json) {
		if (json.isJsonPrimitive()) {
			var str = json.getAsString();
			return str.equals("white") ? WHITE : new KPathTexture(new Identifier(str), DEFAULT_UVS);
		} else if (json instanceof JsonObject o) {
			if (o.has("sprite")) {
				var atlasStr = o.has("atlas") ? o.get("atlas").getAsString() : "blocks";

				return new KAtlasSpriteTexture(switch (atlasStr) {
					case "blocks" -> KAtlasSpriteTexture.BLOCKS;
					case "particles" -> KAtlasSpriteTexture.PARTICLES;
					default -> new Identifier(atlasStr);
				}, new Identifier(o.get("sprite").getAsString()), new Number[4]);
			} else if (o.has("path")) {
				var path = new Identifier(o.get("path").getAsString());
				var uvs = DEFAULT_UVS;

				if (o.has("uvs")) {
					var uvsArray = o.get("uvs").getAsJsonArray();

					if (uvsArray.size() == 4) {
						uvs = new Number[4];

						for (int i = 0; i < 4; i++) {
							uvs[i] = KNumber.fromJson(parent, uvsArray.get(i));
						}
					}
				}

				return new KPathTexture(path, uvs);
			}
		}

		return WHITE;
	}

	@Environment(EnvType.CLIENT)
	Identifier getPath();

	@Environment(EnvType.CLIENT)
	Number[] getUVs();
}
