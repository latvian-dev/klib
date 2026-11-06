package dev.latvian.mods.klib.net;

import dev.latvian.mods.klib.platform.PlatformHelper;
import dev.latvian.mods.klib.platform.PlatformType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

public interface KLibByteBuf {
	static RegistryFriendlyByteBuf create(ByteBuf source, RegistryAccess access, PlatformType platformType) {
		return PlatformHelper.INSTANCE.createKLibBuffer(source, access, platformType);
	}

	static RegistryFriendlyByteBuf create(ByteBuf source) {
		return PlatformHelper.INSTANCE.createKLibBuffer(source);
	}

	static RegistryFriendlyByteBuf with(ByteBuf source, RegistryAccess access, PlatformType platformType, @Nullable Object value) {
		var buf = create(source, access, platformType);

		if (buf instanceof KLibByteBuf kbuf) {
			kbuf.setSpecialValue(value);
		}

		return buf;
	}

	static RegistryFriendlyByteBuf with(ByteBuf parent, @Nullable Object value) {
		var buf = create(parent);

		if (buf instanceof KLibByteBuf kbuf) {
			kbuf.setSpecialValue(value);
		}

		return buf;
	}

	@Nullable
	static Object get(ByteBuf buf) {
		return buf instanceof KLibByteBuf kbuf ? kbuf.getSpecialValue() : null;
	}

	void setSpecialValue(@Nullable Object value);

	@Nullable
	Object getSpecialValue();
}
