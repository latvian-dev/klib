package dev.latvian.mods.klib.net;

import dev.latvian.mods.klib.registry.CustomRegistryMetaInfo;
import dev.latvian.mods.klib.util.ID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record SyncCustomRegistryMetaPayload(List<CustomRegistryMetaInfo<?>> registries) implements CustomPacketPayload {
	public static final Type<SyncCustomRegistryMetaPayload> TYPE = new Type<>(ID.klib("sync_custom_registry_meta"));

	public static final StreamCodec<FriendlyByteBuf, SyncCustomRegistryMetaPayload> STREAM_CODEC = CustomRegistryMetaInfo.STREAM_CODEC.apply(ByteBufCodecs.list()).map(SyncCustomRegistryMetaPayload::new, SyncCustomRegistryMetaPayload::registries);

	@Override
	public Type<SyncCustomRegistryMetaPayload> type() {
		return TYPE;
	}
}
