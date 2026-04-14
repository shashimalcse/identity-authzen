package org.wso2.carbon.identity.authzen.core.exception;

public class AuthZENException extends Exception {

    private static final long serialVersionUID = -2669104282169350430L;

    private final AuthZENErrorCode errorCode;
    private final String description;

    public AuthZENException(AuthZENErrorCode errorCode, String message, String description) {

        super(message);
        this.errorCode = errorCode;
        this.description = description;
    }

    public AuthZENException(AuthZENErrorCode errorCode, String description) {

        this(errorCode, errorCode.getDefaultMessage(), description);
    }

    public AuthZENErrorCode getErrorCode() {

        return errorCode;
    }

    public String getDescription() {

        return description;
    }
}
