package org.varys.common.model;

import org.varys.common.RestApi;

public final class ApiBackOnlineNotification implements Notification {

    private final RestApi api;

    public ApiBackOnlineNotification(RestApi api) {
        this.api = api;
    }

    @Override
    public String getTitle() {
        return this.api.getLabel() + " is back online!";
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
