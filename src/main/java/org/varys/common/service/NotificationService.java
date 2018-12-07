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

package org.varys.common.service;

import org.pmw.tinylog.Logger;
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
        Logger.info("[NOTIFICATION][{}] title={}; description={}; type={}",
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
                        Logger.error(e, "Failed to start browser");
                    }
                }));
    }
}
