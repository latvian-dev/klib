package dev.latvian.mods.klib.registry;

import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record CustomRegistryValueInfo(String registryId, List<ValueInfo> valueInfos) {
	public record ValueInfo(int index, String key, byte[] value) {
		public static final StreamCodec<ByteBuf, ValueInfo> STREAM_CODEC = CompositeStreamCodec.of(
			ByteBufCodecs.VAR_INT, ValueInfo::index,
			KLibStreamCodecs.INTERN_STRING, ValueInfo::key,
			ByteBufCodecs.BYTE_ARRAY, ValueInfo::value,
			ValueInfo::new
		);
	}

	public static final StreamCodec<ByteBuf, CustomRegistryValueInfo> STREAM_CODEC = CompositeStreamCodec.of(
		KLibStreamCodecs.INTERN_STRING, CustomRegistryValueInfo::registryId,
		KLibStreamCodecs.listOf(ValueInfo.STREAM_CODEC), CustomRegistryValueInfo::valueInfos,
		CustomRegistryValueInfo::new
	);
}
