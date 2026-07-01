package dev.latvian.mods.klib.registry;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.List;

public record CustomRegistryMetaInfo<T>(RegistryKeys<T> registryKeys, List<TypeInfo<T>> typeInfos) {
	public record TypeInfo<T>(int index, ResourceKey<T> key, int version) {
	}

	public static <T> CustomRegistryMetaInfo<T> read(ByteBuf buf) {
		var registryKeys = RegistryKeys.<T>read(buf);
		int size = VarInt.read(buf);

		var list = new ArrayList<TypeInfo<T>>(size);

		for (int i = 0; i < size; i++) {
			int index = VarInt.read(buf);
			var key = registryKeys.streamCodec().decode(buf);
			int version = VarInt.read(buf);
			list.add(new TypeInfo<>(index, key, version));
		}

		return new CustomRegistryMetaInfo<>(registryKeys, List.copyOf(list));
	}

	public static <T> void write(ByteBuf buf, CustomRegistryMetaInfo<T> info) {
		RegistryKeys.write(buf, info.registryKeys);
		VarInt.write(buf, info.typeInfos.size());

		for (var entry : info.typeInfos) {
			VarInt.write(buf, entry.index);
			info.registryKeys.streamCodec().encode(buf, entry.key);
			VarInt.write(buf, entry.version);
		}
	}

	public static final StreamCodec<FriendlyByteBuf, CustomRegistryMetaInfo<?>> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public CustomRegistryMetaInfo<?> decode(FriendlyByteBuf buf) {
			return read(buf);
		}

		@Override
		public void encode(FriendlyByteBuf buf, CustomRegistryMetaInfo<?> value) {
			write(buf, value);
		}
	};
}
