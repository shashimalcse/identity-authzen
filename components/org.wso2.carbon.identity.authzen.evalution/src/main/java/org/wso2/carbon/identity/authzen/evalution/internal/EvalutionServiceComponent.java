package org.wso2.carbon.identity.authzen.evalution.internal;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.wso2.carbon.identity.authzen.evalution.EvalutionService;
import org.wso2.carbon.identity.authzen.evalution.PolicyEvaluator;
import org.wso2.carbon.identity.authzen.evalution.RBACPolicyEvaluator;

@Component(
        name = "org.wso2.carbon.identity.authzen.evalution.component",
        immediate = true
)
public class EvalutionServiceComponent {

    @Activate
    protected void activate(BundleContext bundleContext) {

        EvalutionDataHolder.setBundleContext(bundleContext);
        bundleContext.registerService(EvalutionService.class.getName(), new EvalutionServiceImpl(), null);
        bundleContext.registerService(PolicyEvaluator.class.getName(), new RBACPolicyEvaluator(), null);
    }

    @Deactivate
    protected void deactivate() {

        EvalutionDataHolder.setBundleContext(null);
    }
}
