package org.varys.common.service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class NotificationService {

    private static final String DEFAULT_TRAY_ICON_IMAGE_URL =
            "https://ssl.gstatic.com/images/branding/product/1x/alerts_512dp.png";

    private final String moduleName;
    private final TrayIcon trayIcon;

    public NotificationService(String moduleName) {
        this(moduleName, null);
    }

    NotificationService(String moduleName, String trayIconUrl) {
        this.moduleName = moduleName;

        if (SystemTray.isSupported()) {
            Log.debug("SystemTray is supported");
        } else {
            Log.warn("SystemTray is not supported");
        }

        final Image image = buildTrayIconImage(trayIconUrl);

//            final PopupMenu popup = new PopupMenu();
//            MenuItem item = new MenuItem("contextual menu label");
//            item.addActionListener(e -> Log.error("clicked on contextual menu!"));
//            popup.add(item);

//            final TrayIcon trayIcon = new TrayIcon(image, title, popup);

        this.trayIcon = new TrayIcon(image, this.moduleName);
        this.trayIcon.setImageAutoSize(true);

        try {
            SystemTray.getSystemTray().add(this.trayIcon);
        } catch (AWTException e) {
            Log.error(e, "Failed to initialize tray icon for module '{}'", this.moduleName);
        }
    }

    public void notify(String title, String description, TrayIcon.MessageType messageType) {
        Log.info("[NOTIFICATION][{}] title={}; description={}; type={}",
                this.moduleName,
                title,
                description.replace("\\n", ""),
                messageType);

        this.trayIcon.displayMessage(title, description, messageType);
    }

    @Override
    protected void finalize() {
        SystemTray.getSystemTray().remove(this.trayIcon);
    }

    private static Image buildTrayIconImage(String textUrl) {
        final URL url = getTrayIconUrl(textUrl);

        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static URL getTrayIconUrl(String url) {
        try {
            if (url == null || url.equals("")) {
                return new URL(DEFAULT_TRAY_ICON_IMAGE_URL);
            } else {
                return new URL(url);
            }
        } catch (MalformedURLException e) {
            Log.error(e, "Failed to url={}", url);
            throw new RuntimeException(e);
        }
    }
}
