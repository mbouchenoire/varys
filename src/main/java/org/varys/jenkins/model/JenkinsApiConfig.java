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

public class JenkinsApiConfig {

    private final String baseUrl;
    private final String apiToken;
    private final boolean sslVerify;

    public JenkinsApiConfig(String baseUrl, String apiToken, boolean sslVerify) {
        this.baseUrl = baseUrl;
        this.apiToken = apiToken;
        this.sslVerify = sslVerify;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiToken() {
        return apiToken;
    }

    public boolean isSslVerify() {
        return sslVerify;
    }

    @Override
    public String toString() {
        return "JenkinsApiConfig{" +
                "baseUrl='" + baseUrl + '\'' +
                ", apiToken='" + "<hidden>" + '\'' +
                ", sslVerify='" + sslVerify + '\'' +
                '}';
    }
}
