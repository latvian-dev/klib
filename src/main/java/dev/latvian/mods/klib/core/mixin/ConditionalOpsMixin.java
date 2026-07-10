package dev.latvian.mods.klib.core.mixin;

import dev.latvian.mods.klib.core.KLibConditionalOps;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ConditionalOps.class)
public abstract class ConditionalOpsMixin implements KLibConditionalOps {
	@Override
	@Accessor("context")
	public abstract ICondition.IContext klib$context();
}
