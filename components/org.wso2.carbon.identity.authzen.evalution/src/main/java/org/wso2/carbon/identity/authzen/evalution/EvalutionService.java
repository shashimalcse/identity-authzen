package org.wso2.carbon.identity.authzen.evalution;

import org.wso2.carbon.identity.authzen.core.exception.AuthZENException;
import org.wso2.carbon.identity.authzen.core.model.EvaluationDecision;
import org.wso2.carbon.identity.authzen.core.model.EvaluationRequest;
import org.wso2.carbon.identity.authzen.pip.PIPContext;

public interface EvalutionService {

    EvaluationDecision evaluate(EvaluationRequest request, PIPContext pipContext) throws AuthZENException;
}
