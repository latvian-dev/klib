package dev.latvian.mods.klib.entity;

import dev.latvian.mods.klib.net.S2CPacketConsumer;
import dev.latvian.mods.klib.util.MessageConsumer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

@FunctionalInterface
public interface ServerPlayerContainer extends S2CPacketConsumer, MessageConsumer {
	record OfServer(MinecraftServer server) implements ServerPlayerContainer {
		@Override
		public void forEachPlayer(Consumer<ServerPlayer> callback) {
			server.getPlayerList().getPlayers().forEach(callback);
		}
	}

	record OfLevel(ServerLevel level) implements ServerPlayerContainer {
		@Override
		public void forEachPlayer(Consumer<ServerPlayer> callback) {
			level.players().forEach(callback);
		}
	}

	record OfPlayer(ServerPlayer player) implements ServerPlayerContainer {
		@Override
		public void forEachPlayer(Consumer<ServerPlayer> callback) {
			callback.accept(player);
		}
	}

	static ServerPlayerContainer of(MinecraftServer server) {
		if (server instanceof ServerPlayerContainer c) {
			return c;
		} else {
			return new OfServer(server);
		}
	}

	static ServerPlayerContainer of(ServerLevel level) {
		if (level instanceof ServerPlayerContainer c) {
			return c;
		} else {
			return new OfLevel(level);
		}
	}

	static ServerPlayerContainer of(ServerPlayer player) {
		if (player instanceof ServerPlayerContainer c) {
			return c;
		} else {
			return new OfPlayer(player);
		}
	}

	void forEachPlayer(Consumer<ServerPlayer> callback);

	@Override
	default void s2c(Packet<? super ClientGamePacketListener> packet) {
		forEachPlayer(player -> player.connection.send(packet));
	}

	@Override
	default void tell(Component message) {
		forEachPlayer(player -> player.sendSystemMessage(message));
	}

	@Override
	default void status(Component message) {
		forEachPlayer(player -> player.sendOverlayMessage(message));
	}
}
