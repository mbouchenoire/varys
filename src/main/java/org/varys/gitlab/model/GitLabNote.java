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

import java.beans.Transient;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabNote {

    private final long id;
    private final String body;
    @JsonProperty("created_at")
    private final Date createdAt;
    private final GitLabUser author;

    GitLabNote() {
        this.id = -1;
        this.body = null;
        this.createdAt = null;
        this.author = null;
    }

    public GitLabNote(long id, String body, Date createdAt, GitLabUser author) {
        this.id = id;
        this.body = body;
        this.createdAt = createdAt;
        this.author = author;
    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public GitLabUser getAuthor() {
        return author;
    }

    @Transient
    boolean isAutomaticComment() {
        if (body == null) {
            return true;
        }

        return body.startsWith("added")
                || body.startsWith("reopened")
                || body.startsWith("closed")
                || body.startsWith("merged")
                || body.startsWith("changed")
                || body.startsWith("marked");
    }

    @Override
    public String toString() {
        return "GitLabNote{" +
                "id=" + id +
                ", body='" + body + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
