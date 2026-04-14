package org.wso2.carbon.identity.authzen.pip;

import java.util.Objects;

public class PIPContext {

    private SubjectContext subjectContext;
    private ResourceContext resourceContext;

    public PIPContext() {
    }

    public PIPContext(SubjectContext subjectContext, ResourceContext resourceContext) {

        this.subjectContext = subjectContext;
        this.resourceContext = resourceContext;
    }

    public SubjectContext getSubjectContext() {

        return subjectContext;
    }

    public void setSubjectContext(SubjectContext subjectContext) {

        this.subjectContext = subjectContext;
    }

    public ResourceContext getResourceContext() {

        return resourceContext;
    }

    public void setResourceContext(ResourceContext resourceContext) {

        this.resourceContext = resourceContext;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof PIPContext)) {
            return false;
        }
        PIPContext that = (PIPContext) o;
        return Objects.equals(subjectContext, that.subjectContext) &&
                Objects.equals(resourceContext, that.resourceContext);
    }

    @Override
    public int hashCode() {

        return Objects.hash(subjectContext, resourceContext);
    }

    @Override
    public String toString() {

        return "PIPContext{" +
                "subjectContext=" + subjectContext +
                ", resourceContext=" + resourceContext +
                '}';
    }
}
