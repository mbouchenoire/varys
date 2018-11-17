package org.varys.gitlab.model.notification;

import org.varys.common.model.NotificationType;
import org.varys.gitlab.model.GitLabMergeRequest;

import java.util.Optional;
import java.util.stream.Stream;

public class MergeRequestUpdateNotificationChain extends MergeRequestUpdateNotification {

    private final boolean shouldNotify;
    private final String title;
    private final NotificationType notificationType;

    public MergeRequestUpdateNotificationChain(GitLabMergeRequest mergeRequest, GitLabMergeRequest previousVersion) {
        super(mergeRequest, previousVersion);

        final Optional<MergeRequestUpdateNotification> optionalNotification = Stream.of(
                new MergedNotification(this.getMergeRequest(), previousVersion),
                new ClosedNotification(this.getMergeRequest(), previousVersion),
                new StatusChangedNotification(this.getMergeRequest(), previousVersion),
                new ChangedAssigneeNotification(this.getMergeRequest(), previousVersion),
                new NewCommitsNotification(this.getMergeRequest(), previousVersion),
                new NewCommentsNotification(this.getMergeRequest(), previousVersion)
        )
        .filter(MergeRequestUpdateNotification::shouldNotify)
        .findFirst();

        this.shouldNotify = optionalNotification
                .map(MergeRequestUpdateNotification::shouldNotify)
                .orElse(false);

        this.title = optionalNotification
                .map(MergeRequestUpdateNotification::getTitle)
                .orElse(null);

        this.notificationType = optionalNotification
                .map(MergeRequestUpdateNotification::getType)
                .orElse(NotificationType.NONE);
    }

    public boolean shouldNotify() {
        return this.shouldNotify;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public NotificationType getType() {
        return this.notificationType;
    }
}
