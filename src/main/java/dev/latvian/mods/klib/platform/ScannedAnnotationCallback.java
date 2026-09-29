package dev.latvian.mods.klib.platform;

public interface ScannedAnnotationCallback {
	void accept(String source, ClassLoader classLoader, ScannedAnnotation ad) throws Exception;
}
