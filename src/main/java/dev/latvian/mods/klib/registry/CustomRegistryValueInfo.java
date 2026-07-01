package dev.latvian.mods.klib.registry;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

import java.util.ArrayList;
import java.util.List;

public record CustomRegistryValueInfo<T>(RegistryKeys<T> registryKeys, List<ValueInfo<T>> valueInfos) {
	public record ValueInfo<T>(int index, ResourceKey<T> key, byte[] value) {
	}

	public static <T> CustomRegistryValueInfo<T> read(ByteBuf buf) {
		var registryKeys = RegistryKeys.<T>read(buf);
		int size = VarInt.read(buf);
		var valueInfos = new ArrayList<ValueInfo<T>>(size);

		for (int i = 0; i < size; i++) {
			int index = VarInt.read(buf);
			var key = registryKeys.streamCodec().decode(buf);
			var value = ByteBufCodecs.BYTE_ARRAY.decode(buf);
			valueInfos.add(new ValueInfo<>(index, key, value));
		}

		return new CustomRegistryValueInfo<>(registryKeys, List.copyOf(valueInfos));
	}

	public static <T> void write(ByteBuf buf, CustomRegistryValueInfo<T> info) {
		RegistryKeys.write(buf, info.registryKeys);
		VarInt.write(buf, info.valueInfos.size());

		for (var entry : info.valueInfos) {
			VarInt.write(buf, entry.index);
			info.registryKeys.streamCodec().encode(buf, entry.key);
			ByteBufCodecs.BYTE_ARRAY.encode(buf, entry.value);
		}
	}

	public static final StreamCodec<FriendlyByteBuf, CustomRegistryValueInfo<?>> STREAM_CODEC = new StreamCodec<>() {
		@Override
		public CustomRegistryValueInfo<?> decode(FriendlyByteBuf buf) {
			return read(buf);
		}

		@Override
		public void encode(FriendlyByteBuf buf, CustomRegistryValueInfo<?> value) {
			write(buf, value);
		}
	};
}
