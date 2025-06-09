package dev.latvian.mods.klib.vertex.function;

import org.joml.Vector3f;

import java.util.function.Consumer;

@FunctionalInterface
public interface VertexFunction extends Consumer<Vector3f> {
}
