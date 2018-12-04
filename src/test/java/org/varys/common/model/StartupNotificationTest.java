package org.varys.common.model;

import org.junit.Test;
import org.varys.common.service.NotifierModule;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StartupNotificationTest {

    public static final StartupNotification STARTUP_NOTIFICATION = new StartupNotification(Arrays.asList(
            new NotifierModule() {
                @Override
                public String getName() {
                    return "module1";
                }

                @Override
                public long getPeriodSeconds() {
                    return 0;
                }

                @Override
                public void iterate() {
                }
            }
    ));

    @Test
    public void getTitle() {
        assertTrue(STARTUP_NOTIFICATION.getTitle().contains("running"));
    }

    @Test
    public void getDescription() {
        assertTrue(STARTUP_NOTIFICATION.getDescription().map(s -> s.contains("module1")).orElse(false));
    }

    @Test
    public void getType() {
        assertEquals(NotificationType.INFO, STARTUP_NOTIFICATION.getType());
    }
}