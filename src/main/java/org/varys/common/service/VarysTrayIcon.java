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
import org.varys.common.Constants;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public final class VarysTrayIcon {

    private static final long LINKABLE_MENU_ITEMS_DURATION = 5L * 60L * 1000L; // 5 minutes

    private static final TrayIcon trayIcon;
    private static final PopupMenu popupMenu;

    static {
        if (SystemTray.isSupported()) {
            Logger.debug("SystemTray is supported");

            Image trayIconImage;

            try {
                trayIconImage = ImageIO.read(new URL(Constants.VARYS_FAVICON_URL));
            } catch (IOException e) {
                trayIconImage = null;
            }

            if (trayIconImage != null) {
                popupMenu = new PopupMenu();
                trayIcon = new TrayIcon(trayIconImage, "Varys", popupMenu);
                trayIcon.setImageAutoSize(true);

                try {
                    SystemTray.getSystemTray().add(trayIcon);
                } catch (AWTException e) {
                    Logger.error(e, "Failed to initialize Varys tray icon");
                }
            } else {
                trayIcon = null;
                popupMenu = null;
            }
        } else {
            Logger.warn("SystemTray is not supported");
            trayIcon = null;
            popupMenu = null;
        }
    }

    private VarysTrayIcon() {
        super();
    }

    static boolean isSupported() {
        return SystemTray.isSupported();
    }

    public static void addPermanentClickableMenu(String text, Runnable runnable) {
        addClickableMenu(text, runnable, -1);
    }

    static void addTemporaryClickableMenu(String text, Runnable runnable) {
        addClickableMenu(text, runnable, LINKABLE_MENU_ITEMS_DURATION);
    }

    static void notify(String title, String description, TrayIcon.MessageType messageType) {
        if (isSupported()) {
            trayIcon.displayMessage(title, description, messageType);
        }
    }

    private static void addClickableMenu(String text, Runnable clickEvent, long timeout) {
        if (isSupported()) {
            final MenuItem item = new MenuItem(text);
            item.addActionListener(actionEvent -> clickEvent.run());

            popupMenu.add(item);

            if (timeout > 0) {
                new Timer("Removing menu item '" + text + "'").schedule(new TimerTask() {
                    @Override
                    public void run() {
                        popupMenu.remove(item);
                    }
                }, timeout);
            }
        }
    }
}
