package dev.latvian.mods.klib.registry;

import dev.latvian.mods.klib.core.KLibConditionalOps;
import net.neoforged.neoforge.common.conditions.ConditionalOps;

public class RefOps<O, V> extends ConditionalOps<O> {
	public final Ref<V> ref;

	public RefOps(ConditionalOps<O> parent, Ref<V> ref) {
		super(parent, ((KLibConditionalOps) parent).klib$context());
		this.ref = ref;
	}
}
