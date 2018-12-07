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

package org.varys.gitlab.api;

import org.pmw.tinylog.Logger;

public class GitLabApiLocator {

    public GitLabApi findUsable(GitLabApiV3 gitLabApiV3, GitLabApiV4 gitLabApiV4) {
        if (!gitLabApiV3.isAuthorized() && !gitLabApiV4.isAuthorized()) {
            throw new IllegalArgumentException("Failed to authenticate agains't GitLab API (verify your private token)");
        }

        if (gitLabApiV3.isCompatible()) {
            Logger.info("Using compatible GitLab API v3");
            return gitLabApiV3;
        } else if (gitLabApiV4.isCompatible()) {
            Logger.info("GitLab API v3 is not compatible, using compatible API v4");
            return gitLabApiV4;
        } else {
            throw new UnsupportedOperationException("Cannot find compatible GitLab API version");
        }
    }
}
