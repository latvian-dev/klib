package dev.latvian.mods.klib.backport;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;

public record BlendEquation(BlendFactor sourceFactor, BlendFactor destFactor, BlendOp op) {
	public BlendEquation(BlendFactor sourceFactor, BlendFactor destFactor) {
		this(sourceFactor, destFactor, BlendOp.ADD);
	}

	public BlendEquation(SourceFactor sourceFactor, DestFactor destFactor) {
		this(BlendFactor.fromSourceFactor(sourceFactor), BlendFactor.fromDestFactor(destFactor), BlendOp.ADD);
	}
}
