package dev.latvian.mods.klib.math;

import com.mojang.serialization.Codec;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.data.DataType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record Line(Vec3 start, Vec3 end) {
	public static final Codec<Line> CODEC = Codec.DOUBLE.listOf(6, 6).xmap(l -> new Line(KMath.vec3(l.get(0), l.get(1), l.get(2)), KMath.vec3(l.get(3), l.get(4), l.get(5))), v -> List.of(v.start.x, v.start.y, v.start.z, v.end.x, v.end.y, v.end.z));

	public static final StreamCodec<ByteBuf, Line> STREAM_CODEC = CompositeStreamCodec.of(
		MCStreamCodecs.VEC3, Line::start,
		MCStreamCodecs.VEC3, Line::end,
		Line::new
	);

	public static final DataType<Line> DATA_TYPE = DataType.of(CODEC, STREAM_CODEC, Line.class);

	public double dx() {
		return end.x() - start.x();
	}

	public double dy() {
		return end.y() - start.y();
	}

	public double dz() {
		return end.z() - start.z();
	}

	@Nullable
	public BlockHitResult hitBlock(Player player, ClipContext.Fluid fluids) {
		var result = player.level().clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, fluids, player));
		return result.getType() == HitResult.Type.BLOCK ? result : null;
	}
}
