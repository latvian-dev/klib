package dev.latvian.mods.kmath.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <a href="https://easings.net/">Source</a>
 */
public enum EasingGroup {
	LINEAR("linear", Easing.LINEAR),
	SMOOTHSTEP("smoothstep", Easing.SMOOTHSTEP),
	ISMOOTHSTEP("ismoothstep", Easing.ISMOOTHSTEP),
	SMOOTHERSTEP("smootherstep", Easing.SMOOTHERSTEP),
	SINE("sine", Easing.SINE_IN, Easing.SINE_OUT, Easing.SINE_IN_OUT),
	QUAD("quad", Easing.QUAD_IN, Easing.QUAD_OUT, Easing.QUAD_IN_OUT),
	CUBIC("cubic", Easing.CUBIC_IN, Easing.CUBIC_OUT, Easing.CUBIC_IN_OUT),
	QUART("quart", Easing.QUART_IN, Easing.QUART_OUT, Easing.QUART_IN_OUT),
	QUINT("quint", Easing.QUINT_IN, Easing.QUINT_OUT, Easing.QUINT_IN_OUT),
	EXPO("expo", Easing.EXPO_IN, Easing.EXPO_OUT, Easing.EXPO_IN_OUT),
	CIRC("circ", Easing.CIRC_IN, Easing.CIRC_OUT, Easing.CIRC_IN_OUT),
	BACK("back", Easing.BACK_IN, Easing.BACK_OUT, Easing.BACK_IN_OUT),
	ELASTIC("elastic", Easing.ELASTIC_IN, Easing.ELASTIC_OUT, Easing.ELASTIC_IN_OUT),
	BOUNCE("bounce", Easing.BOUNCE_IN, Easing.BOUNCE_OUT, Easing.BOUNCE_IN_OUT);

	public static final EasingGroup[] VALUES = values();
	public static final Map<String, EasingGroup> GROUPS = new LinkedHashMap<>();
	public static final Map<String, Easing> FUNCTIONS = new LinkedHashMap<>();

	static {
		for (var group : VALUES) {
			GROUPS.put(group.id, group);

			if (group.in == group.out && group.in == group.inOut) {
				FUNCTIONS.put(group.id, group.in);
			} else {
				FUNCTIONS.put(group.id + "_in", group.in);
				FUNCTIONS.put(group.id + "_out", group.out);
				FUNCTIONS.put(group.id + "_in_out", group.inOut);
			}
		}
	}

	public final String id;
	public final Easing in;
	public final Easing out;
	public final Easing inOut;

	EasingGroup(String id, Easing in, Easing out, Easing inOut) {
		this.id = id;
		this.in = in;
		this.out = out;
		this.inOut = inOut;
	}

	EasingGroup(String id, Easing common) {
		this(id, common, common, common);
	}
}
