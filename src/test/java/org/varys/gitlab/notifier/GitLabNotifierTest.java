package org.varys.gitlab.notifier;

import org.junit.Test;
import org.varys.common.service.CacheService;
import org.varys.common.service.NotificationService;
import org.varys.gitlab.api.GitLabApi;
import org.varys.gitlab.model.GitLabCommit;
import org.varys.gitlab.model.GitLabMergeRequest;
import org.varys.gitlab.model.GitLabMergeRequestListItem;
import org.varys.gitlab.model.GitLabMergeRequestState;
import org.varys.gitlab.model.GitLabMergeStatus;
import org.varys.gitlab.model.GitLabNote;
import org.varys.gitlab.model.GitLabNotificationsFilters;
import org.varys.gitlab.model.GitLabNotifierConfig;
import org.varys.gitlab.model.GitLabNotifierNotificationsConfig;
import org.varys.gitlab.model.GitLabProject;
import org.varys.gitlab.model.GitLabUser;
import org.varys.gitlab.model.notification.MergeRequestUpdateNotificationChain;
import org.varys.gitlab.model.notification.NewMergeRequestNotification;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class GitLabNotifierTest {

    private static GitLabMergeRequest buildMergeRequest(
            GitLabProject project, GitLabUser author, GitLabUser assigned, GitLabMergeRequestState state) {

        return new GitLabMergeRequest(
                new GitLabMergeRequestListItem(
                        assigned.getId(),
                        assigned.getId(),
                        project.getId(),
                        "mr1",
                        state,
                        GitLabMergeStatus.CAN_BE_MERGED,
                        false,
                        new Date(),
                        "target",
                        "source",
                        author,
                        assigned,
                        2,
                        "http://example.com/mr/1"
                ),
                project,
                Arrays.asList(
                        new GitLabNote(1, "note1"),
                        new GitLabNote(2, "note2")
                ),
                Collections.singletonList(
                        new GitLabCommit("1")
                )
        );
    }

    @Test
    public void iterate() {
        final GitLabUser userMaxime = new GitLabUser(1, "maxime");
        final GitLabUser userFoo = new GitLabUser(2, "foo");

        final GitLabApi gitLabApi = mock(GitLabApi.class);
        when(gitLabApi.isOnline()).thenReturn(true);
        when(gitLabApi.getUser()).thenReturn(userMaxime);

        final GitLabProject project = new GitLabProject(1, "pro1", "pro/pro1");

        final GitLabMergeRequest assignedToMaxime =
                buildMergeRequest(project, userFoo, userMaxime, GitLabMergeRequestState.OPENED);
        final GitLabMergeRequest assignedToFoo =
                buildMergeRequest(project, userMaxime, userFoo, GitLabMergeRequestState.OPENED);

        when(gitLabApi.getMergeRequests(GitLabMergeRequestState.OPENED))
                .thenReturn(Arrays.asList(
                        assignedToMaxime,
                        assignedToFoo
                ));

        when(gitLabApi.getMergeRequest(
                assignedToMaxime.getProject().getId(), assignedToMaxime.getId(), assignedToMaxime.getIid()))
                .thenReturn(Optional.of(assignedToMaxime));

        final CacheService cacheService = new CacheService("GitLabNotifierTest");
        cacheService.clear();

        final NotificationService notificationService = mock(NotificationService.class);

        final GitLabNotifier notifier =
                new GitLabNotifier(
                        new GitLabNotifierConfig(
                                new GitLabNotifierNotificationsConfig(
                                        10,
                                        new GitLabNotificationsFilters(true, 24))
                        ),
                        gitLabApi,
                        cacheService,
                        notificationService
                );

        notifier.iterate();

        verify(notificationService, times(1))
                .send(new NewMergeRequestNotification(assignedToMaxime));

        notifier.iterate();
        verify(notificationService, times(1)).send(any()); // did not send a new notification

        // maxime's merge request is not opened anymore
        when(gitLabApi.getMergeRequests(GitLabMergeRequestState.OPENED))
                .thenReturn(Collections.singletonList(assignedToFoo));

        final GitLabMergeRequest assignedToMaximeMerged =
                buildMergeRequest(project, userFoo, userMaxime, GitLabMergeRequestState.MERGED);

        when(gitLabApi.getMergeRequest(
                assignedToMaxime.getProject().getId(), assignedToMaxime.getId(), assignedToMaxime.getIid()))
                .thenReturn(Optional.of(assignedToMaximeMerged));

        when(gitLabApi.getUser()).thenReturn(userFoo);

        notifier.iterate();
        verify(notificationService, times(1)).send(
                new MergeRequestUpdateNotificationChain(assignedToMaximeMerged, assignedToMaxime, userFoo, 24));
    }
}