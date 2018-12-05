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

import org.varys.common.service.NotifierModule;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

public class StartupNotification implements Notification {

    private final Collection<NotifierModule> runningModules;

    public StartupNotification(Collection<NotifierModule> runningModules) {
        this.runningModules = Collections.unmodifiableCollection(runningModules);
    }

    @Override
    public String getTitle() {
        return "Varys is up and running!";
    }

    @Override
    public Optional<String> getDescription() {
        final String description = "Enabled module(s):\n" + this.runningModules.stream()
                .map(NotifierModule::getName)
                .collect(Collectors.joining(", "));

        return Optional.of(description);
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }
}
