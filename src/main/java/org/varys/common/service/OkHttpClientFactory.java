package org.varys.common.service;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
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

    public static OkHttpClient create() {

        try {

            return new OkHttpClient().newBuilder()
                    .sslSocketFactory(SSLUtils.createUnsecuredSocketFactory(), SSLUtils.TRUST_ALL_CERTS)
                    .hostnameVerifier((s, sslSession) -> true)
                    .addInterceptor(OkHttpClientFactory::loggingInterceptor)
                    .build();
        } catch (NoSuchAlgorithmException e) {
            Log.error(e, "Failed to create unsecured HTTP client, using default one instead");

            return new OkHttpClient().newBuilder()
                    .addInterceptor(OkHttpClientFactory::loggingInterceptor)
                    .build();
        }
    }
}
