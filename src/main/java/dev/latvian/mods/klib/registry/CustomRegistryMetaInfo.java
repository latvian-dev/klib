package dev.latvian.mods.klib.registry;

import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.KLibStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record CustomRegistryMetaInfo(String registryId, List<TypeInfo> typeInfos) {
	public record TypeInfo(int index, String key, int version) {
		public static final StreamCodec<ByteBuf, TypeInfo> STREAM_CODEC = CompositeStreamCodec.of(
			ByteBufCodecs.VAR_INT, TypeInfo::index,
			ByteBufCodecs.STRING_UTF8, TypeInfo::key,
			ByteBufCodecs.VAR_INT, TypeInfo::version,
			TypeInfo::new
		);
	}

	public static final StreamCodec<ByteBuf, CustomRegistryMetaInfo> STREAM_CODEC = CompositeStreamCodec.of(
		ByteBufCodecs.STRING_UTF8, CustomRegistryMetaInfo::registryId,
		KLibStreamCodecs.listOf(TypeInfo.STREAM_CODEC), CustomRegistryMetaInfo::typeInfos,
		CustomRegistryMetaInfo::new
	);
}
