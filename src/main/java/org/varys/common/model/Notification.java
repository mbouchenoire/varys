package org.varys.common.model;

public interface Notification {

    String getTitle();
    String getDescription();
    NotificationType getType();
}
