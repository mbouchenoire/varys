package org.varys.common.service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public final class VarysTrayIcon {

    private static final String DEFAULT_TRAY_ICON_IMAGE_URL = "https://i.imgur.com/Mlg9Atp.png";

    private static final long LINKABLE_MENU_ITEMS_DURATION = 5L * 60L * 1000L; // 5 minutes

    private static final TrayIcon trayIcon;
    private static final PopupMenu popupMenu;

    static {
        if (SystemTray.isSupported()) {
            Log.debug("SystemTray is supported");

            Image trayIconImage;

            try {
                trayIconImage = ImageIO.read(new URL(DEFAULT_TRAY_ICON_IMAGE_URL));
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
                    Log.error(e, "Failed to initialize Varys tray icon");
                }
            } else {
                trayIcon = null;
                popupMenu = null;
            }
        } else {
            Log.warn("SystemTray is not supported");
            trayIcon = null;
            popupMenu = null;
        }
    }

    private VarysTrayIcon() {
        super();
    }

    public static void addPermanentClickableMenu(String text, Runnable runnable) {
        addClickableMenu(text, runnable, -1);
    }

    public static void addTemporaryClickableMenu(String text, Runnable runnable) {
        addClickableMenu(text, runnable, LINKABLE_MENU_ITEMS_DURATION);
    }

    static void notify(String title, String description, TrayIcon.MessageType messageType) {
        trayIcon.displayMessage(title, description, messageType);
    }

    private static void addClickableMenu(String text, Runnable clickEvent, long timeout) {
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
