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
