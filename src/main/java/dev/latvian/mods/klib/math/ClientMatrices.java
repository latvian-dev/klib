package dev.latvian.mods.klib.math;

import org.joml.Matrix4f;

public interface ClientMatrices {
	Matrix4f MODEL_VIEW = new Matrix4f();
	Matrix4f PROJECTION = new Matrix4f();
	Matrix4f WORLD = new Matrix4f();
	Matrix4f INVERSE_WORLD = new Matrix4f();
	Matrix4f PERSPECTIVE = new Matrix4f();
	Matrix4f FRUSTUM = new Matrix4f();
}
