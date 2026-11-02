package dev.latvian.mods.klib.visual;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.registry.CustomRegistryValue;
import dev.latvian.mods.klib.registry.DynamicType;
import dev.latvian.mods.klib.registry.Ref;
import dev.latvian.mods.klib.registry.UnitType;
import net.minecraft.core.Vec3i;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface Visual extends CustomRegistryValue<RegistryFriendlyByteBuf, Visual> {
	UnitType<RegistryFriendlyByteBuf, Visual> EMPTY = UnitType.create("empty", EmptyVisual.INSTANCE);

	CustomRegistry<RegistryFriendlyByteBuf, Visual> REGISTRY = CustomRegistry.create("visual", EMPTY);

	Codec<Ref<Visual>> CODEC = REGISTRY.codec();
	StreamCodec<RegistryFriendlyByteBuf, Ref<Visual>> STREAM_CODEC = REGISTRY.streamCodec();
	DataType<Ref<Visual>> DATA_TYPE = REGISTRY.dataType();

	int PROPERTY_RENDERER = DynamicType.newPropertyId();

	static void builtInTypes(CustomRegistryTypeCollector<RegistryFriendlyByteBuf, Visual> registry) {
		registry.register(ShapeVisual.TYPE);
		registry.register(TexturedCubeVisual.TYPE);
	}

	@Override
	default CustomRegistry<RegistryFriendlyByteBuf, Visual> getRegistry() {
		return REGISTRY;
	}

	default boolean containsBlock(Vec3i block) {
		return false;
	}
}
