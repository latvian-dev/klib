package dev.latvian.mods.klib.core.api.neoforge;

import dev.latvian.mods.klib.core.NoMixinException;
import net.neoforged.neoforge.common.conditions.ICondition;

public interface KLibConditionalOps {
	default ICondition.IContext klib$context() {
		throw new NoMixinException(this);
	}
}
