package org.wso2.carbon.identity.authzen.core.exception;

public class AuthZENServerException extends AuthZENException {

    private static final long serialVersionUID = -5439045652669374958L;

    public AuthZENServerException(AuthZENErrorCode errorCode, String message, String description) {

        super(errorCode, message, description);
    }

    public AuthZENServerException(AuthZENErrorCode errorCode, String description) {

        super(errorCode, description);
    }
}
