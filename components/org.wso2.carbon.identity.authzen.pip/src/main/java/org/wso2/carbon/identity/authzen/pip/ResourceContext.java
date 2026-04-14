package org.wso2.carbon.identity.authzen.pip;

import java.util.List;
import java.util.Objects;

public class ResourceContext {

    private String resourceId;
    private String resourceType;
    private List<String> scopes;
    private String applicationId;

    public ResourceContext() {
    }

    public ResourceContext(String resourceId, String resourceType, List<String> scopes, String applicationId) {

        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.scopes = scopes;
        this.applicationId = applicationId;
    }

    public String getResourceId() {

        return resourceId;
    }

    public void setResourceId(String resourceId) {

        this.resourceId = resourceId;
    }

    public String getResourceType() {

        return resourceType;
    }

    public void setResourceType(String resourceType) {

        this.resourceType = resourceType;
    }

    public List<String> getScopes() {

        return scopes;
    }

    public void setScopes(List<String> scopes) {

        this.scopes = scopes;
    }

    public String getApplicationId() {

        return applicationId;
    }

    public void setApplicationId(String applicationId) {

        this.applicationId = applicationId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceContext)) {
            return false;
        }
        ResourceContext that = (ResourceContext) o;
        return Objects.equals(resourceId, that.resourceId) &&
                Objects.equals(resourceType, that.resourceType) &&
                Objects.equals(scopes, that.scopes) &&
                Objects.equals(applicationId, that.applicationId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(resourceId, resourceType, scopes, applicationId);
    }

    @Override
    public String toString() {

        return "ResourceContext{" +
                "resourceId='" + resourceId + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", scopes=" + scopes +
                ", applicationId='" + applicationId + '\'' +
                '}';
    }
}
