package dev.latvian.mods.klib.util;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.KLib;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class JsonCodecReloadListener<T> extends JsonReloadListener {
	public static class Dummy<T> extends JsonCodecReloadListener<T> {
		public final Map<Identifier, T> map;

		public Dummy(String rootPath, Codec<T> codec, String includeId) {
			super(rootPath, codec, includeId);
			this.map = new Object2ObjectOpenHashMap<>();
		}

		public void load(ResourceManager manager) {
			var profiler = Profiler.get();
			var prepare = prepare(manager, profiler);
			apply(prepare, manager, profiler);
		}

		@Override
		protected void apply(ResourceManager resourceManager, Map<Identifier, T> map) {
			this.map.clear();
			this.map.putAll(map);
		}
	}

	public final Codec<T> codec;
	public final String includeId;

	public JsonCodecReloadListener(String rootPath, Codec<T> codec, String includeId) {
		super(rootPath);
		this.codec = codec;
		this.includeId = includeId;
	}

	@Nullable
	protected T finalize(T t) {
		return t;
	}

	@Override
	protected void apply(Map<Identifier, JsonElement> from, ResourceManager resourceManager, ProfilerFiller profiler) {
		var map = new HashMap<Identifier, CompletableFuture<T>>();
		var ops = makeConditionalOps();

		for (var entry : from.entrySet()) {
			var id = entry.getKey();

			try {
				var json = entry.getValue();

				if (!includeId.isEmpty()) {
					json.getAsJsonObject().addProperty(includeId, id.toString());
				}

				map.put(id, CompletableFuture.supplyAsync(() -> {
					var decoded = codec.parse(ops, json);

					if (decoded.error().isPresent()) {
						KLib.LOGGER.error("Error while parsing " + id.withPath(p -> rootPath + "/" + p) + ": " + decoded.error().get());
						return null;
					} else {
						return finalize(decoded.result().orElseThrow());
					}
				}, Util.backgroundExecutor()));
			} catch (Exception ex) {
				KLib.LOGGER.error("Error while parsing " + id.withPath(p -> rootPath + "/" + p), ex);
			}
		}

		CompletableFuture.allOf(map.values().toArray(new CompletableFuture[0])).join();

		var finalMap = new Object2ObjectOpenHashMap<Identifier, T>();

		for (var entry : map.entrySet()) {
			try {
				var v = entry.getValue().get();

				if (v != null) {
					finalMap.put(entry.getKey(), v);
				}
			} catch (Exception ex) {
				KLib.LOGGER.error("Error while parsing " + entry.getKey().withPath(p -> rootPath + "/" + p), ex);
			}
		}

		apply(resourceManager, finalMap);
	}

	protected abstract void apply(ResourceManager resourceManager, Map<Identifier, T> map);
}
