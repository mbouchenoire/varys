package org.varys.common.model;

public final class RestApiStatus {

    private final boolean isOnline;
    private final boolean isCompatible;
    private final boolean validSslCertificate;
    private final boolean validPrivateToken;

    public RestApiStatus(
            boolean isOnline, boolean isCompatible, boolean validSslCertificate, boolean validPrivateToken) {

        this.isOnline = isOnline;
        this.isCompatible = isCompatible;
        this.validSslCertificate = validSslCertificate;
        this.validPrivateToken = validPrivateToken;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public boolean isCompatible() {
        return isCompatible;
    }

    public boolean isValidSslCertificate() {
        return validSslCertificate;
    }

    public boolean isValidPrivateToken() {
        return validPrivateToken;
    }
}
