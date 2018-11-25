package org.varys.common.service;

import org.varys.common.model.Notification;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class NotificationService {

    private final String moduleName;

    public NotificationService(String moduleName) {
        this.moduleName = moduleName;
    }

    public void send(Notification notification) {
        Log.info("[NOTIFICATION][{}] title={}; description={}; type={}",
                this.moduleName,
                notification.getTitle(),
                notification.getDescription(),
                notification.getType());

        VarysTrayIcon.notify(
                notification.getTitle(),
                notification.getDescription().orElse(null),
                notification.getType().getTrayIconMessageType());

        notification.getLinkable().ifPresent(linkable -> VarysTrayIcon.addTemporaryClickableMenu(
                linkable.getLabel(),
                () -> {
                    try {
                        Desktop.getDesktop().browse(new URI(linkable.getUrl()));
                    } catch (IOException | URISyntaxException e) {
                        Log.error(e, "Failed to start browser");
                    }
                }));
    }
}
