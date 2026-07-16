package dev.latvian.mods.klib.core;

import dev.latvian.mods.klib.color.Color;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;

import java.net.URI;
import java.nio.file.Path;

public interface KLibStyle {
	default Style klib$self() {
		return (Style) this;
	}

	default Style withHoverText(Component component) {
		return klib$self().withHoverEvent(new HoverEvent.ShowText(component));
	}

	default Style withHoverText(String text) {
		return withHoverText(Component.literal(text));
	}

	default Style withClickToRunCommand(String string) {
		return klib$self().withClickEvent(new ClickEvent.RunCommand(string));
	}

	default Style withClickToSuggestCommand(String string) {
		return klib$self().withClickEvent(new ClickEvent.SuggestCommand(string));
	}

	default Style withClickToCopyToClipboard(String string) {
		return klib$self().withClickEvent(new ClickEvent.CopyToClipboard(string));
	}

	default Style withCopyString(String string) {
		return withClickToCopyToClipboard(string).withHoverText("Click to Copy");
	}

	default Style withClickToOpen(Path path) {
		return klib$self().withClickEvent(new ClickEvent.OpenFile(path));
	}

	default Style withClickToOpen(URI uri) {
		return klib$self().withClickEvent(new ClickEvent.OpenUrl(uri));
	}

	default Style withColor(Color color) {
		return klib$self().withColor(color.rgb());
	}
}
