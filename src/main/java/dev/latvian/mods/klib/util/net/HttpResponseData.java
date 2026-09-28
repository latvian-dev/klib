package dev.latvian.mods.klib.util.net;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.klib.util.JsonUtils;

import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class HttpResponseData {
	public static final byte[] NO_DATA = new byte[0];

	public final HttpResponse<?> response;
	public final int code;
	public final byte[] data;

	public HttpResponseData(HttpResponse<?> response, int code, byte[] data) {
		this.response = response;
		this.code = code;
		this.data = data;
	}

	public HttpResponseData(HttpResponseData parent) {
		this.response = parent.response;
		this.code = parent.code;
		this.data = parent.data;
	}

	public boolean isOk() {
		return code / 100 == 2;
	}

	public String string() {
		return new String(data, StandardCharsets.UTF_8);
	}

	public JsonElement json() {
		if (code / 100 == 2) {
			return JsonUtils.parse(string());
		}

		var error = "HTTP Error " + code;

		if (code == 500 || code / 100 == 4) {
			error += ": " + string();
		}

		throw new IllegalStateException(error);
	}

	public <T> T json(Codec<T> codec) {
		return codec.parse(JsonOps.INSTANCE, json()).getOrThrow();
	}

	@Override
	public String toString() {
		return "MCHTTPResponse[" + code + ']';
	}
}