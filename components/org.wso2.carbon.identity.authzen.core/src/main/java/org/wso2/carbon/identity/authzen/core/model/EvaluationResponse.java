package org.wso2.carbon.identity.authzen.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluationResponse {

    private boolean decision;

    private Map<String, Object> context;

    public EvaluationResponse() {
    }

    public EvaluationResponse(boolean decision, Map<String, Object> context) {

        this.decision = decision;
        this.context = context;
    }

    public boolean isDecision() {

        return decision;
    }

    public void setDecision(boolean decision) {

        this.decision = decision;
    }

    public Map<String, Object> getContext() {

        return context;
    }

    public void setContext(Map<String, Object> context) {

        this.context = context;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof EvaluationResponse)) {
            return false;
        }
        EvaluationResponse that = (EvaluationResponse) o;
        return decision == that.decision &&
                Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {

        return Objects.hash(decision, context);
    }

    @Override
    public String toString() {

        return "EvaluationResponse{" +
                "decision=" + decision +
                ", context=" + context +
                '}';
    }
}
