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

package org.varys.jenkins.model;

public class JenkinsNotifierConfig {

    private final JenkinsNotifierNotificationsConfig notificationsConfig;

    public JenkinsNotifierConfig(JenkinsNotifierNotificationsConfig notificationsConfig) {
        this.notificationsConfig = notificationsConfig;
    }

    public JenkinsNotifierNotificationsConfig getNotificationsConfig() {
        return notificationsConfig;
    }

    @Override
    public String toString() {
        return "JenkinsNotifierConfig{" +
                ", notificationsConfig=" + notificationsConfig +
                '}';
    }
}
