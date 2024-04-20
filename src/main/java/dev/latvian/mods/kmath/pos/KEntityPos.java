package dev.latvian.mods.kmath.pos;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.StringReader;
import com.mojang.util.UUIDTypeAdapter;
import dev.latvian.mods.kmath.KStore;
import dev.latvian.mods.kmath.num.DummyNumber;
import dev.latvian.mods.kmath.num.KNumber;
import net.minecraft.command.EntitySelectorReader;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.UUID;

public class KEntityPos implements KPos {
	public static final Number EYE = new DummyNumber();
	public static final Number LEASH = new DummyNumber();

	public static KEntityPos parseEntity(KStore parent, JsonPrimitive entity) {
		if (entity.isNumber()) {
			return new KEntityPos(parent, entity.getAsInt());
		} else if (entity.isString()) {
			var s = entity.getAsString();

			if (s.startsWith("@")) {
				try {
					return new KEntitySelectorPos(parent, new EntitySelectorReader(new StringReader(s), true).read());
				} catch (Exception ex) {
					throw new IllegalArgumentException("Invalid entity selector: " + s);
				}
			} else if (s.matches("\\w{8}\\w{4}\\w{4}\\w{4}\\w{12}")) {
				return new KEntityUUIDPos(parent, UUIDTypeAdapter.fromString(s));
			} else {
				return new KEntityUUIDPos(parent, UUID.fromString(s));
			}
		} else {
			throw new IllegalArgumentException("Invalid entity pos: " + entity);
		}
	}

	public final KStore parent;
	public final int entityId;
	public Number offset;

	public Entity entity;

	public KEntityPos(KStore parent, int entityId) {
		this.parent = parent;
		this.entityId = entityId;
		this.offset = KNumber.ZERO;
	}

	public void update(JsonObject json) {
		var off = json.get("offset");

		offsetBreak:
		if (off != null) {
			if (off instanceof JsonPrimitive p && p.isString()) {
				if (p.getAsString().equals("eye")) {
					offset = EYE;
					break offsetBreak;
				} else if (p.getAsString().equals("leash")) {
					offset = LEASH;
					break offsetBreak;
				}
			}

			offset = KNumber.fromJson(parent, off);
		}
	}

	@Nullable
	public Entity getEntity(World world) {
		return world.getEntityById(entityId);
	}

	@Override
	public void tick(Vector3d pos) {
		if (entity == null || entity.isRemoved()) {
			entity = getEntity(parent.getWorld());
		}

		if (entity != null) {
			if (offset == EYE) {
				pos.set(entity.getX(), entity.getEyeY(), entity.getZ());
			} else if (offset == LEASH) {
				var p = entity.getLeashPos(1F);
				pos.set(p.x, p.y, p.z);
			} else {
				pos.set(entity.getX(), entity.getY() + entity.getHeight() * offset.doubleValue(), entity.getZ());
			}
		}
	}
}
