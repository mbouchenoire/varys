package org.varys.common.service;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public final class OkHttpClientFactory {

    private OkHttpClientFactory() {
        super();
    }

    private static Response loggingInterceptor(Interceptor.Chain chain) throws IOException {
        final Request request = chain.request();
        Log.trace(request.toString());
        final Response response = chain.proceed(chain.request());

        if (response.isSuccessful()) {
            Log.trace(response.toString());
        } else {
            Log.error(response.toString());
        }
        return response;
    }

    private static Response utf8EncodingInterceptor(Interceptor.Chain chain) throws IOException {
        return chain.proceed(chain.request())
                .newBuilder()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .build();
    }

    public static OkHttpClient create() {

        try {

            return new OkHttpClient().newBuilder()
                    .sslSocketFactory(SSLUtils.createSocketFactory())
                    .hostnameVerifier((s, sslSession) -> true)
                    .addInterceptor(OkHttpClientFactory::loggingInterceptor)
                    .addInterceptor(OkHttpClientFactory::utf8EncodingInterceptor)
                    .build();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
