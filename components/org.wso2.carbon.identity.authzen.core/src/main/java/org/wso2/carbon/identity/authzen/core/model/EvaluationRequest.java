package org.wso2.carbon.identity.authzen.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EvaluationRequest {

    private SubjectEntity subject;

    private ActionEntity action;

    private ResourceEntity resource;

    private ContextEntity context;

    public EvaluationRequest() {
    }

    public EvaluationRequest(SubjectEntity subject, ActionEntity action, ResourceEntity resource,
                             ContextEntity context) {

        this.subject = subject;
        this.action = action;
        this.resource = resource;
        this.context = context;
    }

    public SubjectEntity getSubject() {

        return subject;
    }

    public void setSubject(SubjectEntity subject) {

        this.subject = subject;
    }

    public ActionEntity getAction() {

        return action;
    }

    public void setAction(ActionEntity action) {

        this.action = action;
    }

    public ResourceEntity getResource() {

        return resource;
    }

    public void setResource(ResourceEntity resource) {

        this.resource = resource;
    }

    public ContextEntity getContext() {

        return context;
    }

    public void setContext(ContextEntity context) {

        this.context = context;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof EvaluationRequest)) {
            return false;
        }
        EvaluationRequest that = (EvaluationRequest) o;
        return Objects.equals(subject, that.subject) &&
                Objects.equals(action, that.action) &&
                Objects.equals(resource, that.resource) &&
                Objects.equals(context, that.context);
    }

    @Override
    public int hashCode() {

        return Objects.hash(subject, action, resource, context);
    }

    @Override
    public String toString() {

        return "EvaluationRequest{" +
                "subject=" + subject +
                ", action=" + action +
                ", resource=" + resource +
                ", context=" + context +
                '}';
    }
}
