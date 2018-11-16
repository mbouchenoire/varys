package org.varys.common.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public final class OkHttpClientFactory {

    private OkHttpClientFactory() {
        super();
    }

    public static OkHttpClient create() {

        try {
            //noinspection deprecation, will change later
            return new OkHttpClient().newBuilder()
                    .sslSocketFactory(SSLUtils.createSocketFactory())
                    .hostnameVerifier((s, sslSession) -> true)
                    .addInterceptor(chain -> {
                        final Request request = chain.request();
                        Log.trace(request.toString());
                        final Response response = chain.proceed(chain.request());

                        if (response.isSuccessful()) {
                            Log.trace(response.toString());
                        } else {
                            Log.error(response.toString());
                        }
                        return response;
                    }).build();
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
