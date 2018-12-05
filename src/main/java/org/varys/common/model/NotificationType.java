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
