package dev.latvian.mods.kmath.render;

import dev.latvian.mods.kmath.vertex.VertexCallback;

public class BoxBuilder {
	public static void quads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, VertexCallback callback) {
		// East
		callback.acceptPos(maxX, maxY, minZ); // N 1 0 0
		callback.acceptPos(maxX, maxY, maxZ); // N 1 0 0
		callback.acceptPos(maxX, minY, maxZ); // N 1 0 0
		callback.acceptPos(maxX, minY, minZ); // N 1 0 0

		// South
		callback.acceptPos(maxX, minY, maxZ); // N 0 0 1
		callback.acceptPos(maxX, maxY, maxZ); // N 0 0 1
		callback.acceptPos(minX, maxY, maxZ); // N 0 0 1
		callback.acceptPos(minX, minY, maxZ); // N 0 0 1

		// North
		callback.acceptPos(minX, minY, minZ); // N 0 0 -1
		callback.acceptPos(minX, maxY, minZ); // N 0 0 -1
		callback.acceptPos(maxX, maxY, minZ); // N 0 0 -1
		callback.acceptPos(maxX, minY, minZ); // N 0 0 -1

		// West
		callback.acceptPos(minX, minY, minZ); // N -1 0 0
		callback.acceptPos(minX, minY, maxZ); // N -1 0 0
		callback.acceptPos(minX, maxY, maxZ); // N -1 0 0
		callback.acceptPos(minX, maxY, minZ); // N -1 0 0

		// Up
		callback.acceptPos(minX, maxY, minZ); // N 0 1 0
		callback.acceptPos(minX, maxY, maxZ); // N 0 1 0
		callback.acceptPos(maxX, maxY, maxZ); // N 0 1 0
		callback.acceptPos(maxX, maxY, minZ); // N 0 1 0

		// Down
		callback.acceptPos(minX, minY, minZ); // N 0 -1 0
		callback.acceptPos(maxX, minY, minZ); // N 0 -1 0
		callback.acceptPos(maxX, minY, maxZ); // N 0 -1 0
		callback.acceptPos(minX, minY, maxZ); // N 0 -1 0
	}

	public static void lines(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, VertexCallback callback) {
		callback.acceptPos(minX, minY, minZ);
		callback.acceptPos(maxX, minY, minZ);
		callback.acceptPos(minX, minY, minZ);
		callback.acceptPos(minX, maxY, minZ);
		callback.acceptPos(minX, minY, minZ);
		callback.acceptPos(minX, minY, maxZ);
		callback.acceptPos(maxX, minY, minZ);
		callback.acceptPos(maxX, maxY, minZ);
		callback.acceptPos(maxX, maxY, minZ);
		callback.acceptPos(minX, maxY, minZ);
		callback.acceptPos(minX, maxY, minZ);
		callback.acceptPos(minX, maxY, maxZ);
		callback.acceptPos(minX, maxY, maxZ);
		callback.acceptPos(minX, minY, maxZ);
		callback.acceptPos(minX, minY, maxZ);
		callback.acceptPos(maxX, minY, maxZ);
		callback.acceptPos(maxX, minY, maxZ);
		callback.acceptPos(maxX, minY, minZ);
		callback.acceptPos(minX, maxY, maxZ);
		callback.acceptPos(maxX, maxY, maxZ);
		callback.acceptPos(maxX, minY, maxZ);
		callback.acceptPos(maxX, maxY, maxZ);
		callback.acceptPos(maxX, maxY, minZ);
		callback.acceptPos(maxX, maxY, maxZ);
	}

	public static void frameQuads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float cornerSize, float edgeSize, VertexCallback callback) {
		float c = cornerSize * 0.5F;
		float e = edgeSize * 0.5F;
		float o = c * 2F;

		quads(minX - c, minY - c, minZ - c, minX + c, minY + c, minZ + c, callback);
		quads(minX - c, minY - c, maxZ - c, minX + c, minY + c, maxZ + c, callback);
		quads(minX - c, maxY - c, minZ - c, minX + c, maxY + c, minZ + c, callback);
		quads(minX - c, maxY - c, maxZ - c, minX + c, maxY + c, maxZ + c, callback);
		quads(maxX - c, minY - c, minZ - c, maxX + c, minY + c, minZ + c, callback);
		quads(maxX - c, minY - c, maxZ - c, maxX + c, minY + c, maxZ + c, callback);
		quads(maxX - c, maxY - c, minZ - c, maxX + c, maxY + c, minZ + c, callback);
		quads(maxX - c, maxY - c, maxZ - c, maxX + c, maxY + c, maxZ + c, callback);

		quads(minX - e, minY - e, minZ - c + o, minX + e, minY + e, maxZ + c - o, callback);
		quads(maxX - e, minY - e, minZ - c + o, maxX + e, minY + e, maxZ + c - o, callback);
		quads(minX - c + o, minY - e, minZ - e, maxX + c - o, minY + e, minZ + e, callback);
		quads(minX - c + o, minY - e, maxZ - e, maxX + c - o, minY + e, maxZ + e, callback);

		quads(minX - e, maxY - e, minZ - c + o, minX + e, maxY + e, maxZ + c - o, callback);
		quads(maxX - e, maxY - e, minZ - c + o, maxX + e, maxY + e, maxZ + c - o, callback);
		quads(minX - c + o, maxY - e, minZ - e, maxX + c - o, maxY + e, minZ + e, callback);
		quads(minX - c + o, maxY - e, maxZ - e, maxX + c - o, maxY + e, maxZ + e, callback);

		quads(minX - e, minY - c + o, minZ - e, minX + e, maxY + c - o, minZ + e, callback);
		quads(maxX - e, minY - c + o, minZ - e, maxX + e, maxY + c - o, minZ + e, callback);
		quads(minX - e, minY - c + o, maxZ - e, minX + e, maxY + c - o, maxZ + e, callback);
		quads(maxX - e, minY - c + o, maxZ - e, maxX + e, maxY + c - o, maxZ + e, callback);
	}

	public static void frameLines(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float cornerSize, float edgeSize, VertexCallback callback) {
		float c = cornerSize * 0.5F;
		float e = edgeSize * 0.5F;
		float o = c * 2F;

		lines(minX - c, minY - c, minZ - c, minX + c, minY + c, minZ + c, callback);
		lines(minX - c, minY - c, maxZ - c, minX + c, minY + c, maxZ + c, callback);
		lines(minX - c, maxY - c, minZ - c, minX + c, maxY + c, minZ + c, callback);
		lines(minX - c, maxY - c, maxZ - c, minX + c, maxY + c, maxZ + c, callback);
		lines(maxX - c, minY - c, minZ - c, maxX + c, minY + c, minZ + c, callback);
		lines(maxX - c, minY - c, maxZ - c, maxX + c, minY + c, maxZ + c, callback);
		lines(maxX - c, maxY - c, minZ - c, maxX + c, maxY + c, minZ + c, callback);
		lines(maxX - c, maxY - c, maxZ - c, maxX + c, maxY + c, maxZ + c, callback);

		lines(minX - e, minY - e, minZ - c + o, minX + e, minY + e, maxZ + c - o, callback);
		lines(maxX - e, minY - e, minZ - c + o, maxX + e, minY + e, maxZ + c - o, callback);
		lines(minX - c + o, minY - e, minZ - e, maxX + c - o, minY + e, minZ + e, callback);
		lines(minX - c + o, minY - e, maxZ - e, maxX + c - o, minY + e, maxZ + e, callback);

		lines(minX - e, maxY - e, minZ - c + o, minX + e, maxY + e, maxZ + c - o, callback);
		lines(maxX - e, maxY - e, minZ - c + o, maxX + e, maxY + e, maxZ + c - o, callback);
		lines(minX - c + o, maxY - e, minZ - e, maxX + c - o, maxY + e, minZ + e, callback);
		lines(minX - c + o, maxY - e, maxZ - e, maxX + c - o, maxY + e, maxZ + e, callback);

		lines(minX - e, minY - c + o, minZ - e, minX + e, maxY + c - o, minZ + e, callback);
		lines(maxX - e, minY - c + o, minZ - e, maxX + e, maxY + c - o, minZ + e, callback);
		lines(minX - e, minY - c + o, maxZ - e, minX + e, maxY + c - o, maxZ + e, callback);
		lines(maxX - e, minY - c + o, maxZ - e, maxX + e, maxY + c - o, maxZ + e, callback);
	}
}