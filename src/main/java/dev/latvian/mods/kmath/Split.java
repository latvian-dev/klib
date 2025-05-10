package dev.latvian.mods.kmath;

import dev.latvian.mods.kmath.texture.UV;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Consumer;

public class Split {
	public final int id;
	public final int split;
	public final int count;
	public final SplitBox[] boxes;
	public final float scale;
	public final Vector3fc min;
	public final Vector3fc max;
	public final AABB box;

	public Split(int id) {
		this.id = id;
		this.split = 1 << id;
		this.count = split * split * split;
		this.boxes = new SplitBox[count];
		this.scale = 1F / (float) split;
		float mns = -scale / 2F;
		float mxs = scale / 2F;
		this.min = new Vector3f(mns, mns, mns);
		this.max = new Vector3f(mxs, mxs, mxs);
		this.box = new AABB(mns, mns, mns, mxs, mxs, mxs);

		for (int x = 0; x < split; x++) {
			for (int y = 0; y < split; y++) {
				for (int z = 0; z < split; z++) {
					int i = x + z * split + y * split * split;

					float x0 = x * scale;
					float y0 = y * scale;
					float z0 = z * scale;
					float x1 = (x + 1F) * scale;
					float y1 = (y + 1F) * scale;
					float z1 = (z + 1F) * scale;

					boxes[i] = new SplitBox(this, i, (x + 0.5F) * scale, (y + 0.5F) * scale, (z + 0.5F) * scale, new UV[6], new QuadrilaterallyFacedConvexHexahedra(0.5F));

					boxes[i].uvs()[0] = new UV(x0, 1F - z1, x1, 1F - z0); // down
					boxes[i].uvs()[1] = new UV(x0, z0, x1, z1); // up
					boxes[i].uvs()[2] = new UV(x1, 1F - y1, x0, 1F - y0); // north
					boxes[i].uvs()[3] = new UV(x0, 1F - y1, x1, 1F - y0); // south
					boxes[i].uvs()[4] = new UV(z0, 1F - y1, z1, 1F - y0); // west
					boxes[i].uvs()[5] = new UV(1F - z1, 1F - y1, 1F - z0, 1F - y0); // east
				}
			}
		}
	}

	public void reshape(@Nullable Consumer<Vector3f> function) {
		for (var box : boxes) {
			box.shape().accept(function);
		}
	}

	public void reshape(long seed, float weirdness) {
		reshape(new RandomVector3fFunction(seed, weirdness));
	}

	@Override
	public String toString() {
		return "Split_1/" + split;
	}
}
