package org.wso2.carbon.identity.authzen.core.exception;

public class AuthZENClientException extends AuthZENException {

    private static final long serialVersionUID = -7035308860030834112L;

    public AuthZENClientException(AuthZENErrorCode errorCode, String message, String description) {

        super(errorCode, message, description);
    }

    public AuthZENClientException(AuthZENErrorCode errorCode, String description) {

        super(errorCode, description);
    }
}
