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

package org.varys.gitlab.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown =  true)
public class GitLabProject {

    private final long id;
    private final String name;
    @JsonProperty("path_with_namespace")
    private final String pathWithNamespace;

    GitLabProject() {
        this.id = -1;
        this.name = null;
        this.pathWithNamespace = null;
    }

    public GitLabProject(long id, String name, String pathWithNamespace) {
        this.id = id;
        this.name = name;
        this.pathWithNamespace = pathWithNamespace;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPathWithNamespace() {
        return pathWithNamespace;
    }

    @Override
    public String toString() {
        return "GitLabProject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pathWithNamespace='" + pathWithNamespace + '\'' +
                '}';
    }
}
