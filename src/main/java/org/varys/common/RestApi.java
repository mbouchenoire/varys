package org.varys.common;

import java.net.URI;
import java.net.URISyntaxException;

public interface RestApi {

    String getLabel();

    String getBaseUrl();

    boolean isOnline();

    default String getDomainName() {
        try {
            final String baseUrl = getBaseUrl();
            final URI uri = new URI(baseUrl);
            return uri.getHost();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to obtain REST API domain name");
        }
    }
}
