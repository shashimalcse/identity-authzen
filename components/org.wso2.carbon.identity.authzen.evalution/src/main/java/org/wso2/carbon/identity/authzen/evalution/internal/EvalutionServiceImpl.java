package org.wso2.carbon.identity.authzen.evalution.internal;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENErrorCode;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENException;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENServerException;
import org.wso2.carbon.identity.authzen.core.model.EvaluationDecision;
import org.wso2.carbon.identity.authzen.core.model.EvaluationRequest;
import org.wso2.carbon.identity.authzen.evalution.EvalutionService;
import org.wso2.carbon.identity.authzen.evalution.PolicyEvaluator;
import org.wso2.carbon.identity.authzen.pip.PIPContext;

import java.util.Collection;

public class EvalutionServiceImpl implements EvalutionService {

    private static final String DEFAULT_EVALUATOR_NAME = "RBAC";

    @Override
    public EvaluationDecision evaluate(EvaluationRequest request, PIPContext pipContext) throws AuthZENException {

        BundleContext bundleContext = EvalutionDataHolder.getBundleContext();
        if (bundleContext == null) {
            throw new AuthZENServerException(AuthZENErrorCode.INTERNAL_ERROR,
                    "Evalution bundle context is not available.");
        }

        try {
            Collection<ServiceReference<PolicyEvaluator>> serviceReferences =
                    bundleContext.getServiceReferences(PolicyEvaluator.class, null);
            for (ServiceReference<PolicyEvaluator> serviceReference : serviceReferences) {
                PolicyEvaluator policyEvaluator = bundleContext.getService(serviceReference);
                try {
                    if (policyEvaluator != null && DEFAULT_EVALUATOR_NAME.equals(policyEvaluator.getName())) {
                        return policyEvaluator.evaluate(request, pipContext);
                    }
                } finally {
                    if (policyEvaluator != null) {
                        bundleContext.ungetService(serviceReference);
                    }
                }
            }
        } catch (InvalidSyntaxException e) {
            throw new AuthZENServerException(AuthZENErrorCode.INTERNAL_ERROR,
                    "Error while loading registered policy evaluators.");
        }

        throw new AuthZENServerException(AuthZENErrorCode.EVALUATION_FAILED,
                "RBAC policy evaluator is not available.");
    }
}
