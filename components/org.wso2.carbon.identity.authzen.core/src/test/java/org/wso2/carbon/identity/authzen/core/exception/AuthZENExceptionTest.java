package org.wso2.carbon.identity.authzen.core.exception;

import org.testng.Assert;
import org.testng.annotations.Test;

public class AuthZENExceptionTest {

    @Test
    public void shouldExposeErrorCodeMetadata() {

        Assert.assertEquals(AuthZENErrorCode.MISSING_SUBJECT.getCode(), "AZ-40001");
        Assert.assertEquals(AuthZENErrorCode.INTERNAL_ERROR.getDefaultMessage(), "Internal server error.");
    }

    @Test
    public void shouldExposeClientExceptionFields() {

        AuthZENClientException exception = new AuthZENClientException(AuthZENErrorCode.INVALID_REQUEST,
                "Invalid request.", "Subject type is required.");

        Assert.assertEquals(exception.getErrorCode(), AuthZENErrorCode.INVALID_REQUEST);
        Assert.assertEquals(exception.getMessage(), "Invalid request.");
        Assert.assertEquals(exception.getDescription(), "Subject type is required.");
    }
}
