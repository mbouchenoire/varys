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

package org.varys.common.model;

import fr.jcgay.notification.Notification;

import java.awt.*;

public enum NotificationType {
    NONE(TrayIcon.MessageType.NONE, Notification.Level.INFO),
    INFO(TrayIcon.MessageType.INFO, Notification.Level.INFO),
    WARNING(TrayIcon.MessageType.WARNING, Notification.Level.WARNING),
    ERROR(TrayIcon.MessageType.ERROR, Notification.Level.ERROR);

    private final TrayIcon.MessageType trayIconMessageType;
    private final fr.jcgay.notification.Notification.Level jcgayNotificationLevel;

    NotificationType(
            TrayIcon.MessageType trayIconMessageType,
            fr.jcgay.notification.Notification.Level jcgayNotificationLevel) {

        this.trayIconMessageType = trayIconMessageType;
        this.jcgayNotificationLevel = jcgayNotificationLevel;
    }

    public TrayIcon.MessageType getTrayIconMessageType() {
        return trayIconMessageType;
    }

    public Notification.Level getJcgayNotificationLevel() {
        return jcgayNotificationLevel;
    }
}
