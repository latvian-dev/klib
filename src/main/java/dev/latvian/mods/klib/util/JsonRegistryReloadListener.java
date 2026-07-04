package dev.latvian.mods.klib.util;

import dev.latvian.mods.klib.registry.CustomRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Map;

public class JsonRegistryReloadListener<T> extends JsonCodecReloadListener<T> {
	private final CustomRegistry<?, T> registry;

	public JsonRegistryReloadListener(String rootPath, CustomRegistry<?, T> registry) {
		super(rootPath, registry.directCodec(), false);
		this.registry = registry;
	}

	@Override
	protected void apply(ResourceManager resourceManager, Map<Identifier, T> map) {
		registry.updateValues(map);
	}
}
