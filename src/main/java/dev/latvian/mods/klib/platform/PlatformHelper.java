package dev.latvian.mods.klib.platform;

import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.data.DataTypeCommandInfoRegistry;
import dev.latvian.mods.klib.data.DataTypes;
import dev.latvian.mods.klib.data.JOMLDataTypes;
import dev.latvian.mods.klib.gradient.Gradient;
import dev.latvian.mods.klib.interpolation.Interpolation;
import dev.latvian.mods.klib.registry.CustomRegistry;
import dev.latvian.mods.klib.registry.CustomRegistryCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.shape.Shape;
import dev.latvian.mods.klib.util.Cast;
import dev.latvian.mods.klib.util.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class PlatformHelper {
	public static PlatformHelper CURRENT = new PlatformHelper();

	public PlatformType getPlatform() {
		return PlatformType.BUKKIT;
	}

	public Side getSide() {
		return Side.SERVER;
	}

	public boolean isDevEnv() {
		return false;
	}

	public Path getGameDirectory() {
		return Path.of(".");
	}

	public Path getConfigDirectory() {
		return getGameDirectory().resolve("config");
	}

	public Path getModsDirectory() {
		return getGameDirectory().resolve("mods");
	}

	public Path getLocalDirectory() {
		return getGameDirectory().resolve("local");
	}

	public PlatformType getPlatformOf(Player player) {
		return PlatformType.VANILLA; // FIXME
	}

	public RegistryFriendlyByteBuf createBuffer(ByteBuf source, RegistryAccess access, PlatformType platformType) {
		return new RegistryFriendlyByteBuf(source, access);
	}

	public RegistryFriendlyByteBuf createBuffer(ByteBuf source, RegistryAccess access) {
		return createBuffer(source, access, getPlatform());
	}

	public RegistryFriendlyByteBuf createBuffer(ByteBuf source, RegistryFriendlyByteBuf parent) {
		return createBuffer(source, parent.registryAccess());
	}

	public Function<ByteBuf, RegistryFriendlyByteBuf> createDecorator(RegistryAccess access) {
		return RegistryFriendlyByteBuf.decorator(access);
	}

	@Nullable
	public Path findFile(String... path) {
		throw new UnsupportedOperationException("Not supported on bukkit");
	}

	@Nullable
	public Path findFile(PackType type, Identifier id) {
		var path = id.getPath().split("/");
		var pathParts = new String[path.length + 2];
		pathParts[0] = type.getDirectory();
		pathParts[1] = id.getNamespace();
		System.arraycopy(path, 0, pathParts, 2, path.length);
		return findFile(pathParts);
	}

	public List<PlatformModInfo> getModList() {
		return List.of();
	}

	public boolean isModLoaded(String modId) {
		return false;
	}

	public void collectCustomRegistries(CustomRegistryCollector registry) {
		KLib.builtInRegistries(registry);
	}

	public void collectDataTypes(CustomRegistryTypeCollector<ByteBuf, DataType<?>> registry) {
		for (var reg : CustomRegistry.ALL.values()) {
			registry.register("custom_registry/" + reg.registryId(), reg.dataType());
		}

		DataTypes.register(registry);
		JOMLDataTypes.register(registry);
	}

	private static <T> void register(DataTypeCommandInfoRegistry registry, CustomRegistry<?, T> customRegistry) {
		registry.register(customRegistry.dataType(), customRegistry::createArgument, null);
	}

	public void collectDataTypeCommandInfos(DataTypeCommandInfoRegistry registry) {
		for (var customRegistry : CustomRegistry.ALL.values()) {
			register(registry, Cast.to(customRegistry));
		}

		DataTypes.registerCommandInfos(registry);
	}

	public void collectInterpolationTypes(CustomRegistryTypeCollector<ByteBuf, Interpolation> registry) {
		Interpolation.builtInTypes(registry);
	}

	public void collectShapeTypes(CustomRegistryTypeCollector<ByteBuf, Shape> registry) {
		Shape.builtInTypes(registry);
	}

	public void collectGradientTypes(CustomRegistryTypeCollector<ByteBuf, Gradient> registry) {
		Gradient.builtInTypes(registry);
	}
}
