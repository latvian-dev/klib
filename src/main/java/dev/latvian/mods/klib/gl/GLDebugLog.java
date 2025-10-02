package dev.latvian.mods.klib.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricCategory;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.lwjgl.opengl.GL43;

import java.util.function.Supplier;

public interface GLDebugLog {
	enum Type {
		ERROR(GL43.GL_DEBUG_TYPE_ERROR),
		DEPRECATED_BEHAVIOR(GL43.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR),
		UNDEFINED_BEHAVIOR(GL43.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR),
		PORTABILITY(GL43.GL_DEBUG_TYPE_PORTABILITY),
		PERFORMANCE(GL43.GL_DEBUG_TYPE_PERFORMANCE),
		OTHER(GL43.GL_DEBUG_TYPE_OTHER),
		MARKER(GL43.GL_DEBUG_TYPE_MARKER);

		public final int id;

		Type(int id) {
			this.id = id;
		}
	}

	enum Severity {
		NOTIFICATION(GL43.GL_DEBUG_SEVERITY_NOTIFICATION),
		HIGH(GL43.GL_DEBUG_SEVERITY_HIGH),
		MEDIUM(GL43.GL_DEBUG_SEVERITY_MEDIUM),
		LOW(GL43.GL_DEBUG_SEVERITY_LOW);

		public final int id;

		Severity(int id) {
			this.id = id;
		}
	}

	// MAJOR_VERSION >= 4 || (GL11.glGetInteger(GL30.GL_MAJOR_VERSION) > 4 || GL11.glGetInteger(GL30.GL_MINOR_VERSION) >= 3);
	MutableBoolean ENABLED = new MutableBoolean(true);

	ProfilerFiller PROFILER = new ProfilerFiller() {
		@Override
		public void startTick() {
		}

		@Override
		public void endTick() {
		}

		@Override
		public void push(String name) {
			if (RenderSystem.isOnRenderThread()) {
				pushGroup(name);
			}
		}

		@Override
		public void push(Supplier<String> nameSupplier) {
			if (RenderSystem.isOnRenderThread()) {
				pushGroup(nameSupplier.get());
			}
		}

		@Override
		public void pop() {
			if (RenderSystem.isOnRenderThread()) {
				popGroup();
			}
		}

		@Override
		public void popPush(String name) {
			if (RenderSystem.isOnRenderThread()) {
				popGroup();
				pushGroup(name);
			}
		}

		@Override
		public void popPush(Supplier<String> nameSupplier) {
			if (RenderSystem.isOnRenderThread()) {
				popGroup();
				pushGroup(nameSupplier.get());
			}
		}

		@Override
		public void markForCharting(MetricCategory category) {
		}

		@Override
		public void incrementCounter(String counterName, int increment) {
		}

		@Override
		public void incrementCounter(Supplier<String> counterNameSupplier, int increment) {
		}
	};

	static boolean isEnabled() {
		return !Minecraft.ON_OSX && ENABLED.getValue();
	}

	static void message(Object message, Type type, Severity severity) {
		if (isEnabled()) {
			GL43.glDebugMessageInsert(GL43.GL_DEBUG_SOURCE_APPLICATION, type.id, 0, severity.id, String.valueOf(message));
		}
	}

	static void message(Object message, Severity severity) {
		message(message, Type.MARKER, severity);
	}

	static void message(Object message) {
		message(message, Type.MARKER, Severity.NOTIFICATION);
	}

	static void pushGroup(Object name) {
		if (isEnabled()) {
			GL43.glPushDebugGroup(GL43.GL_DEBUG_SOURCE_APPLICATION, 0, String.valueOf(name));
		}
	}

	static void popGroup() {
		if (isEnabled()) {
			GL43.glPopDebugGroup();
		}
	}
}
