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

package org.varys.common.service;

import org.junit.Test;
import org.varys.common.RestApi;
import org.varys.common.model.ApiBackOnlineNotification;
import org.varys.common.model.ApiDownNotification;
import org.varys.common.model.RestApiStatus;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class RestApiServiceTest {

    @Test
    public void notifyApiStatus() {
        final RestApi api = mock(RestApi.class);
        when(api.getStatus()).thenReturn(new RestApiStatus(true, true, true, true));

        final CacheService cacheService = new CacheService("RestApiServiceTest");
        cacheService.clear();
        final NotificationService notificationService = mock(NotificationService.class);

        final RestApiService restApiService = new RestApiService(cacheService, notificationService);

        assertTrue(restApiService.notifyApiStatus(api));
        verify(notificationService, times(0)).send(any());

        when(api.getStatus()).thenReturn(new RestApiStatus(false, true, true, true));
        assertFalse(restApiService.notifyApiStatus(api));
        verify(notificationService, times(1)).send(new ApiDownNotification(api));

        when(api.getStatus()).thenReturn(new RestApiStatus(true, true, true, true));
        assertTrue(restApiService.notifyApiStatus(api));
        verify(notificationService, times(1)).send(new ApiBackOnlineNotification(api));
    }
}