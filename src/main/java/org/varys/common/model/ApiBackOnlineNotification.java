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

package org.varys.common.model;

import org.varys.common.RestApi;

import java.util.Objects;

public final class ApiBackOnlineNotification implements Notification {

    private final RestApi api;

    public ApiBackOnlineNotification(RestApi api) {
        this.api = api;
    }

    @Override
    public String getTitle() {
        return this.api.getLabel() + " is back online!";
    }

    @Override
    public NotificationType getType() {
        return NotificationType.INFO;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiBackOnlineNotification that = (ApiBackOnlineNotification) o;
        return Objects.equals(api.getDomainName(), that.api.getDomainName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(api.getDomainName());
    }
}
