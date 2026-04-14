package org.wso2.carbon.identity.authzen.evalution;

import org.wso2.carbon.identity.authzen.core.exception.AuthZENException;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENErrorCode;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENServerException;
import org.wso2.carbon.identity.authzen.core.model.EvaluationDecision;
import org.wso2.carbon.identity.authzen.core.model.EvaluationRequest;
import org.wso2.carbon.identity.authzen.pip.PIPContext;
import org.wso2.carbon.identity.authzen.pip.ResourceContext;
import org.wso2.carbon.identity.authzen.pip.SubjectContext;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.util.AuthzUtil;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RBACPolicyEvaluator implements PolicyEvaluator {

    private static final String SUBJECT_TYPE_USER = "user";
    private static final String SUBJECT_TYPE_APPLICATION = "application";
    private static final String RESOURCE_TYPE_API_RESOURCE = "api_resource";
    private static final String RESOURCE_TYPE_APPLICATION = "application";
    private static final String STATUS_GRANTED = "200";
    private static final String STATUS_DENIED = "403";

    @Override
    public EvaluationDecision evaluate(EvaluationRequest request, PIPContext pipContext) throws AuthZENException {

        long startTime = System.nanoTime();
        SubjectContext subjectContext = pipContext != null ? pipContext.getSubjectContext() : null;
        ResourceContext resourceContext = pipContext != null ? pipContext.getResourceContext() : null;

        if (request == null) {
            return deny(Collections.emptyList(), startTime, "Evaluation request is required.");
        }
        if (request.getResource() == null) {
            return deny(Collections.emptyList(), startTime, "Resource is required for RBAC evaluation.");
        }
        if (request.getAction() == null) {
            return deny(Collections.emptyList(), startTime, "Action is required for RBAC evaluation.");
        }
        if (pipContext == null) {
            return deny(Collections.emptyList(), startTime, "PIP context is required for RBAC evaluation.");
        }
        if (subjectContext == null) {
            return deny(Collections.emptyList(), startTime, "Subject context is required for RBAC evaluation.");
        }
        if (resourceContext == null) {
            return deny(Collections.emptyList(), startTime, "Resolved resource context is required for RBAC evaluation.");
        }

        String resourceType = resourceContext.getResourceType();
        if (!RESOURCE_TYPE_API_RESOURCE.equals(resourceType)) {
            if (RESOURCE_TYPE_APPLICATION.equals(resourceType)) {
                return deny(Collections.emptyList(), startTime,
                        "RBAC evaluation currently supports only api_resource resources.");
            }
            return deny(Collections.emptyList(), startTime,
                    "Unsupported resource type for RBAC evaluation: " + resourceType);
        }

        String subjectType = subjectContext.getSubjectType();
        if (!SUBJECT_TYPE_USER.equals(subjectType)) {
            if (SUBJECT_TYPE_APPLICATION.equals(subjectType)) {
                return deny(normalizeRoles(subjectContext.getRoles()), startTime,
                        "RBAC evaluation for application subjects is not supported yet.");
            }
            return deny(normalizeRoles(subjectContext.getRoles()), startTime,
                    "Unsupported subject type for RBAC evaluation: " + subjectType);
        }

        String tenantDomain = resolveTenantDomain(subjectContext);
        if (tenantDomain == null || tenantDomain.isBlank()) {
            return deny(Collections.emptyList(), startTime, "Tenant domain is required for RBAC evaluation.");
        }

        String actionName = request.getAction().getName();
        String resourceIdentifier = resourceContext.getResourceId();
        List<String> resourceScopes = normalizeRoles(resourceContext.getScopes());
        if (!resourceScopes.contains(actionName)) {
            return deny(Collections.emptyList(), startTime,
                    "Action " + actionName + " is not defined for API resource " + resourceIdentifier + ".");
        }

        String userId = subjectContext.getUserId();
        if (userId == null || userId.isBlank()) {
            return deny(Collections.emptyList(), startTime, "User id is required for RBAC evaluation.");
        }

        List<String> evaluatedRoles = getEvaluatedRoles(userId, tenantDomain);
        List<String> permittedScopes = getAssociatedScopes(evaluatedRoles, tenantDomain);
        if (intersectScopes(permittedScopes, resourceScopes).contains(actionName)) {
            return grant(evaluatedRoles, startTime, "Role(s) " + evaluatedRoles + " grant " + actionName + " on " +
                    resourceIdentifier + ".");
        }

        return deny(evaluatedRoles, startTime, "Role(s) " + evaluatedRoles + " do not grant " + actionName +
                " on " + resourceIdentifier + ".");
    }

    @Override
    public String getName() {

        return "RBAC";
    }

    @Override
    public int getPriority() {

        return 10;
    }

    List<String> getEvaluatedRoles(String userId, String tenantDomain) throws AuthZENServerException {

        try {
            return normalizeRoles(AuthzUtil.getRoles(userId, tenantDomain));
        } catch (IdentityOAuth2Exception e) {
            throw new AuthZENServerException(AuthZENErrorCode.EVALUATION_FAILED,
                    "Failed to resolve user roles for RBAC evaluation.");
        }
    }

    List<String> getAssociatedScopes(List<String> roles, String tenantDomain) throws AuthZENServerException {

        try {
            return AuthzUtil.getAssociatedScopesForRoles(roles, tenantDomain);
        } catch (IdentityOAuth2Exception e) {
            throw new AuthZENServerException(AuthZENErrorCode.EVALUATION_FAILED,
                    "Failed to resolve role scopes for RBAC evaluation.");
        }
    }

    private String resolveTenantDomain(SubjectContext subjectContext) {

        if (subjectContext.getTenantDomain() != null && !subjectContext.getTenantDomain().isBlank()) {
            return subjectContext.getTenantDomain();
        }
        return null;
    }

    private EvaluationDecision grant(List<String> evaluatedRoles, long startTime, String reason) {

        return new EvaluationDecision(true, buildContext(STATUS_GRANTED, reason, evaluatedRoles, startTime));
    }

    private EvaluationDecision deny(List<String> evaluatedRoles, long startTime, String reason) {

        return new EvaluationDecision(false, buildContext(STATUS_DENIED, reason, evaluatedRoles, startTime));
    }

    private Map<String, Object> buildContext(String statusCode, String reason, List<String> evaluatedRoles,
                                             long startTime) {

        Map<String, Object> context = new LinkedHashMap<>();
        context.put("reason_admin", Collections.singletonMap(statusCode, reason));
        context.put("evaluated_roles", normalizeRoles(evaluatedRoles));
        context.put("evaluation_time_ms", Math.max(0L, (System.nanoTime() - startTime) / 1_000_000L));
        return context;
    }

    private List<String> normalizeRoles(List<String> roles) {

        if (roles == null) {
            return Collections.emptyList();
        }
        return roles;
    }

    private Set<String> intersectScopes(List<String> grantedScopes, List<String> resourceScopes) {

        Set<String> resourceScopeSet = resourceScopes.stream().collect(Collectors.toSet());
        return grantedScopes.stream()
                .filter(resourceScopeSet::contains)
                .collect(Collectors.toSet());
    }
}
