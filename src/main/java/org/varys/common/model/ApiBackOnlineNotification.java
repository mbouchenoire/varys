package org.varys.common.model;

import org.varys.common.RestApi;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiBackOnlineNotification that = (ApiBackOnlineNotification) o;
        return Objects.equals(api.getDomainName(), that.api.getDomainName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(api.getDomainName());
    }
}
