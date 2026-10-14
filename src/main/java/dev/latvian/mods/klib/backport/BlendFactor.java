package dev.latvian.mods.klib.backport;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;

public enum BlendFactor {
	CONSTANT_ALPHA(SourceFactor.CONSTANT_ALPHA, DestFactor.CONSTANT_ALPHA),
	CONSTANT_COLOR(SourceFactor.CONSTANT_COLOR, DestFactor.CONSTANT_COLOR),
	DST_ALPHA(SourceFactor.DST_ALPHA, DestFactor.DST_ALPHA),
	DST_COLOR(SourceFactor.DST_COLOR, DestFactor.DST_COLOR),
	ONE(SourceFactor.ONE, DestFactor.ONE),
	ONE_MINUS_CONSTANT_ALPHA(SourceFactor.ONE_MINUS_CONSTANT_ALPHA, DestFactor.ONE_MINUS_CONSTANT_ALPHA),
	ONE_MINUS_CONSTANT_COLOR(SourceFactor.ONE_MINUS_CONSTANT_COLOR, DestFactor.ONE_MINUS_CONSTANT_COLOR),
	ONE_MINUS_DST_ALPHA(SourceFactor.ONE_MINUS_DST_ALPHA, DestFactor.ONE_MINUS_DST_ALPHA),
	ONE_MINUS_DST_COLOR(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_DST_COLOR),
	ONE_MINUS_SRC_ALPHA(SourceFactor.ONE_MINUS_SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA),
	ONE_MINUS_SRC_COLOR(SourceFactor.ONE_MINUS_SRC_COLOR, DestFactor.ONE_MINUS_SRC_COLOR),
	SRC_ALPHA(SourceFactor.SRC_ALPHA, DestFactor.SRC_ALPHA),
	SRC_ALPHA_SATURATE(SourceFactor.SRC_ALPHA_SATURATE, null),
	SRC_COLOR(SourceFactor.SRC_COLOR, DestFactor.SRC_COLOR),
	ZERO(SourceFactor.ZERO, DestFactor.ZERO);

	public static BlendFactor fromSourceFactor(SourceFactor factor) {
		return switch (factor) {
			case CONSTANT_ALPHA -> CONSTANT_ALPHA;
			case CONSTANT_COLOR -> CONSTANT_COLOR;
			case DST_ALPHA -> DST_ALPHA;
			case DST_COLOR -> DST_COLOR;
			case ONE -> ONE;
			case ONE_MINUS_CONSTANT_ALPHA -> ONE_MINUS_CONSTANT_ALPHA;
			case ONE_MINUS_CONSTANT_COLOR -> ONE_MINUS_CONSTANT_COLOR;
			case ONE_MINUS_DST_ALPHA -> ONE_MINUS_DST_ALPHA;
			case ONE_MINUS_DST_COLOR -> ONE_MINUS_DST_COLOR;
			case ONE_MINUS_SRC_ALPHA -> ONE_MINUS_SRC_ALPHA;
			case ONE_MINUS_SRC_COLOR -> ONE_MINUS_SRC_COLOR;
			case SRC_ALPHA -> SRC_ALPHA;
			case SRC_ALPHA_SATURATE -> SRC_ALPHA_SATURATE;
			case SRC_COLOR -> SRC_COLOR;
			case ZERO -> ZERO;
		};
	}

	public static BlendFactor fromDestFactor(DestFactor factor) {
		return switch (factor) {
			case CONSTANT_ALPHA -> CONSTANT_ALPHA;
			case CONSTANT_COLOR -> CONSTANT_COLOR;
			case DST_ALPHA -> DST_ALPHA;
			case DST_COLOR -> DST_COLOR;
			case ONE -> ONE;
			case ONE_MINUS_CONSTANT_ALPHA -> ONE_MINUS_CONSTANT_ALPHA;
			case ONE_MINUS_CONSTANT_COLOR -> ONE_MINUS_CONSTANT_COLOR;
			case ONE_MINUS_DST_ALPHA -> ONE_MINUS_DST_ALPHA;
			case ONE_MINUS_DST_COLOR -> ONE_MINUS_DST_COLOR;
			case ONE_MINUS_SRC_ALPHA -> ONE_MINUS_SRC_ALPHA;
			case ONE_MINUS_SRC_COLOR -> ONE_MINUS_SRC_COLOR;
			case SRC_ALPHA -> SRC_ALPHA;
			case SRC_COLOR -> SRC_COLOR;
			case ZERO -> ZERO;
		};
	}

	public final SourceFactor src;
	public final DestFactor dst;

	BlendFactor(SourceFactor src, DestFactor dst) {
		this.src = src;
		this.dst = dst;
	}
}