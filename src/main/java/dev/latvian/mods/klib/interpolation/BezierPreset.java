package dev.latvian.mods.klib.interpolation;

import org.joml.Vector2f;
import org.joml.Vector2fc;

public enum BezierPreset {
	LINEAR("Linear", 0.000F, 0.000F, 1.000F, 1.000F),
	IN_SINE("In Sine", 0.470F, 0.000F, 0.745F, 0.715F),
	IN_QUAD("In Quad", 0.550F, 0.085F, 0.680F, 0.530F),
	IN_CUBIC("In Cubic", 0.550F, 0.055F, 0.675F, 0.190F),
	IN_QUART("In Quart", 0.895F, 0.030F, 0.685F, 0.220F),
	IN_QUINT("In Quint", 0.755F, 0.050F, 0.855F, 0.060F),
	IN_EXPO("In Expo", 0.950F, 0.050F, 0.795F, 0.035F),
	IN_CIRC("In Circ", 0.600F, 0.040F, 0.980F, 0.335F),
	IN_BACK("In Back", 0.600F, -0.28F, 0.735F, 0.045F),
	OUT_SINE("Out Sine", 0.390F, 0.575F, 0.565F, 1.000F),
	OUT_QUAD("Out Quad", 0.250F, 0.460F, 0.450F, 0.940F),
	OUT_CUBIC("Out Cubic", 0.215F, 0.610F, 0.355F, 1.000F),
	OUT_QUART("Out Quart", 0.165F, 0.840F, 0.440F, 1.000F),
	OUT_QUINT("Out Quint", 0.230F, 1.000F, 0.320F, 1.000F),
	OUT_EXPO("Out Expo", 0.190F, 1.000F, 0.220F, 1.000F),
	OUT_CIRC("Out Circ", 0.075F, 0.820F, 0.165F, 1.000F),
	OUT_BACK("Out Back", 0.175F, 0.885F, 0.320F, 1.275F),
	INOUT_SINE("InOut Sine", 0.445F, 0.050F, 0.550F, 0.950F),
	INOUT_QUAD("InOut Quad", 0.455F, 0.030F, 0.515F, 0.955F),
	INOUT_CUBIC("InOut Cubic", 0.645F, 0.045F, 0.355F, 1.000F),
	INOUT_QUART("InOut Quart", 0.770F, 0.000F, 0.175F, 1.000F),
	INOUT_QUINT("InOut Quint", 0.860F, 0.000F, 0.070F, 1.000F),
	INOUT_EXPO("InOut Expo", 1.000F, 0.000F, 0.000F, 1.000F),
	INOUT_CIRC("InOut Circ", 0.785F, 0.135F, 0.150F, 0.860F),
	INOUT_BACK("InOut Back", 0.680F, -0.55F, 0.265F, 1.550F);

	public static final BezierPreset[] VALUES = values();

	public final String name;
	public final Vector2fc p1;
	public final Vector2fc p2;

	BezierPreset(String name, float x1, float y1, float x2, float y2) {
		this.name = name;
		this.p1 = new Vector2f(x1, y1);
		this.p2 = new Vector2f(x2, y2);
	}

	public void set(Vector2f p1, Vector2f p2) {
		p1.set(this.p1);
		p2.set(this.p2);
	}
}
