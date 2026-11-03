package dev.latvian.mods.klib.volume;

import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.latvian.mods.klib.codec.CompositeStreamCodec;
import dev.latvian.mods.klib.codec.MCCodecs;
import dev.latvian.mods.klib.codec.MCStreamCodecs;
import dev.latvian.mods.klib.registry.DynamicType;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.stream.Stream;

public record BoxVolume(AABB box) implements Volume {
	public static final DynamicType<ByteBuf, Volume> TYPE = DynamicType.create(
		"box",
		RecordCodecBuilder.mapCodec(instance -> instance.group(
			MCCodecs.VEC3.fieldOf("start").forGetter(z -> z.box.getMinPosition()),
			MCCodecs.VEC3.fieldOf("end").forGetter(z -> z.box.getMaxPosition())
		).apply(instance, BoxVolume::new)),
		CompositeStreamCodec.of(
			MCStreamCodecs.AABB, BoxVolume::box,
			BoxVolume::new
		)
	);

	public BoxVolume(Vec3 start, Vec3 end) {
		this(new AABB(start, end));
	}

	@Override
	public DynamicType<ByteBuf, Volume> type() {
		return TYPE;
	}

	@Override
	public Stream<BlockPos> getBlocks() {
		return BlockPos.betweenClosedStream(box);
	}
}
