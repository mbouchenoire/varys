package org.varys.common.service;

import org.varys.common.RestApi;
import org.varys.common.model.ApiBackOnlineNotification;
import org.varys.common.model.ApiDownNotification;

import java.io.File;

public final class RestApiService {

    private final CacheService cacheService;
    private final NotificationService notificationService;

    public RestApiService(CacheService cacheService, NotificationService notificationService) {
        this.cacheService = cacheService;
        this.notificationService = notificationService;
    }

    public boolean notifyApiStatus(RestApi api) {
        final boolean apiIsOnline = api.isOnline();
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
