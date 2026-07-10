package dev.latvian.mods.klib.util;

import com.google.gson.JsonElement;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.RefOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.common.conditions.ConditionalOps;

import java.util.Map;

public class JsonRegistryReloadListener<T> extends JsonCodecReloadListener<T> {
	private final CustomRegistry<?, T> registry;

	public JsonRegistryReloadListener(String rootPath, CustomRegistry<?, T> registry) {
		super(rootPath, registry.directCodec());
		this.registry = registry;
	}

	@Override
	public ConditionalOps<JsonElement> wrapOps(Identifier id, ConditionalOps<JsonElement> ops, JsonElement json) {
		return new RefOps<>(ops, registry.ref(id.getPath()));
	}

	@Override
	protected void apply(ResourceManager resourceManager, Map<Identifier, T> map) {
		registry.updateValues(map);
	}
}
