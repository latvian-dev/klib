package dev.latvian.mods.kmath.tex;

public class KLight {
	public static final KLight NORMAL = new KLight(0xF00000, 0xA0000);
	public static final KLight FULLBRIGHT = new KLight(0xF000F0, 0xA0000);
	public static final KLight NORMAL_HURT = new KLight(0xF00000, 0x30000);
	public static final KLight FULLBRIGHT_HURT = new KLight(0xF000F0, 0x30000);

	public static KLight get(boolean fullbright, boolean hurt) {
		return fullbright ? (hurt ? FULLBRIGHT_HURT : FULLBRIGHT) : (hurt ? NORMAL_HURT : NORMAL);
	}

	public final int light;
	public final int overlay;
	public final int lightU;
	public final int lightV;
	public final int overlayU;
	public final int overlayV;

	public KLight(int light, int overlay) {
		this.light = light;
		this.overlay = overlay;
		this.lightU = light & '\uffff';
		this.lightV = light >> 16 & '\uffff';
		this.overlayU = overlay & '\uffff';
		this.overlayV = overlay >> 16 & '\uffff';
	}
}
