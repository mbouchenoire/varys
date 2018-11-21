package org.varys.common.model;

import org.varys.common.RestApi;

public final class ApiDownNotification implements Notification {

    private final RestApi api;

    public ApiDownNotification(RestApi api) {
        this.api = api;
    }

    @Override
    public String getTitle() {
        return this.api.getLabel() + " is down!";
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public NotificationType getType() {
        return NotificationType.WARNING;
    }
}
