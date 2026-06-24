package dev.latvian.mods.klib.platform;

import dev.latvian.mods.klib.util.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class PlatformHelper {
	public static PlatformHelper CURRENT = new PlatformHelper();

	public String getPlatform() {
		return "bukkit";
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

	public RegistryFriendlyByteBuf createBuffer(ByteBuf source, RegistryAccess access) {
		return new RegistryFriendlyByteBuf(source, access);
	}

	public RegistryFriendlyByteBuf createBuffer(ByteBuf source, RegistryFriendlyByteBuf parent) {
		return new RegistryFriendlyByteBuf(source, parent.registryAccess());
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
}
