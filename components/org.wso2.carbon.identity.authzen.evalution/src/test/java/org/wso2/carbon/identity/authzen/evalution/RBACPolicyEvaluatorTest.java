package org.wso2.carbon.identity.authzen.evalution;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENErrorCode;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENServerException;
import org.wso2.carbon.identity.authzen.core.model.ActionEntity;
import org.wso2.carbon.identity.authzen.core.model.EvaluationDecision;
import org.wso2.carbon.identity.authzen.core.model.EvaluationRequest;
import org.wso2.carbon.identity.authzen.core.model.ResourceEntity;
import org.wso2.carbon.identity.authzen.core.model.SubjectEntity;
import org.wso2.carbon.identity.authzen.pip.PIPContext;
import org.wso2.carbon.identity.authzen.pip.ResourceContext;
import org.wso2.carbon.identity.authzen.pip.SubjectContext;

import java.util.List;
import java.util.Map;

public class RBACPolicyEvaluatorTest {

    @Test
    public void shouldExposeRbacMetadata() {

        RBACPolicyEvaluator evaluator = new RBACPolicyEvaluator();

        Assert.assertEquals(evaluator.getName(), "RBAC");
        Assert.assertEquals(evaluator.getPriority(), 10);
    }

    @Test
    public void shouldGrantWhenRoleScopesContainAction() throws Exception {

        RBACPolicyEvaluator evaluator = new TestRBACPolicyEvaluator(
                List.of("student"), List.of("courses:read", "internal_login"), null);

        EvaluationDecision decision = evaluator.evaluate(buildRequest("user", "/api/courses", "courses:read"),
                buildPipContext("user", "api_resource", "/api/courses", "courses:read"));

        Assert.assertTrue(decision.isDecision());
        Assert.assertEquals(decision.getContext().get("evaluated_roles"), List.of("student"));
    }

    @Test
    public void shouldDenyWhenPipContextIsMissing() throws Exception {

        RBACPolicyEvaluator evaluator = new RBACPolicyEvaluator();

        EvaluationDecision decision = evaluator.evaluate(buildRequest("user", "/api/courses", "courses:read"), null);

        Assert.assertFalse(decision.isDecision());
        Assert.assertEquals(decision.getContext().get("reason_admin"),
                Map.of("403", "PIP context is required for RBAC evaluation."));
    }

    @Test
    public void shouldThrowCheckedExceptionWhenRoleResolutionFails() {

        RBACPolicyEvaluator evaluator = new TestRBACPolicyEvaluator(
                null, null, new AuthZENServerException(AuthZENErrorCode.EVALUATION_FAILED,
                        "Failed to resolve user roles for RBAC evaluation."));

        AuthZENServerException exception = Assert.expectThrows(AuthZENServerException.class,
                () -> evaluator.evaluate(buildRequest("user", "/api/courses", "courses:read"),
                        buildPipContext("user", "api_resource", "/api/courses", "courses:read")));

        Assert.assertTrue(exception.getDescription().contains("Failed to resolve user roles"));
    }

    private static EvaluationRequest buildRequest(String subjectType, String resourceId, String actionName) {

        return new EvaluationRequest(
                new SubjectEntity(subjectType, "alice", null),
                new ActionEntity(actionName, null),
                new ResourceEntity("api_resource", resourceId, null),
                null);
    }

    private static PIPContext buildPipContext(String subjectType, String resourceType, String resourceId,
                                              String... scopes) {

        return new PIPContext(
                new SubjectContext("user-1", "carbon.super", List.of(), List.of(), Map.of(), subjectType),
                new ResourceContext(resourceId, resourceType, List.of(scopes), null));
    }

    private static final class TestRBACPolicyEvaluator extends RBACPolicyEvaluator {

        private final List<String> roles;
        private final List<String> scopes;
        private final AuthZENServerException roleFailure;

        private TestRBACPolicyEvaluator(List<String> roles, List<String> scopes, AuthZENServerException roleFailure) {

            this.roles = roles;
            this.scopes = scopes;
            this.roleFailure = roleFailure;
        }

        @Override
        List<String> getEvaluatedRoles(String userId, String tenantDomain) throws AuthZENServerException {

            if (roleFailure != null) {
                throw roleFailure;
            }
            return roles;
        }

        @Override
        List<String> getAssociatedScopes(List<String> roles, String tenantDomain) throws AuthZENServerException {

            return scopes;
        }
    }
}
