package dev.latvian.mods.klib.platform;

import dev.latvian.mods.klib.util.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class NeoPlatformHelper extends PlatformHelper {
	public final ModContainer mod;

	public NeoPlatformHelper(ModContainer mod) {
		this.mod = mod;
	}

	@Override
	public PlatformType getPlatform() {
		return PlatformType.NEOFORGE;
	}

	@Override
	public Side getSide() {
		return FMLLoader.getDist().isClient() ? Side.CLIENT : Side.SERVER;
	}

	@Override
	public boolean isDevEnv() {
		return !FMLLoader.isProduction();
	}

	@Override
	public Path getGameDirectory() {
		return FMLPaths.GAMEDIR.get();
	}

	@Override
	public Path getConfigDirectory() {
		return FMLPaths.CONFIGDIR.get();
	}

	@Override
	public Path getModsDirectory() {
		return FMLPaths.MODSDIR.get();
	}

	@Override
	public PlatformType getPlatformOf(Player player) {
		return PlatformType.NEOFORGE; // FIXME
	}

	@Override
	public PlatformType getPlatformOf(RegistryFriendlyByteBuf buf) {
		return buf.getConnectionType() == ConnectionType.NEOFORGE ? PlatformType.NEOFORGE : PlatformType.OTHER;
	}

	@Override
	public RegistryFriendlyByteBuf createBuffer(ByteBuf source, RegistryAccess access, PlatformType platformType) {
		return new RegistryFriendlyByteBuf(source, access, platformType == PlatformType.NEOFORGE ? ConnectionType.NEOFORGE : ConnectionType.OTHER);
	}

	@Override
	public RegistryFriendlyByteBuf createBuffer(ByteBuf source, RegistryFriendlyByteBuf parent) {
		return new RegistryFriendlyByteBuf(source, parent.registryAccess(), parent.getConnectionType());
	}

	@Override
	public Function<ByteBuf, RegistryFriendlyByteBuf> createDecorator(RegistryAccess access) {
		return RegistryFriendlyByteBuf.decorator(access, ConnectionType.NEOFORGE);
	}

	@Override
	@Nullable
	public Path findFile(String... path) {
		for (var file : ModList.get().getModFiles()) {
			try {
				var p = file.getFile().findResource(String.join("/", path));

				if (p != null && Files.exists(p)) {
					return p;
				}
			} catch (Exception ignored) {
			}
		}

		return null;
	}

	@Override
	public List<PlatformModInfo> getModList() {
		var list = new ArrayList<PlatformModInfo>();

		for (var mod : ModList.get().getMods()) {
			list.add(new PlatformModInfo(mod.getModId(), mod.getDisplayName(), mod.getVersion().toString(), mod.getOwningFile().getFile().getFileName()));
		}

		return list;
	}

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}
}
