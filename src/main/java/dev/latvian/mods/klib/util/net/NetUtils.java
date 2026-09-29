package dev.latvian.mods.klib.util.net;

import dev.latvian.mods.klib.KLib;
import dev.latvian.mods.klib.io.CompressionMethod;
import dev.latvian.mods.klib.util.Async;
import dev.latvian.mods.klib.util.Lazy;
import net.minecraft.util.FastBufferedInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;

public interface NetUtils {
	DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(ZoneId.of("GMT"));

	Semaphore HTTP_SEMAPHORE = new Semaphore(100);

	Executor EXECUTOR = command -> Async.EXECUTOR.execute(() -> {
		try {
			HTTP_SEMAPHORE.acquire();

			try {
				command.run();
			} finally {
				HTTP_SEMAPHORE.release();
			}
		} catch (InterruptedException ex) {
			KLib.LOGGER.error("HTTP call interrupted", ex);
		}
	});

	HttpClient CLIENT = HttpClient.newBuilder()
		.version(HttpClient.Version.HTTP_1_1)
		.executor(EXECUTOR)
		.followRedirects(HttpClient.Redirect.ALWAYS)
		.connectTimeout(Duration.ofSeconds(30L))
		.build();

	Lazy<HttpRequest.Builder> HTTP_REQUEST_BASE = Lazy.of(() -> HttpRequest.newBuilder()
		.header("Accept-Language", "en-US,en;q=0.5")
		.header("User-Agent", "KLib/" + KLib.VERSION)
		.header("Accept-Encoding", "zstd, gzip, deflate, br")
	);

	static HttpRequest.Builder newRequest() {
		return HTTP_REQUEST_BASE.get().copy();
	}

	static InputStream decode(HttpResponse<InputStream> response) throws IOException {
		var encoding = response.headers().firstValue("Content-Encoding").orElse("");
		return CompressionMethod.of(encoding).in(new FastBufferedInputStream(response.body()));
	}

	static HttpResponseData send(HttpRequest request, boolean responseBody) throws IOException, InterruptedException {
		if (responseBody) {
			var response = send(request, HttpResponse.BodyHandlers.ofInputStream());

			try (var in = decode(response)) {
				return new HttpResponseData(response, response.statusCode(), in.readAllBytes());
			}
		} else {
			var response = send(request, HttpResponse.BodyHandlers.discarding());
			return new HttpResponseData(response, response.statusCode(), HttpResponseData.NO_DATA);
		}
	}

	static <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> bodyHandler) throws IOException, InterruptedException {
		HttpResponse<T> response;

		HTTP_SEMAPHORE.acquire();

		try {
			response = CLIENT.send(request, bodyHandler);
		} finally {
			HTTP_SEMAPHORE.release();
		}

		int retries = 0;

		while (retries < 5 && response.statusCode() != 500 && response.headers().firstValue("Retry-After").orElse(response.statusCode() / 100 == 5 ? "10" : null) instanceof String h) {
			try {
				long seconds = Long.parseLong(h);

				if (seconds > 0L) {
					Thread.sleep(seconds * 1000L);
				}
			} catch (Exception ignored) {
				try {
					var duration = Duration.between(Instant.now(), Instant.from(DATE_TIME_FORMATTER.parse(h)));

					if (duration.isPositive()) {
						Thread.sleep(duration.toMillis());
					}
				} catch (Exception ignored2) {
				}
			}

			HTTP_SEMAPHORE.acquire();

			try {
				response = CLIENT.send(request, bodyHandler);
			} finally {
				HTTP_SEMAPHORE.release();
			}

			retries++;
		}

		return response;
	}
}
