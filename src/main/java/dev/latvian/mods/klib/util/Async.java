package dev.latvian.mods.klib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Stream;

public interface Async {
	Executor EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

	static CompletableFuture<Void> run(Runnable runnable) {
		return CompletableFuture.runAsync(runnable, EXECUTOR);
	}

	static <T> CompletableFuture<T> supply(Supplier<T> supplier) {
		return CompletableFuture.supplyAsync(supplier, EXECUTOR);
	}

	static <T> void waitFor(Collection<CompletableFuture<T>> futures) {
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
	}

	static <T> Stream<T> waitForResultStream(Collection<CompletableFuture<T>> futures) {
		waitFor(futures);
		return futures.stream().map(CompletableFuture::join);
	}

	static <T> List<T> waitForResults(Collection<CompletableFuture<T>> futures) {
		waitFor(futures);

		var result = new ArrayList<T>(futures.size());

		for (var future : futures) {
			result.add(future.join());
		}

		return result;
	}
}
