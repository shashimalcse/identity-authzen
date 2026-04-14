package org.wso2.carbon.identity.authzen.evalution;

import org.wso2.carbon.identity.authzen.core.exception.AuthZENException;
import org.wso2.carbon.identity.authzen.core.model.EvaluationDecision;
import org.wso2.carbon.identity.authzen.core.model.EvaluationRequest;
import org.wso2.carbon.identity.authzen.pip.PIPContext;

public interface PolicyEvaluator {

    EvaluationDecision evaluate(EvaluationRequest request, PIPContext pipContext) throws AuthZENException;

    default String getName() {

        return getClass().getSimpleName();
    }

    default int getPriority() {

        return 100;
    }
}
