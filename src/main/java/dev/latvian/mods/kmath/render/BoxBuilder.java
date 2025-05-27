package dev.latvian.mods.kmath.render;

import dev.latvian.mods.kmath.vertex.VertexCallback;

public interface BoxBuilder {
	static void downQuads(float y, float minX, float minZ, float maxX, float maxZ, VertexCallback callback) {
		callback.acceptPos(minX, y, minZ).acceptTex(0F, 0F).acceptNormal(0F, -1F, 0F);
		callback.acceptPos(maxX, y, minZ).acceptTex(1F, 0F).acceptNormal(0F, -1F, 0F);
		callback.acceptPos(maxX, y, maxZ).acceptTex(1F, 1F).acceptNormal(0F, -1F, 0F);
		callback.acceptPos(minX, y, maxZ).acceptTex(0F, 1F).acceptNormal(0F, -1F, 0F);
	}

	static void upQuads(float y, float minX, float minZ, float maxX, float maxZ, VertexCallback callback) {
		callback.acceptPos(minX, y, minZ).acceptTex(0F, 0F).acceptNormal(0F, 1F, 0F);
		callback.acceptPos(minX, y, maxZ).acceptTex(0F, 1F).acceptNormal(0F, 1F, 0F);
		callback.acceptPos(maxX, y, maxZ).acceptTex(1F, 1F).acceptNormal(0F, 1F, 0F);
		callback.acceptPos(maxX, y, minZ).acceptTex(1F, 0F).acceptNormal(0F, 1F, 0F);
	}

	static void northQuads(float z, float minX, float minY, float maxX, float maxY, VertexCallback callback) {
		callback.acceptPos(minX, minY, z).acceptTex(1F, 1F).acceptNormal(0F, 0F, -1F);
		callback.acceptPos(minX, maxY, z).acceptTex(1F, 0F).acceptNormal(0F, 0F, -1F);
		callback.acceptPos(maxX, maxY, z).acceptTex(0F, 0F).acceptNormal(0F, 0F, -1F);
		callback.acceptPos(maxX, minY, z).acceptTex(0F, 1F).acceptNormal(0F, 0F, -1F);
	}

	static void southQuads(float z, float minX, float minY, float maxX, float maxY, VertexCallback callback) {
		callback.acceptPos(maxX, minY, z).acceptTex(1F, 1F).acceptNormal(0F, 0F, 1F);
		callback.acceptPos(maxX, maxY, z).acceptTex(1F, 0F).acceptNormal(0F, 0F, 1F);
		callback.acceptPos(minX, maxY, z).acceptTex(0F, 0F).acceptNormal(0F, 0F, 1F);
		callback.acceptPos(minX, minY, z).acceptTex(0F, 1F).acceptNormal(0F, 0F, 1F);
	}

	static void westQuads(float x, float minY, float minZ, float maxY, float maxZ, VertexCallback callback) {
		callback.acceptPos(x, minY, minZ).acceptTex(0F, 1F).acceptNormal(-1F, 0F, 0F);
		callback.acceptPos(x, minY, maxZ).acceptTex(1F, 1F).acceptNormal(-1F, 0F, 0F);
		callback.acceptPos(x, maxY, maxZ).acceptTex(1F, 0F).acceptNormal(-1F, 0F, 0F);
		callback.acceptPos(x, maxY, minZ).acceptTex(0F, 0F).acceptNormal(-1F, 0F, 0F);
	}

	static void eastQuads(float x, float minY, float minZ, float maxY, float maxZ, VertexCallback callback) {
		callback.acceptPos(x, maxY, minZ).acceptTex(1F, 0F).acceptNormal(1F, 0F, 0F);
		callback.acceptPos(x, maxY, maxZ).acceptTex(0F, 0F).acceptNormal(1F, 0F, 0F);
		callback.acceptPos(x, minY, maxZ).acceptTex(0F, 1F).acceptNormal(1F, 0F, 0F);
		callback.acceptPos(x, minY, minZ).acceptTex(1F, 1F).acceptNormal(1F, 0F, 0F);
	}

	static void quads(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, VertexCallback callback) {
		downQuads(minY, minX, minZ, maxX, maxZ, callback);
		upQuads(maxY, minX, minZ, maxX, maxZ, callback);
		northQuads(minZ, minX, minY, maxX, maxY, callback);
		southQuads(maxZ, minX, minY, maxX, maxY, callback);
		westQuads(minX, minY, minZ, maxY, maxZ, callback);
		eastQuads(maxX, minY, minZ, maxY, maxZ, callback);
	}

	static void lines(float minX, float minY, float minZ, float maxX, float maxY, float maxZ, VertexCallback callback) {
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