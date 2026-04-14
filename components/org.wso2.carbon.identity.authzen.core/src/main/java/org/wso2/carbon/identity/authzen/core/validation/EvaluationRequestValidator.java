package org.wso2.carbon.identity.authzen.core.validation;

import org.wso2.carbon.identity.authzen.core.AuthZENConstants;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENClientException;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENErrorCode;
import org.wso2.carbon.identity.authzen.core.model.ActionEntity;
import org.wso2.carbon.identity.authzen.core.model.EvaluationRequest;
import org.wso2.carbon.identity.authzen.core.model.ResourceEntity;
import org.wso2.carbon.identity.authzen.core.model.SubjectEntity;

public final class EvaluationRequestValidator {

    private EvaluationRequestValidator() {
    }

    public static void validate(EvaluationRequest request) throws AuthZENClientException {

        if (request == null) {
            throw new AuthZENClientException(AuthZENErrorCode.INVALID_REQUEST, "Evaluation request is required.");
        }

        SubjectEntity subject = request.getSubject();
        if (subject == null) {
            throw new AuthZENClientException(AuthZENErrorCode.MISSING_SUBJECT, "Subject is required.");
        }

        ActionEntity action = request.getAction();
        if (action == null) {
            throw new AuthZENClientException(AuthZENErrorCode.MISSING_ACTION, "Action is required.");
        }

        ResourceEntity resource = request.getResource();
        if (resource == null) {
            throw new AuthZENClientException(AuthZENErrorCode.MISSING_RESOURCE, "Resource is required.");
        }

        if (!hasText(subject.getType())) {
            throw new AuthZENClientException(AuthZENErrorCode.INVALID_REQUEST, "Subject type is required.");
        }

        if (!hasText(subject.getId())) {
            throw new AuthZENClientException(AuthZENErrorCode.INVALID_REQUEST, "Subject id is required.");
        }

        if (!isSupportedSubjectType(subject.getType())) {
            throw new AuthZENClientException(AuthZENErrorCode.UNSUPPORTED_SUBJECT_TYPE,
                    "Unsupported subject type: " + subject.getType());
        }

        if (!hasText(action.getName())) {
            throw new AuthZENClientException(AuthZENErrorCode.INVALID_REQUEST, "Action name is required.");
        }

        if (!hasText(resource.getType())) {
            throw new AuthZENClientException(AuthZENErrorCode.INVALID_REQUEST, "Resource type is required.");
        }

        if (!hasText(resource.getId())) {
            throw new AuthZENClientException(AuthZENErrorCode.INVALID_REQUEST, "Resource id is required.");
        }

        if (!isSupportedResourceType(resource.getType())) {
            throw new AuthZENClientException(AuthZENErrorCode.UNSUPPORTED_RESOURCE_TYPE,
                    "Unsupported resource type: " + resource.getType());
        }
    }

    private static boolean isSupportedSubjectType(String subjectType) {

        return AuthZENConstants.SUBJECT_TYPE_USER.equals(subjectType) ||
                AuthZENConstants.SUBJECT_TYPE_APPLICATION.equals(subjectType);
    }

    private static boolean isSupportedResourceType(String resourceType) {

        return AuthZENConstants.RESOURCE_TYPE_API_RESOURCE.equals(resourceType) ||
                AuthZENConstants.RESOURCE_TYPE_APPLICATION.equals(resourceType);
    }

    private static boolean hasText(String value) {

        return value != null && !value.trim().isEmpty();
    }
}
