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

import java.beans.Transient;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JenkinsBuildListItem implements JenkinsBuildNumber {

    private final long number;
    @JsonProperty("url")
    private final String url;

    JenkinsBuildListItem() {
        this.number = -1;
        this.url = null;
    }

    public JenkinsBuildListItem(long number, String url) {
        this.number = number;
        this.url = url;
    }

    @Override
    public long getNumber() {
        return number;
    }

    @Transient
    public String getApiUrl() {
        return (this.url + "/api/json").replace("//", "/").replace(":/", "://");
    }

    @Override
    public String toString() {
        return "JenkinsBuildListItem{" +
                "number=" + number +
                ", url='" + url + '\'' +
                '}';
    }
}
