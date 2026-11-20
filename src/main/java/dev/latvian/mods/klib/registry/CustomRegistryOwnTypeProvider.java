package dev.latvian.mods.klib.registry;

import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.Nullable;

public interface CustomRegistryOwnTypeProvider<B extends ByteBuf, T> extends DynamicTypePropertyProvider {
	@Nullable
	default CustomRegistryType<B, T> type() {
		return null;
	}

	@Override
	@Nullable
	default <P> P getDynamicTypeProperty(int property, Class<P> type) {
		if (type() instanceof DynamicType<?, ?> dynamicType) {
			return dynamicType.getDynamicTypeProperty(property, type);
		}

		return null;
	}
}
