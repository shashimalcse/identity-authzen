package org.wso2.carbon.identity.authzen.evalution;

import org.testng.Assert;
import org.testng.annotations.Test;
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

public class PolicyEvaluatorTest {

    @Test
    public void shouldExposeDefaultEvaluatorMetadata() {

        PolicyEvaluator evaluator = new TestPolicyEvaluator();

        Assert.assertEquals(evaluator.getName(), "TestPolicyEvaluator");
        Assert.assertEquals(evaluator.getPriority(), 100);
    }

    @Test
    public void shouldEvaluateUsingPipTypes() throws Exception {

        PolicyEvaluator evaluator = new TestPolicyEvaluator();
        EvaluationRequest request = new EvaluationRequest(
                new SubjectEntity("user", "alice", null),
                new ActionEntity("courses:read", null),
                new ResourceEntity("api_resource", "/api/courses", null),
                null);
        PIPContext pipContext = new PIPContext(
                new SubjectContext("user-1", "carbon.super", List.of(), List.of(), Map.of(), "user"),
                new ResourceContext("/api/courses", "api_resource", List.of("courses:read"), null));

        EvaluationDecision decision = evaluator.evaluate(request, pipContext);

        Assert.assertTrue(decision.isDecision());
        Assert.assertEquals(decision.getContext().get("userId"), "user-1");
        Assert.assertEquals(decision.getContext().get("resourceId"), "/api/courses");
    }

    private static class TestPolicyEvaluator implements PolicyEvaluator {

        @Override
        public EvaluationDecision evaluate(EvaluationRequest request, PIPContext pipContext) {

            return new EvaluationDecision(true, Map.of(
                    "userId", pipContext.getSubjectContext().getUserId(),
                    "resourceId", pipContext.getResourceContext().getResourceId(),
                    "action", request.getAction().getName()));
        }
    }
}
