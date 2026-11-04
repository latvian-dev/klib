package dev.latvian.mods.klib.shape;

import dev.latvian.mods.klib.math.AAFBB;
import dev.latvian.mods.klib.math.FrustumCheck;
import dev.latvian.mods.klib.math.HitBuilder;
import dev.latvian.mods.klib.registry.UnitType;
import dev.latvian.mods.klib.vertex.LineVertexCallback;
import dev.latvian.mods.klib.vertex.QuadVertexCallback;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3fc;

public enum UniverseShape implements Shape {
	INSTANCE;

	@Override
	public UnitType<ByteBuf, Shape> type() {
		return UNIVERSE;
	}

	@Override
	public AAFBB box() {
		return AAFBB.INFINITE;
	}

	@Override
	public void buildLines(Vector3fc offset, LineVertexCallback callback) {
	}

	@Override
	public void buildQuads(Vector3fc offset, QuadVertexCallback callback) {
	}

	@Override
	public boolean contains(float x, float y, float z) {
		return true;
	}

	@Override
	public boolean hit(Vec3 pos, HitBuilder hit) {
		return false;
	}

	@Override
	public boolean isVisible(double x, double y, double z, FrustumCheck frustum) {
		return false;
	}

	@Override
	public String toString() {
		return "UniverseShape";
	}
}
