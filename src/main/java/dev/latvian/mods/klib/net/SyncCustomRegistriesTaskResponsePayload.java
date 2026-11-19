package dev.latvian.mods.klib.net;

import dev.latvian.mods.klib.util.ID;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public enum SyncCustomRegistriesTaskResponsePayload implements CustomPacketPayload {
	INSTANCE;

	public static final Type<SyncCustomRegistriesTaskResponsePayload> TYPE = new Type<>(ID.klib("sync_custom_registry_meta_response"));

	public static final StreamCodec<ByteBuf, SyncCustomRegistriesTaskResponsePayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

	@Override
	public Type<SyncCustomRegistriesTaskResponsePayload> type() {
		return TYPE;
	}
}
