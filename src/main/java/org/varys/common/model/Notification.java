package org.varys.common.model;

import java.util.Optional;

public interface Notification {

    String getTitle();

    default Optional<String> getDescription() {
        return Optional.empty();
    }

    default NotificationType getType() {
        return NotificationType.INFO;
    }

    default Optional<Linkable> getLinkable() {
        return Optional.empty();
    }
}
