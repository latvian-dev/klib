package dev.latvian.mods.klib.shape;

import dev.latvian.mods.klib.vertex.VertexCallback;

public interface CuboidBuilder {
	static void downQuad(float y, float minX, float minZ, float maxX, float maxZ, VertexCallback callback) {
		if (minX == maxX || minZ == maxZ) {
			return;
		}

		callback.quad(minX, y, minZ, 0F, 0F, 0F, -1F, 0F);
		callback.quad(maxX, y, minZ, 1F, 0F, 0F, -1F, 0F);
		callback.quad(maxX, y, maxZ, 1F, 1F, 0F, -1F, 0F);
		callback.quad(minX, y, maxZ, 0F, 1F, 0F, -1F, 0F);
	}

	static void upQuad(float y, float minX, float minZ, float maxX, float maxZ, VertexCallback callback) {
		if (minX == maxX || minZ == maxZ) {
			return;
		}

		callback.quad(minX, y, minZ, 0F, 0F, 0F, 1F, 0F);
		callback.quad(minX, y, maxZ, 0F, 1F, 0F, 1F, 0F);
		callback.quad(maxX, y, maxZ, 1F, 1F, 0F, 1F, 0F);
		callback.quad(maxX, y, minZ, 1F, 0F, 0F, 1F, 0F);
	}

	static void northQuad(float z, float minX, float minY, float maxX, float maxY, VertexCallback callback) {
		if (minY == maxY || minX == maxX) {
			return;
		}

		callback.quad(minX, minY, z, 1F, 1F, 0F, 0F, -1F);
		callback.quad(minX, maxY, z, 1F, 0F, 0F, 0F, -1F);
		callback.quad(maxX, maxY, z, 0F, 0F, 0F, 0F, -1F);
		callback.quad(maxX, minY, z, 0F, 1F, 0F, 0F, -1F);
	}

	static void southQuad(float z, float minX, float minY, float maxX, float maxY, VertexCallback callback) {
		if (minY == maxY || minX == maxX) {
			return;
		}

		callback.quad(maxX, minY, z, 1F, 1F, 0F, 0F, 1F);
		callback.quad(maxX, maxY, z, 1F, 0F, 0F, 0F, 1F);
		callback.quad(minX, maxY, z, 0F, 0F, 0F, 0F, 1F);
		callback.quad(minX, minY, z, 0F, 1F, 0F, 0F, 1F);
	}

	static void westQuad(float x, float minY, float minZ, float maxY, float maxZ, VertexCallback callback) {
		if (minY == maxY || minZ == maxZ) {
			return;
		}

		callback.quad(x, minY, minZ, 0F, 1F, -1F, 0F, 0F);
		callback.quad(x, minY, maxZ, 1F, 1F, -1F, 0F, 0F);
		callback.quad(x, maxY, maxZ, 1F, 0F, -1F, 0F, 0F);
		callback.quad(x, maxY, minZ, 0F, 0F, -1F, 0F, 0F);
	}

	static void eastQuad(float x, float minY, float minZ, float maxY, float maxZ, VertexCallback callback) {
		if (minY == maxY || minZ == maxZ) {
			return;
		}

		callback.quad(x, maxY, minZ, 1F, 0F, 1F, 0F, 0F);
		callback.quad(x, maxY, maxZ, 0F, 0F, 1F, 0F, 0F);
		callback.quad(x, minY, maxZ, 0F, 1F, 1F, 0F, 0F);
		callback.quad(x, minY, minZ, 1F, 1F, 1F, 0F, 0F);
	}

	static void quads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, VertexCallback callback) {
		downQuad(minY, minX, minZ, maxX, maxZ, callback);
		upQuad(maxY, minX, minZ, maxX, maxZ, callback);
		northQuad(minZ, minX, minY, maxX, maxY, callback);
		southQuad(maxZ, minX, minY, maxX, maxY, callback);
		westQuad(minX, minY, minZ, maxY, maxZ, callback);
		eastQuad(maxX, minY, minZ, maxY, maxZ, callback);
	}

	static void lines(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, VertexCallback callback) {
		if (minY == maxY && minZ == maxZ) {
			callback.line(minX, minY, minZ, maxX, minY, minZ, 1F, 0F, 0F);
		} else if (minX == maxX && minZ == maxZ) {
			callback.line(minX, minY, minZ, minX, maxY, minZ, 0F, 1F, 0F);
		} else if (minX == maxX && minY == maxY) {
			callback.line(minX, minY, minZ, minX, minY, maxZ, 0F, 0F, 1F);
		} else if (minX == maxX) {
			callback.line(minX, minY, minZ, minX, maxY, minZ, 0F, 1F, 0F);
			callback.line(minX, minY, minZ, minX, minY, maxZ, 0F, 0F, 1F);
			callback.line(minX, maxY, minZ, minX, maxY, maxZ, 0F, 0F, 1F);
			callback.line(minX, maxY, maxZ, minX, minY, maxZ, 0F, -1F, 0F);
		} else if (minY == maxY) {
			callback.line(minX, minY, minZ, maxX, minY, minZ, 1F, 0F, 0F);
			callback.line(minX, minY, minZ, minX, minY, maxZ, 0F, 0F, 1F);
			callback.line(minX, minY, maxZ, maxX, minY, maxZ, 1F, 0F, 0F);
			callback.line(maxX, minY, maxZ, maxX, minY, minZ, 0F, 0F, -1F);
		} else if (minZ == maxZ) {
			callback.line(minX, minY, minZ, maxX, minY, minZ, 1F, 0F, 0F);
			callback.line(minX, minY, minZ, minX, maxY, minZ, 0F, 1F, 0F);
			callback.line(maxX, minY, minZ, maxX, maxY, minZ, 0F, 1F, 0F);
			callback.line(maxX, maxY, minZ, minX, maxY, minZ, -1F, 0F, 0F);
		} else if (minX != maxX || minY != maxY || minZ != maxZ) {
			callback.line(minX, minY, minZ, maxX, minY, minZ, 1F, 0F, 0F);
			callback.line(minX, minY, minZ, minX, maxY, minZ, 0F, 1F, 0F);
			callback.line(minX, minY, minZ, minX, minY, maxZ, 0F, 0F, 1F);
			callback.line(maxX, minY, minZ, maxX, maxY, minZ, 0F, 1F, 0F);
			callback.line(maxX, maxY, minZ, minX, maxY, minZ, -1F, 0F, 0F);
			callback.line(minX, maxY, minZ, minX, maxY, maxZ, 0F, 0F, 1F);
			callback.line(minX, maxY, maxZ, minX, minY, maxZ, 0F, -1F, 0F);
			callback.line(minX, minY, maxZ, maxX, minY, maxZ, 1F, 0F, 0F);
			callback.line(maxX, minY, maxZ, maxX, minY, minZ, 0F, 0F, -1F);
			callback.line(minX, maxY, maxZ, maxX, maxY, maxZ, 1F, 0F, 0F);
			callback.line(maxX, minY, maxZ, maxX, maxY, maxZ, 0F, 1F, 0F);
			callback.line(maxX, maxY, minZ, maxX, maxY, maxZ, 0F, 0F, 1F);
		}
	}

	static void frameQuads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float cornerSize, float edgeSize, VertexCallback callback) {
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

	static void frameLines(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float cornerSize, float edgeSize, VertexCallback callback) {
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