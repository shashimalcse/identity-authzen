package org.wso2.carbon.identity.authzen.api.internal;

import org.wso2.carbon.identity.authzen.evalution.EvalutionService;
import org.wso2.carbon.identity.authzen.pip.PIPService;

public final class ApiDataHolder {

    private static PIPService pipService;
    private static EvalutionService evalutionService;

    private ApiDataHolder() {
    }

    public static PIPService getPipService() {

        return pipService;
    }

    public static void setPipService(PIPService pipService) {

        ApiDataHolder.pipService = pipService;
    }

    public static EvalutionService getEvalutionService() {

        return evalutionService;
    }

    public static void setEvalutionService(EvalutionService evalutionService) {

        ApiDataHolder.evalutionService = evalutionService;
    }
}
