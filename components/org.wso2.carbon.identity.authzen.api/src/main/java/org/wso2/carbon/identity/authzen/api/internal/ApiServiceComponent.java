package org.wso2.carbon.identity.authzen.api.internal;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.wso2.carbon.identity.authzen.api.rest.EvaluationServlet;
import org.wso2.carbon.identity.authzen.evalution.EvalutionService;
import org.wso2.carbon.identity.authzen.pip.PIPService;

import javax.servlet.ServletException;

@Component(
        name = "org.wso2.carbon.identity.authzen.api.component",
        immediate = true
)
public class ApiServiceComponent {

    private static final String EVALUATION_SERVLET_ALIAS = "/access/v1/evaluation";

    private HttpService httpService;

    @Activate
    protected void activate() throws ServletException, NamespaceException {

        httpService.registerServlet(EVALUATION_SERVLET_ALIAS, new EvaluationServlet(), null, null);
    }

    @Deactivate
    protected void deactivate() {

        if (httpService != null) {
            httpService.unregister(EVALUATION_SERVLET_ALIAS);
        }
        ApiDataHolder.setPipService(null);
        ApiDataHolder.setEvalutionService(null);
    }

    @Reference(
            name = "org.osgi.service.http.HttpService",
            service = HttpService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetHttpService"
    )
    protected void setHttpService(HttpService httpService) {

        this.httpService = httpService;
    }

    protected void unsetHttpService(HttpService httpService) {

        this.httpService = null;
    }

    @Reference(
            name = "org.wso2.carbon.identity.authzen.pip.PIPService",
            service = PIPService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetPIPService"
    )
    protected void setPIPService(PIPService pipService) {

        ApiDataHolder.setPipService(pipService);
    }

    protected void unsetPIPService(PIPService pipService) {

        ApiDataHolder.setPipService(null);
    }

    @Reference(
            name = "org.wso2.carbon.identity.authzen.evalution.EvalutionService",
            service = EvalutionService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetEvalutionService"
    )
    protected void setEvalutionService(EvalutionService evalutionService) {

        ApiDataHolder.setEvalutionService(evalutionService);
    }

    protected void unsetEvalutionService(EvalutionService evalutionService) {

        ApiDataHolder.setEvalutionService(null);
    }
}
