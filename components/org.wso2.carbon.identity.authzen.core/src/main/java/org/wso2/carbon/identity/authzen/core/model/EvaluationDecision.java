package org.wso2.carbon.identity.authzen.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluationDecision {

    private boolean decision;

    private Map<String, Object> context;

    public EvaluationDecision() {
    }

    public EvaluationDecision(boolean decision, Map<String, Object> context) {

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
        if (!(o instanceof EvaluationDecision)) {
            return false;
        }
        EvaluationDecision that = (EvaluationDecision) o;
        return decision == that.decision &&
                Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {

        return Objects.hash(decision, context);
    }

    @Override
    public String toString() {

        return "EvaluationDecision{" +
                "decision=" + decision +
                ", context=" + context +
                '}';
    }
}
