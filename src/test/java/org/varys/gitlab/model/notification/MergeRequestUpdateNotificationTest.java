package org.varys.gitlab.model.notification;

import org.junit.Test;
import org.varys.common.model.NotificationType;
import org.varys.gitlab.model.GitLabCommit;
import org.varys.gitlab.model.GitLabMergeRequest;
import org.varys.gitlab.model.GitLabMergeRequestListItem;
import org.varys.gitlab.model.GitLabMergeRequestState;
import org.varys.gitlab.model.GitLabMergeStatus;
import org.varys.gitlab.model.GitLabNote;
import org.varys.gitlab.model.GitLabProject;
import org.varys.gitlab.model.GitLabUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;

public class MergeRequestUpdateNotificationTest {

    private static final GitLabUser GIT_LAB_USER_A = new GitLabUser(
            1,
            "user1");

    private static final GitLabUser GIT_LAB_USER_B = new GitLabUser(
            2,
            "user2");

    private static final GitLabProject GIT_LAB_PROJECT = new GitLabProject(1, "name", "projectNamespace");

    private static final GitLabMergeRequest MERGE_REQUEST = new GitLabMergeRequest(
            new GitLabMergeRequestListItem(
                    1,
                    1,
                    1,
                    "title",
                    GitLabMergeRequestState.OPENED,
                    GitLabMergeStatus.CAN_BE_MERGED,
                    false,
                    new Date(),
                    "targetBranch",
                    "sourceBranch",
                    GIT_LAB_USER_A,
                    GIT_LAB_USER_B,
                    2,
                    "url.com"
            ),
            GIT_LAB_PROJECT,
            Collections.singletonList(new GitLabNote(1, "body")),
            Collections.singletonList(new GitLabCommit("1"))
    );

    private static final GitLabMergeRequest ADDED_COMMIT = new GitLabMergeRequest(
            new GitLabMergeRequestListItem(
                    1,
                    1,
                    1,
                    "title",
                    GitLabMergeRequestState.OPENED,
                    GitLabMergeStatus.CAN_BE_MERGED,
                    false,
                    new Date(),
                    "targetBranch",
                    "sourceBranch",
                    GIT_LAB_USER_A,
                    GIT_LAB_USER_B,
                    2,
                    "url.com"
            ),
            GIT_LAB_PROJECT,
            Collections.singletonList(new GitLabNote(1, "body")),
            Arrays.asList(
                    new GitLabCommit("1"),
                    new GitLabCommit("2"))
    );

    private static final GitLabMergeRequest ADDED_COMMENT = new GitLabMergeRequest(
            new GitLabMergeRequestListItem(
                    1,
                    1,
                    1,
                    "title",
                    GitLabMergeRequestState.OPENED,
                    GitLabMergeStatus.CAN_BE_MERGED,
                    false,
                    new Date(),
                    "targetBranch",
                    "sourceBranch",
                    GIT_LAB_USER_A,
                    GIT_LAB_USER_B,
                    2,
                    "url.com"
            ),
            GIT_LAB_PROJECT,
            Arrays.asList(
                    new GitLabNote(1, "body"),
                    new GitLabNote(2, "body2")),
            Collections.singletonList(new GitLabCommit("1"))
    );

    private static final GitLabMergeRequest OTHER_ASSIGNEE = new GitLabMergeRequest(
            new GitLabMergeRequestListItem(
                    1,
                    1,
                    1,
                    "title",
                    GitLabMergeRequestState.OPENED,
                    GitLabMergeStatus.CAN_BE_MERGED,
                    false,
                    new Date(),
                    "targetBranch",
                    "sourceBranch",
                    GIT_LAB_USER_A,
                    GIT_LAB_USER_A,
                    2,
                    "url.com"
            ),
            GIT_LAB_PROJECT,
            Collections.singletonList(new GitLabNote(1, "body")),
            Collections.singletonList(new GitLabCommit("1"))
    );

    private static final GitLabMergeRequest MERGED = new GitLabMergeRequest(
            new GitLabMergeRequestListItem(
                    1,
                    1,
                    1,
                    "title",
                    GitLabMergeRequestState.MERGED,
                    GitLabMergeStatus.CAN_BE_MERGED,
                    false,
                    new Date(),
                    "targetBranch",
                    "sourceBranch",
                    GIT_LAB_USER_A,
                    GIT_LAB_USER_B,
                    2,
                    "url.com"
            ),
            GIT_LAB_PROJECT,
            Collections.singletonList(new GitLabNote(1, "body")),
            Collections.singletonList(new GitLabCommit("1"))
    );

    private static final GitLabMergeRequest CLOSED = new GitLabMergeRequest(
            new GitLabMergeRequestListItem(
                    1,
                    1,
                    1,
                    "title",
                    GitLabMergeRequestState.CLOSED,
                    GitLabMergeStatus.CAN_BE_MERGED,
                    false,
                    new Date(),
                    "targetBranch",
                    "sourceBranch",
                    GIT_LAB_USER_A,
                    GIT_LAB_USER_B,
                    2,
                    "url.com"
            ),
            GIT_LAB_PROJECT,
            Collections.singletonList(new GitLabNote(1, "body")),
            Collections.singletonList(new GitLabCommit("1"))
    );

    private static final GitLabMergeRequest MERGED_WITH_COMMENT = new GitLabMergeRequest(
            new GitLabMergeRequestListItem(
                    1,
                    1,
                    1,
                    "title",
                    GitLabMergeRequestState.MERGED,
                    GitLabMergeStatus.CAN_BE_MERGED,
                    false,
                    new Date(),
                    "targetBranch",
                    "sourceBranch",
                    GIT_LAB_USER_A,
                    GIT_LAB_USER_B,
                    2,
                    "url.com"
            ),
            GIT_LAB_PROJECT,
            Arrays.asList(
                    new GitLabNote(1, "body"),
                    new GitLabNote(2, "body2")),
            Collections.singletonList(new GitLabCommit("1"))
    );

    @Test
    public void shouldNotNotifyNoChange() {
        assertFalse(new MergeRequestUpdateNotificationChain(
                MERGE_REQUEST, MERGE_REQUEST, GIT_LAB_USER_A, 24).shouldNotify());
    }

    @Test
    public void shouldNotifyAddedCommit() {
        assertTrue(new MergeRequestUpdateNotificationChain(
                ADDED_COMMIT, MERGE_REQUEST, GIT_LAB_USER_B, 24).shouldNotify());
    }

    @Test
    public void shouldNotifyAddedComment() {
        assertTrue(new MergeRequestUpdateNotificationChain(
                ADDED_COMMENT, MERGE_REQUEST, GIT_LAB_USER_A,24).shouldNotify());
    }

    @Test
    public void shouldNotifyChangedAssignee() {
        assertTrue(new MergeRequestUpdateNotificationChain(
                OTHER_ASSIGNEE, MERGE_REQUEST, GIT_LAB_USER_A, 24).shouldNotify());
    }

    @Test
    public void shouldNotifyMerged() {
        assertTrue(new MergeRequestUpdateNotificationChain(
                MERGED, MERGE_REQUEST, GIT_LAB_USER_A, 24).shouldNotify());
    }

    @Test
    public void shouldNotifyClosed() {
        assertTrue(new MergeRequestUpdateNotificationChain(
                CLOSED, MERGE_REQUEST, GIT_LAB_USER_A, 24).shouldNotify());
    }

    @Test
    public void getNotificationTitleMerged() {
        assertTrue(new MergeRequestUpdateNotificationChain(
                MERGED, MERGE_REQUEST, GIT_LAB_USER_A, 24).getTitle().contains("merged"));
    }

    @Test
    public void getNotificationTitleClosed() {
        assertTrue(new MergeRequestUpdateNotificationChain(
                CLOSED, MERGE_REQUEST, GIT_LAB_USER_A,24).getTitle().contains("closed"));
    }

    @Test
    public void getNotificationTypeMerged() {
        assertEquals(
                NotificationType.INFO,
                new MergeRequestUpdateNotificationChain(
                        MERGED, MERGE_REQUEST, GIT_LAB_USER_A, 24).getType());
    }

    @Test
    public void getNotificationTypeClosed() {
        assertEquals(
                NotificationType.WARNING,
                new MergeRequestUpdateNotificationChain(CLOSED, MERGE_REQUEST, GIT_LAB_USER_A, 24).getType());
    }

    @Test
    public void notifyMergedBeforeAddedComment() {
        assertTrue(
                new MergeRequestUpdateNotificationChain(
                        MERGED_WITH_COMMENT, MERGE_REQUEST, GIT_LAB_USER_A, 24).getTitle().contains("merged"));
    }
}