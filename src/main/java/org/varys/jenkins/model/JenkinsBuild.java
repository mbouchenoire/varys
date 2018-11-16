package org.varys.jenkins.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import java.beans.Transient;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JenkinsBuild implements JenkinsBuildNumber {

    private static final String BUILD_DATA_CLASS = "hudson.plugins.git.util.BuildData";
    private static final String CAUSE_ACTION_CLASS = "hudson.model.CauseAction";

    private final List<JsonNode> actions;
    private final String fullDisplayName;
    private final String id;
    private final long number;
    private final String result;
    private final String url;

    JenkinsBuild() {
        this.actions = Collections.emptyList();
        this.fullDisplayName = null;
        this.id = null;
        this.number = -1;
        this.result = null;
        this.url = null;
    }

    public List<JsonNode> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public String getFullDisplayName() {
        return fullDisplayName;
    }

    public String getId() {
        return id;
    }

    @Override
    public long getNumber() {
        return number;
    }

    public JenkinsBuildResult getResult() {
        return JenkinsBuildResult.of(this.result);
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
    public Optional<String> getCause() {
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
        final Function<JsonNode, JsonNode> lastBuiltRevision = buildData -> buildData.get("lastBuiltRevision");
        final Function<JsonNode, JsonNode> firstBranchFound = revision -> revision.get("branch").elements().next();
        final Function<JsonNode, String> branchName = branch -> branch.get("name").asText();

        return this.actions.stream()
                .filter(JenkinsBuild::isActionBuildData)
                .findFirst()
                .map(lastBuiltRevision)
                .map(firstBranchFound)
                .map(branchName);
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
}
