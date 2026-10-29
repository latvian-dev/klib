package dev.latvian.mods.klib.icon;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface Icon extends CustomRegistryValue<RegistryFriendlyByteBuf, Icon> {
	UnitType<RegistryFriendlyByteBuf, Icon> EMPTY = UnitType.create("empty", EmptyIcon.INSTANCE);

	CustomRegistry<RegistryFriendlyByteBuf, Icon> REGISTRY = CustomRegistry.create("icon", EMPTY);
	Codec<Ref<Icon>> CODEC = REGISTRY.codec();
	StreamCodec<RegistryFriendlyByteBuf, Ref<Icon>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<Icon>> DATA_TYPE = REGISTRY.dataType();

	static void builtInTypes(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, Icon> registry) {
		registry.register(SimpleColorIcon.TYPE);
		registry.register(TextureIcon.TYPE);
		registry.register(ItemIcon.TYPE);
		registry.register(AtlasSpriteIcon.TYPE);
	}

	@Override
	default CustomRegistry<RegistryFriendlyByteBuf, Icon> getRegistry() {
		return REGISTRY;
	}
}
