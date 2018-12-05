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
import org.varys.common.service.Log;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabVersion {

    private final String version;

    GitLabVersion() {
        super();
        this.version = null;
    }

    public GitLabVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public int getMajor() {
        if (this.version == null) {
            Log.warn("Cannot parse GitLab major version (null version)");
            return -1;
        }

        final String majorString = version.split("\\.")[0];
        return Integer.parseInt(majorString);
    }

    public int getMinor() {
        if (this.version == null) {
            Log.warn("Cannot parse GitLab minor version (null version)");
            return -1;
        }

        final String majorString = version.split("\\.")[1];
        return Integer.parseInt(majorString);
    }

    @Override
    public String toString() {
        return this.version;
    }
}
