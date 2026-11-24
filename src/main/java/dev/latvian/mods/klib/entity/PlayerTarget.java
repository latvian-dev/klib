package dev.latvian.mods.klib.entity;

import dev.latvian.mods.klib.core.KLibExtensions;
import dev.latvian.mods.klib.net.C2SPacketConsumer;
import dev.latvian.mods.klib.net.S2CPacketConsumer;
import dev.latvian.mods.klib.platform.PlatformHelper;
import dev.latvian.mods.klib.util.MessageConsumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface PlayerTarget extends S2CPacketConsumer, C2SPacketConsumer, MessageConsumer {
	record Filtered(PlayerTarget parent, Predicate<Player> filter) implements PlayerTarget {
		@Override
		public Level getCommonLevel() {
			return parent.getCommonLevel();
		}

		@Override
		public void forEachPlayer(Consumer<Player> callback) {
			parent.forEachPlayer(player -> {
				if (filter.test(player)) {
					callback.accept(player);
				}
			});
		}
	}

	record OfServer(MinecraftServer server) implements PlayerTarget {
		public static final Function<MinecraftServer, OfServer> FACTORY = OfServer::new;

		@Override
		public Level getCommonLevel() {
			return server.overworld();
		}

		@Override
		public void forEachPlayer(Consumer<Player> callback) {
			server.getPlayerList().getPlayers().forEach(callback);
		}

		@Override
		public void tell(Component message) {
			for (var player : server.getPlayerList().getPlayers()) {
				player.sendSystemMessage(message);
			}
		}

		@Override
		public void status(Component message) {
			for (var player : server.getPlayerList().getPlayers()) {
				player.sendOverlayMessage(message);
			}
		}
	}

	record OfLevel(Level level) implements PlayerTarget {
		public static final Function<Level, OfLevel> FACTORY = OfLevel::new;

		@Override
		public Level getCommonLevel() {
			return level;
		}

		@Override
		public void forEachPlayer(Consumer<Player> callback) {
			level.players().forEach(callback);
		}

		@Override
		public void tell(Component message) {
			for (var player : level.players()) {
				player.sendSystemMessage(message);
			}
		}

		@Override
		public void status(Component message) {
			for (var player : level.players()) {
				player.sendOverlayMessage(message);
			}
		}
	}

	record OfPlayer(Player player) implements PlayerTarget {
		public static final Function<Player, OfPlayer> FACTORY = OfPlayer::new;

		@Override
		public Level getCommonLevel() {
			return player.level();
		}

		@Override
		public void forEachPlayer(Consumer<Player> callback) {
			callback.accept(player);
		}

		@Override
		public void tell(Component message) {
			player.sendSystemMessage(message);
		}

		@Override
		public void status(Component message) {
			player.sendOverlayMessage(message);
		}
	}

	record OfCommandSourceStack(CommandSourceStack commandSourceStack) implements PlayerTarget {
		public static final Function<CommandSourceStack, OfCommandSourceStack> FACTORY = OfCommandSourceStack::new;

		@Override
		public Level getCommonLevel() {
			return PlatformHelper.INSTANCE.getLevel(commandSourceStack);
		}

		@Override
		public void forEachPlayer(Consumer<Player> callback) {
			if (commandSourceStack.getEntity() instanceof Player player) {
				callback.accept(player);
			}
		}

		@Override
		public void tell(Component message) {
			commandSourceStack.sendSuccess(() -> message, false);
		}

		@Override
		public void status(Component message) {
			if (commandSourceStack.getEntity() instanceof Player player) {
				player.sendOverlayMessage(message);
			} else {
				tell(message);
			}
		}

		@Override
		public void error(Component message) {
			commandSourceStack.sendFailure(message);
		}

		@Override
		public void broadcast(Component message) {
			commandSourceStack.sendSuccess(() -> message, true);
		}
	}

	static OfServer of(MinecraftServer server) {
		return KLibExtensions.compute(Objects.requireNonNull(server, "MinecraftServer is null"), 492, OfServer.FACTORY);
	}

	static OfLevel of(Level level) {
		return KLibExtensions.compute(Objects.requireNonNull(level, "Level is null"), 492, OfLevel.FACTORY);
	}

	static OfPlayer of(Player player) {
		return KLibExtensions.compute(Objects.requireNonNull(player, "Player is null"), 492, OfPlayer.FACTORY);
	}

	static OfCommandSourceStack of(CommandSourceStack commandSourceStack) {
		return KLibExtensions.compute(Objects.requireNonNull(commandSourceStack, "CommandSourceStack is null"), 492, OfCommandSourceStack.FACTORY);
	}

	Level getCommonLevel();

	void forEachPlayer(Consumer<Player> callback);

	@Override
	default void s2c(Packet<? super ClientGamePacketListener> packet) {
		forEachPlayer(player -> {
			if (player instanceof ServerPlayer serverPlayer) {
				serverPlayer.connection.send(packet);
			} else {
				throw new IllegalStateException("Can't call PlayerTarget#s2c on a non-ServerPlayer " + player);
			}
		});
	}

	@Override
	default void c2s(Packet<? super ServerGamePacketListener> packet) {
		forEachPlayer(player -> {
			if (player instanceof C2SPacketConsumer c) {
				c.c2s(packet);
			} else {
				throw new IllegalStateException("Can't call PlayerTarget#s2c on a non-LocalPlayer " + player);
			}
		});
	}

	@Override
	default void tell(Component message) {
		forEachPlayer(player -> player.sendSystemMessage(message));
	}

	@Override
	default void status(Component message) {
		forEachPlayer(player -> player.sendOverlayMessage(message));
	}

	default PlayerTarget filter(Predicate<Player> filter) {
		return new Filtered(this, filter);
	}
}
