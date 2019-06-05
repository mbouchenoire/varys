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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import org.varys.common.Constants;
import org.varys.common.model.Linkable;
import org.varys.common.model.Notification;
import org.varys.common.model.NotificationType;

import java.beans.Transient;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JenkinsBuild implements JenkinsBuildNumber, Notification, Linkable {

    private static final String BUILD_DATA_CLASS = "hudson.plugins.git.util.BuildData";
    private static final String CAUSE_ACTION_CLASS = "hudson.model.CauseAction";

    private final List<JsonNode> actions;
    private final String fullDisplayName;
    private final long duration;
    private final String id;
    private final long number;
    private final String result;
    private final String url;

    JenkinsBuild() {
        this.actions = Collections.emptyList();
        this.fullDisplayName = null;
        this.duration = -1;
        this.id = null;
        this.number = -1;
        this.result = null;
        this.url = null;
    }

    public JenkinsBuild(
            List<JsonNode> actions,
            String fullDisplayName,
            long duration,
            String id,
            long number,
            JenkinsBuildResult result,
            String url) {

        this.actions = Collections.unmodifiableList(actions);
        this.fullDisplayName = fullDisplayName;
        this.duration = duration;
        this.id = id;
        this.number = number;
        this.result = result.getCode();
        this.url = url;
    }

    public List<JsonNode> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public String getFullDisplayName() {
        return fullDisplayName;
    }

    public long getDuration() {
        return duration;
    }

    public String getId() {
        return id;
    }

    @Override
    public long getNumber() {
        return number;
    }

    public JenkinsBuildResult getResult() {
        return JenkinsBuildResult.ofCode(this.result);
    }

    @Transient
    public boolean hasResult() {
        return !this.getResult().equals(JenkinsBuildResult.NONE);
    }

    public String getUrl() {
        return url;
    }

    @Transient
    public boolean isNotSuccess() {
        return !this.getResult().equals(JenkinsBuildResult.SUCCESS);
    }

    private static boolean isCauseAction(JsonNode actionNode) {
        return actionNode.get("_class").asText().equals(CAUSE_ACTION_CLASS);
    }

    private static String getShortDescription(JsonNode causeActionNode) {
        final Iterator<JsonNode> causeNodes = causeActionNode.get("causes").iterator();
        return causeNodes.next().get("shortDescription").asText();
    }

    @Transient
    private Optional<String> getCause() {
        return this.actions.stream()
                .filter(JenkinsBuild::isCauseAction)
                .findFirst()
                .map(JenkinsBuild::getShortDescription);
    }

    private static boolean isActionBuildData(JsonNode actionNode) {
        final JsonNode actionClassNode = actionNode.get("_class");

        if (actionClassNode == null) {
            return false;
        }

        return actionClassNode.asText().equals(BUILD_DATA_CLASS);
    }

    @Transient
    public Optional<String> getBranchName() {
        final UnaryOperator<JsonNode> lastBuiltRevision = buildData -> buildData.get("lastBuiltRevision");
        final UnaryOperator<JsonNode> firstBranchFound = revision -> revision.get("branch").elements().next();
        final Function<JsonNode, String> branchName = branch -> branch.get("name").asText();

        return this.actions.stream()
                .filter(JenkinsBuild::isActionBuildData)
                .findFirst()
                .map(lastBuiltRevision)
                .map(firstBranchFound)
                .map(branchName);
    }

    @Transient
    @Override
    public String getTitle() {
        return this.getResult().getAdjective() + " Jenkins build";
    }

    private static String timeConversion(long totalMillis) {
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        final long totalSeconds = totalMillis / 1000;
        final long seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        final long totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        final long minutes = totalMinutes % MINUTES_IN_AN_HOUR;

        return  minutes + "mn " + seconds + "s";
    }

    @Transient
    @Override
    public Optional<String> getDescription() {
        final String description = this.getFullDisplayName() + "\n" +
                this.getCause().orElse("Unknown cause") + "\n" +
                "Duration: " + timeConversion(this.duration);

        return Optional.of(description);
    }

    @Transient
    @Override
    public NotificationType getType() {
        return this.getResult().getNotificationType();
    }

    @Transient
    @Override
    public String getLabel() {
        return "Jenkins - " + this.fullDisplayName;
    }

    @Transient
    @Override
    public Optional<Linkable> getLinkable() {
        return Optional.of(this);
    }

    @Transient
    @Override
    public Optional<String> getIconUrl() {
        return Optional.of(Constants.JENKINS_FAVICON_URL);
    }

    @Override
    public String toString() {
        return "JenkinsBuild{" +
                "actions=" + actions +
                ", fullDisplayName='" + fullDisplayName + '\'' +
                ", id='" + id + '\'' +
                ", number=" + number +
                ", result='" + result + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JenkinsBuild that = (JenkinsBuild) o;
        return Objects.equals(fullDisplayName, that.fullDisplayName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullDisplayName);
    }
}
