package org.varys.common.model;

import java.awt.*;

public enum NotificationType {
    NONE(TrayIcon.MessageType.NONE),
    INFO(TrayIcon.MessageType.INFO),
    WARNING(TrayIcon.MessageType.WARNING),
    ERROR(TrayIcon.MessageType.ERROR);

    private final TrayIcon.MessageType trayIconMessageType;

    NotificationType(TrayIcon.MessageType trayIconMessageType) {
        this.trayIconMessageType = trayIconMessageType;
    }

    public TrayIcon.MessageType getTrayIconMessageType() {
        return trayIconMessageType;
    }
}
