package dev.latvian.mods.klib.platform;

import dev.latvian.mods.klib.util.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.connection.ConnectionType;
import org.jetbrains.annotations.Nullable;

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
	public String getPlatform() {
		return "neoforge";
	}

	@Override
	public Side getSide() {
		return FMLLoader.getCurrent().getDist().isClient() ? Side.CLIENT : Side.SERVER;
	}

	@Override
	public boolean isDevEnv() {
		return !FMLLoader.getCurrent().isProduction();
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
	public RegistryFriendlyByteBuf createBuffer(ByteBuf source, RegistryAccess access) {
		return new RegistryFriendlyByteBuf(source, access, ConnectionType.NEOFORGE);
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
			var uri = file.getFile().getContents().findFile(String.join("/", path)).orElse(null);

			if (uri != null) {
				try {
					return Path.of(uri);
				} catch (Exception ignored) {
				}
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
