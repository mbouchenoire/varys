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

package org.varys.common;

import org.apache.http.HttpStatus;
import org.varys.common.model.RestApiStatus;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public interface RestApi {

    String getLabel();
    String getBaseUrl();
    boolean isCompatible();
    Call buildStatusCall();

    /**
     * We check twice for the API status to avoid occasional timeouts.
     *
     * @return true if the API is online, false otherwise
     */
    default boolean isOnline() {
        final Call statusCall = buildStatusCall();

        try {
            statusCall.execute();
            return true;
        } catch (IOException e) {
            try {
                statusCall.execute();
                return true;
            } catch (IOException e2) {
                return e.getMessage().contains("cert");
            }
        }
    }

    default RestApiStatus getStatus() {
        try {
            final Call statusCall = buildStatusCall();
            final Response response = statusCall.execute();

            final boolean online = isOnline();
            final boolean validPrivateToken = response.code() != HttpStatus.SC_UNAUTHORIZED;
            final boolean compatible = isCompatible();

            return new RestApiStatus(online, compatible, true, validPrivateToken);
        } catch (IOException e) {
            if (e.getMessage().contains("cert")) {
                return new RestApiStatus(true, true, false, true);
            } else {
                return new RestApiStatus(false, true, true, true);
            }
        }
    }

    default String getDomainName() {
        try {
            final String baseUrl = getBaseUrl();
            final URI uri = new URI(baseUrl);
            return uri.getHost();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to obtain REST API domain name");
        }
    }
}
