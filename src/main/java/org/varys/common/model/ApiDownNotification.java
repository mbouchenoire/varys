package org.varys.common.model;

import org.varys.common.RestApi;

import java.util.Objects;

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
    public NotificationType getType() {
        return NotificationType.WARNING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiDownNotification that = (ApiDownNotification) o;
        return Objects.equals(api.getDomainName(), that.api.getDomainName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(api.getDomainName());
    }
}
