package org.varys.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RestApiTest {

    @Test
    public void getDomainName() {
        final RestApi restApi = new RestApi() {
            @Override
            public String getLabel() {
                return "test rest api";
            }

            @Override
            public String getBaseUrl() {
                return "http://example.com/test";
            }

            @Override
            public boolean isOnline() {
                return false;
            }
        };

        assertEquals("example.com", restApi.getDomainName());
    }
}