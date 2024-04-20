package dev.latvian.mods.kmath.num;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kmath.KStore;
import dev.latvian.mods.kmath.util.DeltaTicking;
import org.joml.Vector4f;

public class KColor implements DeltaTicking {
	public final KNumberHolder red, green, blue, alpha;

	public KColor(KStore parent) {
		this.red = new KNumberHolder(parent, KNumber.ONE);
		this.green = new KNumberHolder(parent, KNumber.ONE);
		this.blue = new KNumberHolder(parent, KNumber.ONE);
		this.alpha = new KNumberHolder(parent, KNumber.ONE);
	}

	@Override
	public void snap() {
		red.snap();
		green.snap();
		blue.snap();
		alpha.snap();
	}

	@Override
	public void tickValue() {
		red.tickValue();
		green.tickValue();
		blue.tickValue();
		alpha.tickValue();
	}

	public void set(Vector4f color, float delta, float colorMod) {
		color.x = (float) (red.get(delta) * colorMod);
		color.y = (float) (green.get(delta) * colorMod);
		color.z = (float) (blue.get(delta) * colorMod);
		color.w = (float) alpha.get(delta);
	}

	public void update(JsonElement json) {
		if (json instanceof JsonArray a) {
			if (a.size() == 1) {
				red.update(a.get(0));
				green.source = red.source;
				blue.source = red.source;
				alpha.source = KNumber.ONE;
			} else if (a.size() == 2) {
				red.update(a.get(0));
				green.source = red.source;
				blue.source = red.source;
				alpha.update(a.get(1));
			} else if (a.size() == 3) {
				red.update(a.get(0));
				green.update(a.get(1));
				blue.update(a.get(2));
				alpha.source = KNumber.ONE;
			} else if (a.size() == 4) {
				red.update(a.get(0));
				green.update(a.get(1));
				blue.update(a.get(2));
				alpha.update(a.get(3));
			}
		} else if (json instanceof JsonPrimitive p) {
			if (p.isString()) {
				var hex = p.getAsString();

				if (hex.length() == 7 && hex.charAt(0) == '#') {
					red.source = Integer.parseInt(hex.substring(1, 3), 16) / 255D;
					green.source = Integer.parseInt(hex.substring(3, 5), 16) / 255D;
					blue.source = Integer.parseInt(hex.substring(5, 7), 16) / 255D;
					alpha.source = KNumber.ONE;
				} else if (hex.length() == 9 && hex.charAt(0) == '#') {
					alpha.source = Integer.parseInt(hex.substring(1, 3), 16) / 255D;
					red.source = Integer.parseInt(hex.substring(3, 5), 16) / 255D;
					green.source = Integer.parseInt(hex.substring(5, 7), 16) / 255D;
					blue.source = Integer.parseInt(hex.substring(7, 9), 16) / 255D;
				}
			} else {
				var num = p.getAsDouble();
				red.source = green.source = blue.source = num;
				alpha.source = KNumber.ONE;
			}
		}
	}
}
