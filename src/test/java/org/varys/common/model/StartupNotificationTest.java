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