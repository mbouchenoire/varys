package org.varys.gitlab.model.notification;

import org.varys.common.model.NotificationType;
import org.varys.gitlab.model.GitLabMergeRequest;
import org.varys.gitlab.model.GitLabUser;

import java.util.Optional;
import java.util.stream.Stream;

public class MergeRequestUpdateNotificationChain extends MergeRequestUpdateNotification {

    private final boolean shouldNotify;
    private final String title;
    private final NotificationType notificationType;

    public MergeRequestUpdateNotificationChain(
            GitLabMergeRequest mergeRequest,
            GitLabMergeRequest previousVersion,
            GitLabUser myself,
            long hoursBeforeReminder) {

        super(mergeRequest, previousVersion);

        final Optional<MergeRequestUpdateNotification> optionalNotification = Stream.of(
                new MergedNotification(mergeRequest, previousVersion, myself),
                new ClosedNotification(mergeRequest, previousVersion),
                new StatusChangedNotification(mergeRequest, previousVersion),
                new ChangedAssigneeNotification(mergeRequest, previousVersion),
                new WipNotification(mergeRequest, previousVersion, myself),
                new ConflictedMergeRequestNotification(mergeRequest, previousVersion, myself),
                new NewCommitsNotification(mergeRequest, previousVersion, myself),
                new NewCommentsNotification(mergeRequest, previousVersion, myself),
                new PendingMergeRequestNotification(mergeRequest, previousVersion, hoursBeforeReminder)
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
    protected String getHeader() {
        return null;
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
