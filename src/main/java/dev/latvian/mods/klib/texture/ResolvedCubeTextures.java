package dev.latvian.mods.klib.texture;

import java.util.ArrayList;
import java.util.List;

public record ResolvedCubeTextures(List<FaceTexture> faces) {
	public static final ResolvedCubeTextures EMPTY = new ResolvedCubeTextures(FaceTexture.EMPTY);
	public static final ResolvedCubeTextures WHITE = new ResolvedCubeTextures(FaceTexture.WHITE);

	public static ResolvedCubeTextures resolve(CubeTextures cube) {
		if (cube == CubeTextures.EMPTY) {
			return EMPTY;
		}

		var all = cube.all().orElse(FaceTexture.EMPTY);

		var faces = List.of(
			cube.down().orElse(all),
			cube.up().orElse(all),
			cube.north().orElse(all),
			cube.south().orElse(all),
			cube.west().orElse(all),
			cube.east().orElse(all)
		);

		return new ResolvedCubeTextures(faces);
	}

	public ResolvedCubeTextures(FaceTexture face) {
		this(List.of(face, face, face, face, face, face));
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}

	public ResolvedCubeTextures merge(ResolvedCubeTextures other) {
		if (other.isEmpty()) {
			return this;
		} else if (isEmpty()) {
			return other;
		}

		var faces = List.of(
			this.faces.get(0).merge(other.faces.get(0)),
			this.faces.get(1).merge(other.faces.get(1)),
			this.faces.get(2).merge(other.faces.get(2)),
			this.faces.get(3).merge(other.faces.get(3)),
			this.faces.get(4).merge(other.faces.get(4)),
			this.faces.get(5).merge(other.faces.get(5))
		);

		return new ResolvedCubeTextures(faces);
	}

	public ResolvedCubeTextures filter(boolean onlyUp) {
		boolean empty = true;
		var faces = new ArrayList<FaceTexture>(6);

		for (int i = 0; i < 6; i++) {
			var face = this.faces.get(i);

			if ((!onlyUp || i == 1) && face != FaceTexture.EMPTY) {
				faces.add(face);
				empty = false;
			} else {
				faces.add(FaceTexture.EMPTY);
			}
		}

		return empty ? EMPTY : new ResolvedCubeTextures(faces);
	}
}
