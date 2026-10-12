package dev.latvian.mods.klib.gl;

import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.pipeline.IndexType;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

record StaticIndexBuffer(GpuBuffer buffer, IndexType type, int vertices, int indices) implements IndexBuffer {
	@Override
	public void close() {
		buffer.close();
	}

	@Override
	public @NotNull String toString() {
		return "StaticIndexBuffer[size=%,d bytes, vertices=%,d, indices=%,d, type=%s]".formatted(buffer.size(), vertices, indices, type.toString().toLowerCase(Locale.ROOT));
	}
}
