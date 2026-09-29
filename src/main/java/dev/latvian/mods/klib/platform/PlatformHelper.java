package dev.latvian.mods.klib.platform;

import dev.latvian.mods.klib.util.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class PlatformHelper {
	public static PlatformHelper CURRENT = new PlatformHelper();

	public PlatformType getPlatform() {
		return PlatformType.OTHER;
	}

	public Side getSide() {
		return Side.SERVER;
	}

	public boolean isDevEnv() {
		return false;
	}

	public Path getGameDirectory() {
		return Path.of("");
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
		return PlatformType.OTHER; // FIXME
	}

	public PlatformType getPlatformOf(RegistryFriendlyByteBuf buf) {
		return PlatformType.OTHER;
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

	public void load(Class<? extends Annotation> annotation, Set<ElementType> elementTypes, ScannedAnnotationCallback callback) {
	}

	@Nullable
	public Path findFile(String... path) {
		throw new UnsupportedOperationException("Not supported on " + getPlatform().getSerializedName());
	}

	@Nullable
	public Path findFile(PackType type, ResourceLocation id) {
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
		for (var mod : getModList()) {
			if (mod.id().equals(modId)) {
				return true;
			}
		}

		return false;
	}

	public Level getLevel(CommandSourceStack source) {
		return source.getLevel();
	}

	public boolean isLocalServer(Level level) {
		return level instanceof ServerLevel serverLevel && serverLevel.getServer().isSingleplayer();
	}

	public Stream<LevelChunk> getChunks(Level level) {
		if (level instanceof ServerLevel serverLevel) {
			return StreamSupport.stream(serverLevel.getChunkSource().chunkMap.getChunks().spliterator(), false).map(ChunkHolder::getTickingChunk).filter(c -> c != null && !c.isEmpty());
		}

		return Stream.empty();
	}
}
