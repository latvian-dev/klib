package dev.latvian.mods.klib.core;

public class NoMixinException extends IllegalStateException {
	public NoMixinException(Object thisObject) {
		super("A mixin should have implemented this method! Missing in " + thisObject.getClass().getName());
	}
}
