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
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JenkinsNode {

    private final String name;
    private final String displayName;
    @JsonProperty("jobs")
    private final List<JenkinsNodeListItem> children;
    private final List<JenkinsBuildListItem> builds;

    JenkinsNode() {
        this.name = null;
        this.displayName = null;
        this.children = Collections.emptyList();
        this.builds = Collections.emptyList();
    }

    public JenkinsNode(
            String name,
            String displayName,
            List<JenkinsNodeListItem> children,
            List<JenkinsBuildListItem> builds) {

        this.name = name;
        this.displayName = displayName;
        this.children = Collections.unmodifiableList(children);
        this.builds = Collections.unmodifiableList(builds);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<JenkinsNodeListItem> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public List<JenkinsBuildListItem> getBuilds() {
        return Collections.unmodifiableList(builds);
    }

    @Override
    public String toString() {
        return "JenkinsNode{" +
                "name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", children=" + children +
                ", builds=" + builds +
                '}';
    }
}
