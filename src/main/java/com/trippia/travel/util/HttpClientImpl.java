package com.trippia.travel.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HttpClientImpl implements HttpClient{

    private final OkHttpClient httpClient = new OkHttpClient();

    @Override
    public String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            if (response.body() != null) {
                return response.body().string();
            }
            return "";
        }
    }
}
