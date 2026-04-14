package org.wso2.carbon.identity.authzen.api.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.authzen.api.internal.ApiDataHolder;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENClientException;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENException;
import org.wso2.carbon.identity.authzen.core.model.EvaluationDecision;
import org.wso2.carbon.identity.authzen.core.model.EvaluationRequest;
import org.wso2.carbon.identity.authzen.core.model.EvaluationResponse;
import org.wso2.carbon.identity.authzen.core.validation.EvaluationRequestValidator;
import org.wso2.carbon.identity.authzen.pip.PIPContext;
import org.wso2.carbon.identity.authzen.pip.ResourceContext;
import org.wso2.carbon.identity.authzen.pip.SubjectContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EvaluationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String CONTENT_TYPE_JSON = "application/json";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType(CONTENT_TYPE_JSON);
        resp.setCharacterEncoding("UTF-8");

        String contentType = req.getContentType();
        if (contentType == null || !contentType.toLowerCase().contains(CONTENT_TYPE_JSON)) {
            resp.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            writeGenericError(resp, "unsupported_media_type", "Content-Type must be application/json",
                    "AZ-41500");
            return;
        }

        EvaluationRequest request;
        try {
            request = OBJECT_MAPPER.readValue(req.getReader(), EvaluationRequest.class);
        } catch (JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeGenericError(resp, "invalid_request", "Request body is not valid JSON", "AZ-40000");
            return;
        }

        try {
            EvaluationRequestValidator.validate(request);

            String tenantDomain = PrivilegedCarbonContext.getThreadLocalCarbonContext().getTenantDomain();

            SubjectContext subjectContext = ApiDataHolder.getPipService()
                    .resolveSubject(request.getSubject(), tenantDomain);
            ResourceContext resourceContext = ApiDataHolder.getPipService()
                    .resolveResource(request.getResource(), tenantDomain);

            PIPContext pipContext = new PIPContext(subjectContext, resourceContext);

            EvaluationDecision decision = ApiDataHolder.getEvalutionService().evaluate(request, pipContext);

            EvaluationResponse response = new EvaluationResponse(decision.isDecision(), decision.getContext());
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(OBJECT_MAPPER.writeValueAsString(response));

        } catch (AuthZENClientException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeErrorResponse(resp, e);
        } catch (AuthZENException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            writeErrorResponse(resp, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType(CONTENT_TYPE_JSON);
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        writeGenericError(resp, "method_not_allowed", "Only POST is supported", "AZ-40500");
    }

    private void writeErrorResponse(HttpServletResponse resp, AuthZENException e) throws IOException {

        Map<String, String> error = new HashMap<>();
        error.put("error", e.getErrorCode().name().toLowerCase());
        error.put("error_description", e.getDescription());
        error.put("error_code", e.getErrorCode().getCode());
        resp.getWriter().write(OBJECT_MAPPER.writeValueAsString(error));
    }

    private void writeGenericError(HttpServletResponse resp, String error, String description, String code)
            throws IOException {

        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", error);
        errorBody.put("error_description", description);
        errorBody.put("error_code", code);
        resp.getWriter().write(OBJECT_MAPPER.writeValueAsString(errorBody));
    }
}
