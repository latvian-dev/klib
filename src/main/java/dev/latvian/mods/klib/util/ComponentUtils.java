package dev.latvian.mods.klib.util;

import dev.latvian.mods.klib.color.Color;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.net.URI;
import java.nio.file.Path;

public interface ComponentUtils {
	// Style

	static Style withHoverText(Style style, Component text) {
		return style.withHoverEvent(new HoverEvent.ShowText(text));
	}

	static Style withHoverText(Style style, String text) {
		return withHoverText(style, Component.literal(text));
	}

	static Style withClickToRunCommand(Style style, String command) {
		return style.withClickEvent(new ClickEvent.RunCommand(command));
	}

	static Style withClickToSuggestCommand(Style style, String command) {
		return style.withClickEvent(new ClickEvent.SuggestCommand(command));
	}

	static Style withClickToCopyToClipboard(Style style, String text) {
		return style.withClickEvent(new ClickEvent.CopyToClipboard(text));
	}

	static Style withCopyString(Style style, String text) {
		return withHoverText(withClickToCopyToClipboard(style, text), "Click to Copy");
	}

	static Style withClickToOpen(Style style, Path path) {
		return style.withClickEvent(new ClickEvent.OpenFile(path));
	}

	static Style withClickToOpen(Style style, URI uri) {
		return style.withClickEvent(new ClickEvent.OpenUrl(uri));
	}

	static Style withColor(Style style, Color color) {
		return style.withColor(color.rgb());
	}

	// Component

	static MutableComponent withHoverText(MutableComponent component, Component text) {
		return component.withStyle(withHoverText(component.getStyle(), text));
	}

	static MutableComponent withHoverText(MutableComponent component, String text) {
		return component.withStyle(withHoverText(component.getStyle(), text));
	}

	static MutableComponent withClickToRunCommand(MutableComponent component, String command) {
		return component.withStyle(withClickToRunCommand(component.getStyle(), command));
	}

	static MutableComponent withClickToSuggestCommand(MutableComponent component, String command) {
		return component.withStyle(withClickToSuggestCommand(component.getStyle(), command));
	}

	static MutableComponent withClickToCopyToClipboard(MutableComponent component, String text) {
		return component.withStyle(withClickToCopyToClipboard(component.getStyle(), text));
	}

	static MutableComponent withCopyString(MutableComponent component, String text) {
		return component.withStyle(withCopyString(component.getStyle(), text));
	}

	static MutableComponent withClickToOpen(MutableComponent component, Path path) {
		return component.withStyle(withClickToOpen(component.getStyle(), path));
	}

	static MutableComponent withClickToOpen(MutableComponent component, URI uri) {
		return component.withStyle(withClickToOpen(component.getStyle(), uri));
	}

	static MutableComponent withColor(MutableComponent component, Color color) {
		return component.withStyle(withColor(component.getStyle(), color));
	}
}
