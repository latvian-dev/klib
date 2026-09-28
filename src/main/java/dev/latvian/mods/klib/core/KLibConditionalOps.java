package dev.latvian.mods.klib.core;

import net.neoforged.neoforge.common.conditions.ICondition;

public interface KLibConditionalOps {
	default ICondition.IContext klib$context() {
		throw new NoMixinException(this);
	}
}
