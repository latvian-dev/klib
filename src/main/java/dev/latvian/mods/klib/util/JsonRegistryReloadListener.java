package dev.latvian.mods.klib.util;

import com.google.gson.JsonElement;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.RefOps;
import dev.latvian.mods.klib.registry.RefType;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.neoforge.common.conditions.ConditionalOps;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiConsumer;

public class JsonRegistryReloadListener<T> extends JsonCodecReloadListener<T> {
	public final CustomRegistry<?, T> registry;
	public final RefType refType;

	public JsonRegistryReloadListener(String rootPath, CustomRegistry<?, T> registry, RefType refType) {
		super(rootPath, registry.directCodec());
		this.registry = registry;
		this.refType = refType;
	}

	@Override
	public ConditionalOps<JsonElement> wrapOps(Identifier id, ConditionalOps<JsonElement> ops, JsonElement json) {
		return new RefOps<>(ops, registry.ref(id.getPath()));
	}

	@Override
	protected void apply(ResourceManager resourceManager, Map<Identifier, T> map) {
		var entries = new ArrayList<Map.Entry<String, T>>(map.size());

		for (var entry : map.entrySet()) {
			entries.add(Map.entry(entry.getKey().getPath(), entry.getValue()));
		}

		generate((key, value) -> entries.add(Map.entry(key, value)));
		registry.updateValues(entries, refType);
	}

	public void generate(BiConsumer<String, T> callback) {
	}
}
