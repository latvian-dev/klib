package dev.latvian.mods.klib.entity;

import dev.latvian.mods.klib.net.C2SPacketConsumer;
import dev.latvian.mods.klib.net.S2CPacketConsumer;
import dev.latvian.mods.klib.util.MessageConsumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;
import java.util.function.Predicate;

@FunctionalInterface
public interface PlayerTarget extends S2CPacketConsumer, C2SPacketConsumer, MessageConsumer {
	@FunctionalInterface
	interface Container {
		PlayerTarget klib$getPlayerTarget();
	}

	PlayerTarget NONE = _ -> {
	};

	record OfServer(MinecraftServer server) implements PlayerTarget {
		@Override
		public void forEachPlayer(Consumer<Player> callback) {
			server.getPlayerList().getPlayers().forEach(callback);
		}
	}

	record OfLevel(Level level) implements PlayerTarget {
		@Override
		public void forEachPlayer(Consumer<Player> callback) {
			level.players().forEach(callback);
		}
	}

	record OfPlayer(Player player) implements PlayerTarget {
		@Override
		public void forEachPlayer(Consumer<Player> callback) {
			callback.accept(player);
		}
	}

	static PlayerTarget of(MinecraftServer server) {
		if (server instanceof Container c) {
			return c.klib$getPlayerTarget();
		} else {
			return new OfServer(server);
		}
	}

	static PlayerTarget of(Level level) {
		if (level instanceof Container c) {
			return c.klib$getPlayerTarget();
		} else {
			return new OfLevel(level);
		}
	}

	static PlayerTarget of(Player player) {
		if (player instanceof Container c) {
			return c.klib$getPlayerTarget();
		} else {
			return new OfPlayer(player);
		}
	}

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
		return callback -> forEachPlayer(player -> {
			if (filter.test(player)) {
				callback.accept(player);
			}
		});
	}
}
