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

import org.varys.common.RestApi;
import org.varys.common.model.ApiBackOnlineNotification;
import org.varys.common.model.ApiDownNotification;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public final class RestApiService {

    private final CacheService cacheService;
    private final NotificationService notificationService;

    public RestApiService(CacheService cacheService, NotificationService notificationService) {
        this.cacheService = cacheService;
        this.notificationService = notificationService;
    }

    public boolean isOffline() {
        try {
            final URL url = new URL("http://google.com");
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return false;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return true;
        }
    }

    public boolean notifyApiStatus(RestApi api) {
        final boolean apiIsOnline = api.getStatus().isOnline();
        final String apiStatusPath = buildApiStatusPath(api);

        return this.cacheService.get(apiStatusPath, Boolean.class)
                .map(cachedApiIsOnline -> {
                    if (apiIsOnline && !cachedApiIsOnline) {
                        this.notificationService.send(new ApiBackOnlineNotification(api));
                    } else if (!apiIsOnline && cachedApiIsOnline) {
                        this.notificationService.send(new ApiDownNotification(api));
                    }

                    this.cacheService.save(apiStatusPath, apiIsOnline);
                    return apiIsOnline;
                })
                .orElseGet(() -> {
                    this.cacheService.save(apiStatusPath, apiIsOnline);
                    return apiIsOnline;
                });
    }

    private static String buildApiStatusPath(RestApi api) {
        return api.getDomainName() + File.separator + "status";
    }
}
