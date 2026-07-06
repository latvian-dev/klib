package dev.latvian.mods.klib.registry;

import io.netty.buffer.ByteBuf;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public interface CustomRegistryValue<B extends ByteBuf, T> extends WithKey<T>, RefOptimizer<T>, CustomRegistryOwnTypeProvider<B, T> {
	CustomRegistry<B, T> getRegistry();

	@Override
	@Nullable
	default ResourceKey<T> optionalKey() {
		return getRegistry().getKey((T) this);
	}

	default Ref<T> ref() {
		return getRegistry().ref((T) this);
	}
}
