package dev.latvian.mods.klib.net;

import dev.latvian.mods.klib.registry.CustomRegistryValueInfo;
import dev.latvian.mods.klib.util.ID;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncCustomRegistryValuesPayload(CustomRegistryValueInfo info) implements CustomPacketPayload {
	public static final Type<SyncCustomRegistryValuesPayload> TYPE = new Type<>(ID.klib("sync_custom_registry_values"));

	public static final StreamCodec<ByteBuf, SyncCustomRegistryValuesPayload> STREAM_CODEC = CustomRegistryValueInfo.STREAM_CODEC.map(SyncCustomRegistryValuesPayload::new, SyncCustomRegistryValuesPayload::info);

	@Override
	public Type<SyncCustomRegistryValuesPayload> type() {
		return TYPE;
	}
}
