package org.varys.common.service;

import org.junit.Test;
import org.varys.common.RestApi;
import org.varys.common.model.ApiBackOnlineNotification;
import org.varys.common.model.ApiDownNotification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class RestApiServiceTest {

    @Test
    public void notifyApiStatus() {
        final RestApi api = mock(RestApi.class);
        when(api.isOnline()).thenReturn(true);

        final CacheService cacheService = new CacheService("RestApiServiceTest");
        final NotificationService notificationService = mock(NotificationService.class);

        final RestApiService restApiService = new RestApiService(cacheService, notificationService);

        assertTrue(restApiService.notifyApiStatus(api));
        verify(notificationService, times(0)).send(any());

        when(api.isOnline()).thenReturn(false);
        assertFalse(restApiService.notifyApiStatus(api));
        verify(notificationService, times(1)).send(new ApiDownNotification(api));

        when(api.isOnline()).thenReturn(true);
        assertTrue(restApiService.notifyApiStatus(api));
        verify(notificationService, times(1)).send(new ApiBackOnlineNotification(api));
    }
}