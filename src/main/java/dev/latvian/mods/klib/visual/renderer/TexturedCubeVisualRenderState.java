package dev.latvian.mods.klib.visual.renderer;

import dev.latvian.mods.klib.render.SubmitContext;
import dev.latvian.mods.klib.texture.FaceTextureRenderState;
import dev.latvian.mods.klib.vertex.VertexCallback;
import dev.latvian.mods.klib.visual.TexturedCubeVisual;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TexturedCubeVisualRenderState implements VisualRenderState {
	public Vec3 position;
	public float sizeX;
	public float sizeY;
	public float sizeZ;
	public FaceTextureRenderState down;
	public FaceTextureRenderState up;
	public FaceTextureRenderState north;
	public FaceTextureRenderState south;
	public FaceTextureRenderState west;
	public FaceTextureRenderState east;

	@Nullable
	public static VisualRenderState extract(TexturedCubeVisual visual, TexturedCubeVisualRenderState state, VisualExactContext ctx) {
		state.position = visual.position().value().get(ctx.numberContext());

		if (state.position == null) {
			return null;
		}

		var mc = ctx.mc();
		state.down = FaceTextureRenderState.extract(mc, visual.textures().down().orElse(null), state::drawDown);
		state.up = FaceTextureRenderState.extract(mc, visual.textures().up().orElse(null), state::drawUp);
		state.north = FaceTextureRenderState.extract(mc, visual.textures().north().orElse(null), state::drawNorth);
		state.south = FaceTextureRenderState.extract(mc, visual.textures().south().orElse(null), state::drawSouth);
		state.west = FaceTextureRenderState.extract(mc, visual.textures().west().orElse(null), state::drawWest);
		state.east = FaceTextureRenderState.extract(mc, visual.textures().east().orElse(null), state::drawEast);

		if (!(state.down == null || state.up == null || state.north == null || state.south == null || state.west == null || state.east == null)) {
			return null;
		}

		return state;
	}

	private void drawDown(VertexCallback callback) {
		callback.downQuad(0F, 0F, 0F, sizeX, sizeZ);
	}

	private void drawUp(VertexCallback callback) {
		callback.upQuad(sizeY, 0F, 0F, sizeX, sizeZ);
	}

	private void drawNorth(VertexCallback callback) {
		callback.northQuad(0F, 0F, 0F, sizeX, sizeY);
	}

	private void drawSouth(VertexCallback callback) {
		callback.southQuad(sizeZ, 0F, 0F, sizeX, sizeY);
	}

	private void drawWest(VertexCallback callback) {
		callback.westQuad(0F, 0F, 0F, sizeY, sizeZ);
	}

	private void drawEast(VertexCallback callback) {
		callback.eastQuad(sizeX, 0F, 0F, sizeY, sizeZ);
	}

	@Override
	public void submit(SubmitContext ctx) {
		ctx.push(position);

		if (down != null) {
			down.submit(ctx);
		}

		if (up != null) {
			up.submit(ctx);
		}

		if (north != null) {
			north.submit(ctx);
		}

		if (south != null) {
			south.submit(ctx);
		}

		if (west != null) {
			west.submit(ctx);
		}

		if (east != null) {
			east.submit(ctx);
		}

		ctx.pop();
	}
}
