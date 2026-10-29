package dev.latvian.mods.klib.registry;

public interface RefOptimizer<V> extends WithRef<V> {
	default Ref<V> optimize(Ref<V> self) {
		return self;
	}

	default Ref<V> optimizedRef() {
		return optimize(ref());
	}
}
