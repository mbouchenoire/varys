/*
 * This file is part of Varys.
 *
 * Foobar is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Varys.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.varys.common.service;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.pmw.tinylog.Logger;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public final class OkHttpClientFactory {

    private OkHttpClientFactory() {
        super();
    }

    private static Response loggingInterceptor(Interceptor.Chain chain) throws IOException {
        final Request request = chain.request();
        Logger.trace(request.toString());
        final Response response = chain.proceed(chain.request());

        if (response.isSuccessful()) {
            Logger.trace(response.toString());
        } else {
            Logger.error(response.toString());
        }
        return response;
    }

    public static OkHttpClient create(boolean sslVerify) {
        try {
            final OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(OkHttpClientFactory::loggingInterceptor);

            if (!sslVerify) {
                okHttpClientBuilder
                        .sslSocketFactory(SSLUtils.createUnsecuredSocketFactory(), SSLUtils.TRUST_ALL_CERTS)
                        .hostnameVerifier((s, sslSession) -> true);
            }

            return okHttpClientBuilder.build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            Logger.error(e, "Failed to findUsable unsecured HTTP client, using default one instead");

            return new OkHttpClient().newBuilder()
                    .addInterceptor(OkHttpClientFactory::loggingInterceptor)
                    .build();
        }
    }
}
