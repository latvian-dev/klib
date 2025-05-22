package dev.latvian.mods.kmath.core.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.kmath.core.KMathPoseStackPose;
import dev.latvian.mods.kmath.vertex.VertexCallback;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PoseStack.Pose.class)
public class PoseStackPoseMixin implements KMathPoseStackPose {
	@Shadow
	@Final
	private Matrix4f pose;

	@Shadow
	@Final
	private Matrix3f normal;

	@Shadow
	private boolean trustedNormals;

	@Override
	public VertexCallback transform(VertexCallback callback) {
		boolean n = trustedNormals && normal.equals(KMathPoseStackPose.IDENTITY_3x3);
		boolean p = pose.equals(KMathPoseStackPose.IDENTITY_4x4);

		if (n && p) {
			return callback;
		} else if (n) {
			return callback.withTransformedPositions(pose);
		} else if (p) {
			return callback.withTransformedNormals(normal, !trustedNormals);
		} else {
			return callback.withTransformedPositionsAndNormals(pose, normal, !trustedNormals);
		}
	}

	@Override
	public VertexCallback transformPositions(VertexCallback callback) {
		boolean p = pose.equals(KMathPoseStackPose.IDENTITY_4x4);

		if (p) {
			return callback;
		} else {
			return callback.withTransformedPositions(pose);
		}
	}

	@Override
	public VertexCallback transformNormals(VertexCallback callback) {
		boolean n = trustedNormals && normal.equals(KMathPoseStackPose.IDENTITY_3x3);

		if (n) {
			return callback;
		} else {
			return callback.withTransformedNormals(normal, !trustedNormals);
		}
	}
}
