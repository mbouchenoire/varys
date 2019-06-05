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

import fr.jcgay.notification.Application;
import fr.jcgay.notification.Icon;
import fr.jcgay.notification.Notifier;
import fr.jcgay.notification.SendNotification;
import org.pmw.tinylog.Logger;
import org.varys.common.model.Notification;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.varys.common.Constants.*;

public class NotificationService {

    private final String moduleName;
    private final Notifier notifier;

    public NotificationService(String moduleName) throws MalformedURLException {
        this.moduleName = moduleName;

        this.notifier = new SendNotification()
                .setApplication(Application.builder()
                        .icon(Icon.create(new URL(VARYS_FAVICON_URL), String.valueOf(VARYS_FAVICON_URL.hashCode())))
                        .id("varys")
                        .name("Varys")
                        .build())
                .initNotifier();
    }

    public void send(Notification notification) {
        Logger.info("[NOTIFICATION][{}] title={}; description={}; type={}",
                this.moduleName,
                notification.getTitle(),
                notification.getDescription(),
                notification.getType());

        if (VarysTrayIcon.isSupported()) {
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
        } else {
            try {
                final String iconUrl = notification.getIconUrl().orElse(VARYS_FAVICON_URL);

                this.notifier.send(fr.jcgay.notification.Notification.builder()
                        .title(notification.getTitle())
                        .message(notification.getDescription().orElse(""))
                        .level(notification.getType().getJcgayNotificationLevel())
                        .icon(Icon.create(new URL(iconUrl), String.valueOf(iconUrl.hashCode())))
                        .build());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }
}
