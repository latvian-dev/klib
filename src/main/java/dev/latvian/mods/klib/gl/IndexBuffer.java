package dev.latvian.mods.klib.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.pipeline.IndexType;
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology;
import org.jetbrains.annotations.Nullable;

public interface IndexBuffer extends AutoCloseable {
	static IndexBuffer of(PrimitiveTopology mode, int vertices) {
		var sequentialBuffer = RenderSystem.getSequentialBuffer(mode);
		int indices = mode.indexCount(vertices);
		return new SharedIndexBuffer(sequentialBuffer, vertices, indices);
	}

	static IndexBuffer of(MeshData meshData) {
		var mode = meshData.drawState().primitiveTopology();
		int vertices = meshData.drawState().vertexCount();
		var indexBuffer = meshData.indexBuffer();

		if (indexBuffer == null) {
			return of(mode, vertices);
		} else {
			int count = meshData.drawState().indexCount();
			// BufferType.INDICES, BufferUsage.STATIC_WRITE
			var buffer = RenderSystem.getDevice().createBuffer(null, GpuBuffer.USAGE_INDEX | GpuBuffer.USAGE_MAP_WRITE, indexBuffer);
			var type = meshData.drawState().indexType();
			return new StaticIndexBuffer(buffer, type, vertices, count);
		}
	}

	GpuBuffer buffer();

	IndexType type();

	@Override
	default void close() {
	}

	@Nullable
	default IndexBuffer staticBuffer() {
		return this;
	}
}
