package dev.latvian.mods.kmath.codec;

import com.mojang.serialization.Codec;
import dev.latvian.mods.kmath.KMath;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface KMathCodecs {
	Codec<Vec3> VEC3 = Codec.DOUBLE.listOf(3, 3).xmap(l -> KMath.vec3(l.get(0), l.get(1), l.get(2)), v -> List.of(v.x, v.y, v.z));
}
