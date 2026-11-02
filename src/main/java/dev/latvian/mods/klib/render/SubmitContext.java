package dev.latvian.mods.klib.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.latvian.mods.klib.color.Color;
import dev.latvian.mods.klib.math.Identity;
import dev.latvian.mods.klib.shape.Shape;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.Position;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.SubmitCustomGeometryEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionfc;
import org.joml.Vector3dc;

public record SubmitContext(
	Minecraft mc,
	@Nullable SubmitCustomGeometryEvent event,
	SubmitNodeCollector collector,
	CameraRenderState cameraState,
	Vec3 cameraPos,
	PoseStack poseStack,
	float lineWidth
) {
	public SubmitContext(Minecraft mc, SubmitCustomGeometryEvent event) {
		this(
			mc,
			event,
			event.getSubmitNodeCollector(),
			event.getLevelRenderState().cameraRenderState,
			event.getLevelRenderState().cameraRenderState.pos,
			event.getPoseStack(),
			mc.gameRenderer.gameRenderState().windowRenderState.appropriateLineWidth
		);
	}

	public SubmitContext(Minecraft mc, SubmitNodeCollector collector, CameraRenderState cameraRenderState) {
		this(
			mc,
			null,
			collector,
			cameraRenderState,
			cameraRenderState.pos,
			new PoseStack(),
			mc.gameRenderer.gameRenderState().windowRenderState.appropriateLineWidth
		);
	}

	public void push(double x, double y, double z) {
		poseStack.pushPose();
		poseStack.translate(x - cameraPos.x, y - cameraPos.y, z - cameraPos.z);
	}

	public void push(Position pos) {
		push(pos.x(), pos.y(), pos.z());
	}

	public void push(Vector3dc pos) {
		push(pos.x(), pos.y(), pos.z());
	}

	public void pop() {
		poseStack.popPose();
	}

	public void rotate(Quaternionfc rotation) {
		if (rotation != Identity.QUATERNION) {
			poseStack.rotate(rotation);
		}
	}

	public void scale(float scale) {
		if (scale != 1F) {
			poseStack.scale(scale, scale, scale);
		}
	}

	public void submit(RenderType renderType, SubmitNodeCollector.CustomGeometryRenderer renderer) {
		collector.submitCustomGeometry(poseStack, renderType, renderer);
	}

	public void submitQuads(Shape shape, Color color) {
		submit(RenderTypes.debugFilledBox(), (pose, buffer) -> shape.buildQuads(pose.transform(buffer).withColor(color)));
	}

	public void submitLines(Shape shape, Color color) {
		submit(RenderTypes.lines(), (pose, buffer) -> shape.buildLines(pose.transform(buffer).withColor(color).withLineWidth(lineWidth)));
	}

	public void submitOuterLines(Shape shape, Color color) {
		submit(RenderTypes.lines(), (pose, buffer) -> shape.buildOuterLines(pose.transform(buffer).withColor(color).withLineWidth(lineWidth)));
	}
}
