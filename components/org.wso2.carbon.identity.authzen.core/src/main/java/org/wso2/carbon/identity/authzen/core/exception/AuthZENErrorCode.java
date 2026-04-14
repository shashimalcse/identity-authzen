package org.wso2.carbon.identity.authzen.core.exception;

public enum AuthZENErrorCode {

    INVALID_REQUEST("AZ-40000", "Invalid request."),
    MISSING_SUBJECT("AZ-40001", "Subject is required."),
    MISSING_ACTION("AZ-40002", "Action is required."),
    MISSING_RESOURCE("AZ-40003", "Resource is required."),
    UNSUPPORTED_SUBJECT_TYPE("AZ-40004", "Unsupported subject type."),
    UNSUPPORTED_RESOURCE_TYPE("AZ-40005", "Unsupported resource type."),
    EVALUATION_FAILED("AZ-50001", "Evaluation failed."),
    INTERNAL_ERROR("AZ-50002", "Internal server error.");

    private final String code;
    private final String defaultMessage;

    AuthZENErrorCode(String code, String defaultMessage) {

        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {

        return code;
    }

    public String getDefaultMessage() {

        return defaultMessage;
    }
}
