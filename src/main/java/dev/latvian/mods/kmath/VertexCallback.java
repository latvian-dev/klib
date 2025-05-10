package dev.latvian.mods.kmath;

public interface VertexCallback {
	@FunctionalInterface
	interface Pos {
		void accept(float x, float y, float z);
	}

	@FunctionalInterface
	interface PosTex {
		void accept(float x, float y, float z, float u, float v);
	}

	@FunctionalInterface
	interface PosNormal {
		void accept(float x, float y, float z, float nx, float ny, float nz);
	}

	@FunctionalInterface
	interface PosTexNormal {
		void accept(float x, float y, float z, float u, float v, float nx, float ny, float nz);
	}

	@FunctionalInterface
	interface PosCol {
		void accept(float x, float y, float z, float r, float g, float b, float a);
	}

	@FunctionalInterface
	interface PosTexCol {
		void accept(float x, float y, float z, float u, float v, float r, float g, float b, float a);
	}

	@FunctionalInterface
	interface PosTexColNormal {
		void accept(float x, float y, float z, float u, float v, float r, float g, float b, float a, float nx, float ny, float nz);
	}

	@FunctionalInterface
	interface PosTexColLightNormal {
		void accept(float x, float y, float z, float u, float v, float r, float g, float b, float a, float lu, float lv, float nx, float ny, float nz);
	}
}
