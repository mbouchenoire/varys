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

public class JenkinsBuildNotifierNotificationsFiltersConfig {

    private final boolean localBranchesOnly;
    private final boolean successfulBuilds;

    public JenkinsBuildNotifierNotificationsFiltersConfig(boolean localBranchesOnly, boolean successfulBuilds) {
        this.localBranchesOnly = localBranchesOnly;
        this.successfulBuilds = successfulBuilds;
    }

    public boolean localBranchesOnly() {
        return localBranchesOnly;
    }

    public boolean successfulBuilds() {
        return successfulBuilds;
    }

    @Override
    public String toString() {
        return "JenkinsBuildNotifierNotificationsFiltersConfig{" +
                "localBranchesOnly=" + localBranchesOnly +
                ", successfulBuilds=" + successfulBuilds +
                '}';
    }
}
