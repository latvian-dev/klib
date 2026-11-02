package dev.latvian.mods.klib.render;

@FunctionalInterface
public interface Submittable {
	void submit(SubmitContext ctx);
}
