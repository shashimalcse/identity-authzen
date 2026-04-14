package org.wso2.carbon.identity.authzen.evalution.internal;

import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENServerException;
import org.wso2.carbon.identity.authzen.core.model.ActionEntity;
import org.wso2.carbon.identity.authzen.core.model.EvaluationDecision;
import org.wso2.carbon.identity.authzen.core.model.EvaluationRequest;
import org.wso2.carbon.identity.authzen.core.model.ResourceEntity;
import org.wso2.carbon.identity.authzen.core.model.SubjectEntity;
import org.wso2.carbon.identity.authzen.evalution.PolicyEvaluator;
import org.wso2.carbon.identity.authzen.pip.PIPContext;
import org.wso2.carbon.identity.authzen.pip.ResourceContext;
import org.wso2.carbon.identity.authzen.pip.SubjectContext;

import java.util.List;
import java.util.Map;

public class EvalutionServiceImplTest {

    @AfterMethod
    public void tearDown() {

        EvalutionDataHolder.setBundleContext(null);
    }

    @Test
    public void shouldUseRegisteredRbacEvaluator() throws Exception {

        BundleContext bundleContext = Mockito.mock(BundleContext.class);
        @SuppressWarnings("unchecked")
        ServiceReference<PolicyEvaluator> serviceReference = Mockito.mock(ServiceReference.class);
        PolicyEvaluator policyEvaluator = Mockito.mock(PolicyEvaluator.class);
        EvalutionDataHolder.setBundleContext(bundleContext);
        EvaluationDecision expectedDecision = new EvaluationDecision(true, Map.of("source", "rbac"));

        Mockito.when(bundleContext.getServiceReferences(PolicyEvaluator.class, null))
                .thenReturn(List.of(serviceReference));
        Mockito.when(bundleContext.getService(serviceReference)).thenReturn(policyEvaluator);
        Mockito.when(policyEvaluator.getName()).thenReturn("RBAC");
        Mockito.when(policyEvaluator.evaluate(Mockito.any(), Mockito.any())).thenReturn(expectedDecision);

        EvalutionServiceImpl evalutionService = new EvalutionServiceImpl();
        EvaluationDecision actualDecision = evalutionService.evaluate(buildRequest(), buildPipContext());

        Assert.assertEquals(actualDecision, expectedDecision);
        Mockito.verify(bundleContext).ungetService(serviceReference);
    }

    @Test
    public void shouldFailWhenRbacEvaluatorIsNotAvailable() throws Exception {

        BundleContext bundleContext = Mockito.mock(BundleContext.class);
        @SuppressWarnings("unchecked")
        ServiceReference<PolicyEvaluator> serviceReference = Mockito.mock(ServiceReference.class);
        PolicyEvaluator policyEvaluator = Mockito.mock(PolicyEvaluator.class);
        EvalutionDataHolder.setBundleContext(bundleContext);

        Mockito.when(bundleContext.getServiceReferences(PolicyEvaluator.class, null))
                .thenReturn(List.of(serviceReference));
        Mockito.when(bundleContext.getService(serviceReference)).thenReturn(policyEvaluator);
        Mockito.when(policyEvaluator.getName()).thenReturn("XACML");

        EvalutionServiceImpl evalutionService = new EvalutionServiceImpl();

        Assert.expectThrows(AuthZENServerException.class,
                () -> evalutionService.evaluate(buildRequest(), buildPipContext()));
    }

    private static EvaluationRequest buildRequest() {

        return new EvaluationRequest(
                new SubjectEntity("user", "alice", null),
                new ActionEntity("courses:read", null),
                new ResourceEntity("api_resource", "/api/courses", null),
                null);
    }

    private static PIPContext buildPipContext() {

        return new PIPContext(
                new SubjectContext("user-1", "carbon.super", List.of(), List.of(), Map.of(), "user"),
                new ResourceContext("/api/courses", "api_resource", List.of("courses:read"), null));
    }
}
