package org.wso2.carbon.identity.authzen.core.validation;

import org.wso2.carbon.identity.authzen.core.exception.AuthZENClientException;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENErrorCode;
import org.wso2.carbon.identity.authzen.core.model.ActionEntity;
import org.wso2.carbon.identity.authzen.core.model.EvaluationRequest;
import org.wso2.carbon.identity.authzen.core.model.ResourceEntity;
import org.wso2.carbon.identity.authzen.core.model.SubjectEntity;

import org.testng.Assert;
import org.testng.annotations.Test;

public class EvaluationRequestValidatorTest {

    @Test
    public void shouldRejectMissingSubject() {

        EvaluationRequest request = new EvaluationRequest(null, new ActionEntity("read", null),
                new ResourceEntity("api_resource", "resource-1", null), null);

        AuthZENClientException exception = Assert.expectThrows(AuthZENClientException.class,
                () -> EvaluationRequestValidator.validate(request));

        Assert.assertEquals(exception.getErrorCode(), AuthZENErrorCode.MISSING_SUBJECT);
    }

    @Test
    public void shouldRejectMissingAction() {

        EvaluationRequest request = new EvaluationRequest(new SubjectEntity("user", "alice", null), null,
                new ResourceEntity("api_resource", "resource-1", null), null);

        AuthZENClientException exception = Assert.expectThrows(AuthZENClientException.class,
                () -> EvaluationRequestValidator.validate(request));

        Assert.assertEquals(exception.getErrorCode(), AuthZENErrorCode.MISSING_ACTION);
    }

    @Test
    public void shouldRejectMissingResource() {

        EvaluationRequest request = new EvaluationRequest(new SubjectEntity("user", "alice", null),
                new ActionEntity("read", null), null, null);

        AuthZENClientException exception = Assert.expectThrows(AuthZENClientException.class,
                () -> EvaluationRequestValidator.validate(request));

        Assert.assertEquals(exception.getErrorCode(), AuthZENErrorCode.MISSING_RESOURCE);
    }

    @Test
    public void shouldRejectBlankSubjectType() {

        EvaluationRequest request = new EvaluationRequest(new SubjectEntity(" ", "alice", null),
                new ActionEntity("read", null), new ResourceEntity("api_resource", "resource-1", null), null);

        AuthZENClientException exception = Assert.expectThrows(AuthZENClientException.class,
                () -> EvaluationRequestValidator.validate(request));

        Assert.assertEquals(exception.getErrorCode(), AuthZENErrorCode.INVALID_REQUEST);
        Assert.assertEquals(exception.getDescription(), "Subject type is required.");
    }

    @Test
    public void shouldRejectBlankSubjectId() {

        EvaluationRequest request = new EvaluationRequest(new SubjectEntity("user", " ", null),
                new ActionEntity("read", null), new ResourceEntity("api_resource", "resource-1", null), null);

        AuthZENClientException exception = Assert.expectThrows(AuthZENClientException.class,
                () -> EvaluationRequestValidator.validate(request));

        Assert.assertEquals(exception.getErrorCode(), AuthZENErrorCode.INVALID_REQUEST);
        Assert.assertEquals(exception.getDescription(), "Subject id is required.");
    }

    @Test
    public void shouldRejectUnsupportedSubjectType() {

        EvaluationRequest request = new EvaluationRequest(new SubjectEntity("group", "alice", null),
                new ActionEntity("read", null), new ResourceEntity("api_resource", "resource-1", null), null);

        AuthZENClientException exception = Assert.expectThrows(AuthZENClientException.class,
                () -> EvaluationRequestValidator.validate(request));

        Assert.assertEquals(exception.getErrorCode(), AuthZENErrorCode.UNSUPPORTED_SUBJECT_TYPE);
    }

    @Test
    public void shouldRejectBlankActionName() {

        EvaluationRequest request = new EvaluationRequest(new SubjectEntity("user", "alice", null),
                new ActionEntity(" ", null), new ResourceEntity("api_resource", "resource-1", null), null);

        AuthZENClientException exception = Assert.expectThrows(AuthZENClientException.class,
                () -> EvaluationRequestValidator.validate(request));

        Assert.assertEquals(exception.getErrorCode(), AuthZENErrorCode.INVALID_REQUEST);
        Assert.assertEquals(exception.getDescription(), "Action name is required.");
    }

    @Test
    public void shouldRejectBlankResourceType() {

        EvaluationRequest request = new EvaluationRequest(new SubjectEntity("user", "alice", null),
                new ActionEntity("read", null), new ResourceEntity(" ", "resource-1", null), null);

        AuthZENClientException exception = Assert.expectThrows(AuthZENClientException.class,
                () -> EvaluationRequestValidator.validate(request));

        Assert.assertEquals(exception.getErrorCode(), AuthZENErrorCode.INVALID_REQUEST);
        Assert.assertEquals(exception.getDescription(), "Resource type is required.");
    }

    @Test
    public void shouldRejectBlankResourceId() {

        EvaluationRequest request = new EvaluationRequest(new SubjectEntity("user", "alice", null),
                new ActionEntity("read", null), new ResourceEntity("api_resource", " ", null), null);

        AuthZENClientException exception = Assert.expectThrows(AuthZENClientException.class,
                () -> EvaluationRequestValidator.validate(request));

        Assert.assertEquals(exception.getErrorCode(), AuthZENErrorCode.INVALID_REQUEST);
        Assert.assertEquals(exception.getDescription(), "Resource id is required.");
    }

    @Test
    public void shouldRejectUnsupportedResourceType() {

        EvaluationRequest request = new EvaluationRequest(new SubjectEntity("user", "alice", null),
                new ActionEntity("read", null), new ResourceEntity("document", "resource-1", null), null);

        AuthZENClientException exception = Assert.expectThrows(AuthZENClientException.class,
                () -> EvaluationRequestValidator.validate(request));

        Assert.assertEquals(exception.getErrorCode(), AuthZENErrorCode.UNSUPPORTED_RESOURCE_TYPE);
    }

    @Test
    public void shouldAcceptValidRequest() throws AuthZENClientException {

        EvaluationRequest request = new EvaluationRequest(new SubjectEntity("application", "client-id", null),
                new ActionEntity("courses:read", null),
                new ResourceEntity("application", "app-id", null), null);

        EvaluationRequestValidator.validate(request);
    }
}
