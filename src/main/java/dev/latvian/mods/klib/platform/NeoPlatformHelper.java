package dev.latvian.mods.klib.platform;

import dev.latvian.mods.klib.command.CustomRegistryRegistryEvent;
import dev.latvian.mods.klib.data.DataType;
import dev.latvian.mods.klib.data.DataTypeCommandInfoRegistry;
import dev.latvian.mods.klib.data.DataTypeCommandInfoRegistryEvent;
import dev.latvian.mods.klib.data.DataTypeRegistryEvent;
import dev.latvian.mods.klib.gradient.Gradient;
import dev.latvian.mods.klib.gradient.GradientTypeRegistryEvent;
import dev.latvian.mods.klib.interpolation.Interpolation;
import dev.latvian.mods.klib.interpolation.InterpolationTypeRegistryEvent;
import dev.latvian.mods.klib.registry.CustomRegistryCollector;
import dev.latvian.mods.klib.registry.CustomRegistryTypeCollector;
import dev.latvian.mods.klib.shape.Shape;
import dev.latvian.mods.klib.shape.ShapeTypeRegistryEvent;
import dev.latvian.mods.klib.util.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
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
	public PlatformType getPlatform() {
		return PlatformType.NEOFORGE;
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

	@Override
	public void collectCustomRegistries(CustomRegistryCollector registry) {
		super.collectCustomRegistries(registry);
		ModLoader.postEvent(new CustomRegistryRegistryEvent(registry));
	}

	@Override
	public void collectDataTypes(CustomRegistryTypeCollector<ByteBuf, DataType<?>> registry) {
		super.collectDataTypes(registry);
		ModLoader.postEvent(new DataTypeRegistryEvent(registry));
	}

	@Override
	public void collectDataTypeCommandInfos(DataTypeCommandInfoRegistry registry) {
		super.collectDataTypeCommandInfos(registry);
		ModLoader.postEvent(new DataTypeCommandInfoRegistryEvent(registry));
	}

	@Override
	public void collectInterpolationTypes(CustomRegistryTypeCollector<ByteBuf, Interpolation> registry) {
		super.collectInterpolationTypes(registry);
		ModLoader.postEvent(new InterpolationTypeRegistryEvent(registry));
	}

	@Override
	public void collectShapeTypes(CustomRegistryTypeCollector<ByteBuf, Shape> registry) {
		super.collectShapeTypes(registry);
		ModLoader.postEvent(new ShapeTypeRegistryEvent(registry));
	}

	@Override
	public void collectGradientTypes(CustomRegistryTypeCollector<ByteBuf, Gradient> registry) {
		super.collectGradientTypes(registry);
		ModLoader.postEvent(new GradientTypeRegistryEvent(registry));
	}
}
