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
import org.varys.common.RestApi;
import org.varys.gitlab.model.GitLabMergeRequest;
import org.varys.gitlab.model.GitLabMergeRequestState;
import org.varys.gitlab.model.GitLabUser;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public interface GitLabApi extends RestApi {

    @Override
    default String getLabel() {
        return "GitLab";
    }

    int getVersion();
    boolean isAuthorized();
    boolean isCompatible();
    GitLabUser getUser();
    List<GitLabMergeRequest> getMergeRequests(GitLabMergeRequestState state);
    Optional<GitLabMergeRequest> getMergeRequest(long projectId, long mergeRequestId, long mergeRequestIid);

    default GitLabUser getUser(Supplier<Call<GitLabUser>> gitLabUserSupplier) {
        try {
            final Response<GitLabUser> response = gitLabUserSupplier.get().execute();

            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw new IOException(response.message());
            }
        } catch (IOException e) {
            final String msg = "Failed to fetch GitLab user";
            Logger.error(e, msg);
            throw new IllegalStateException(msg);
        }
    }
}
