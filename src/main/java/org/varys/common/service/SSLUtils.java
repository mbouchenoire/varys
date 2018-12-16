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

import org.pmw.tinylog.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

final class SSLUtils {

    private SSLUtils() {
        super();
    }

    static final X509TrustManager TRUST_ALL_CERTS = new X509TrustManager() {
                private boolean clientTrustWarned = false;
                private boolean serverTrustWarned = false;

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    if (!clientTrustWarned) {
                        // if the queried APIs certificates are not up to date, we don't check them
                        Logger.warn("Unsecure client trust check (auth type: {})", authType);
                        clientTrustWarned = true;
                    }
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    if (!serverTrustWarned) {
                        // if the queried APIs certificates are not up to date, we don't check them
                        Logger.warn("Unsecure server trust check (auth type: {})", authType);
                        serverTrustWarned = true;
                    }
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }

            };

    static SSLSocketFactory createUnsecuredSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, new X509TrustManager[]{SSLUtils.TRUST_ALL_CERTS}, new SecureRandom());
        return sslContext.getSocketFactory();
    }
}
