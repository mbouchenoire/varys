package org.varys.common.model;

public class StartupNotification implements Notification {

    @Override
    public String getTitle() {
        return "Varys is up and running!";
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
