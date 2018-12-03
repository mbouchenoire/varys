package org.varys.gitlab.api;

import org.varys.gitlab.model.GitLabUser;
import retrofit2.Call;

interface GitLabApiRetrofit {

    Call<GitLabUser> getUser(String privateToken);
}
