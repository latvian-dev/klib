package dev.latvian.mods.kmath.pos;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kmath.KStore;
import dev.latvian.mods.kmath.num.KNumber;
import org.joml.Vector3d;

public interface KPos {
	static KPos fromJson(KStore parent, JsonElement json, Vector3d previous) {
		if (json instanceof JsonArray arr && (arr.size() == 3 || arr.size() == 4)) {
			if (arr.get(1) instanceof JsonPrimitive p && p.isString()) {
				var op = KArithmeticPos.Operation.get(p.getAsString());

				if (op != null) {
					return new KArithmeticPos(op, fromJson(parent, arr.get(0), previous), fromJson(parent, arr.get(2), previous));
				}
			}

			var x = arr.get(0).getAsDouble();
			var y = arr.get(1).getAsDouble();
			var z = arr.get(2).getAsDouble();

			if (arr.size() == 3) {
				return new KFixedPos(x, y, z);
			}

			return switch (arr.get(3).getAsString()) {
				case "+" -> new KFixedPos(previous.x + x, previous.y + y, previous.z + z);
				case "-" -> new KFixedPos(previous.x - x, previous.y - y, previous.z - z);
				case "*" -> new KFixedPos(previous.x * x, previous.y * y, previous.z * z);
				case "/" -> new KFixedPos(previous.x / x, previous.y / y, previous.z / z);
				case "%" -> new KFixedPos(x == 0D ? previous.x : (previous.x % x), previous.y % y, z == 0D ? previous.z : (previous.z % z));
				case "**" -> new KFixedPos(Math.pow(previous.x, x), Math.pow(previous.y, y), Math.pow(previous.z, z));
				default -> new KFixedPos(x, y, z);
			};
		} else if (json instanceof JsonObject o) {
			if (o.get("entity") instanceof JsonPrimitive entity) {
				var ep = KEntityPos.parseEntity(parent, entity);
				ep.update(o);
				return ep;
			} else if (o.has("approach")) {
				var ap = new KApproachPos(fromJson(parent, o.get("approach"), previous));

				if (o.has("speed")) {
					ap.speed = KNumber.fromJson(parent, o.get("speed"));
				}

				if (o.has("minDistance")) {
					ap.minDistance = KNumber.fromJson(parent, o.get("minDistance"));
				}

				if (o.has("avoid")) {
					ap.avoid = KNumber.fromJson(parent, o.get("avoid"));
				}

				if (o.has("distExp")) {
					ap.distExp = KNumber.fromJson(parent, o.get("distExp"));
				}

				return ap;
			} else {
				var x = o.has("x") ? KNumber.fromJson(parent, o.get("x")) : previous.x;
				var y = o.has("y") ? KNumber.fromJson(parent, o.get("y")) : previous.y;
				var z = o.has("z") ? KNumber.fromJson(parent, o.get("z")) : previous.z;
				return new KFixedPos(x, y, z);
			}
		} else if (json instanceof JsonPrimitive) {
			var num = KNumber.fromJson(parent, json);
			return new KFixedPos(num, num, num);
		}

		return new KFixedPos(previous.x, previous.y, previous.z);
	}

	void tick(Vector3d pos);
}
