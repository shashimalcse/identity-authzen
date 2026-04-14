package org.wso2.carbon.identity.authzen.pip;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SubjectContext {

    private String userId;
    private String tenantDomain;
    private List<String> roles;
    private List<String> groups;
    private Map<String, String> claims;
    private String subjectType;

    public SubjectContext() {
    }

    public SubjectContext(String userId, String tenantDomain, List<String> roles, List<String> groups,
                          Map<String, String> claims, String subjectType) {

        this.userId = userId;
        this.tenantDomain = tenantDomain;
        this.roles = roles;
        this.groups = groups;
        this.claims = claims;
        this.subjectType = subjectType;
    }

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {

        this.userId = userId;
    }

    public String getTenantDomain() {

        return tenantDomain;
    }

    public void setTenantDomain(String tenantDomain) {

        this.tenantDomain = tenantDomain;
    }

    public List<String> getRoles() {

        return roles;
    }

    public void setRoles(List<String> roles) {

        this.roles = roles;
    }

    public List<String> getGroups() {

        return groups;
    }

    public void setGroups(List<String> groups) {

        this.groups = groups;
    }

    public Map<String, String> getClaims() {

        return claims;
    }

    public void setClaims(Map<String, String> claims) {

        this.claims = claims;
    }

    public String getSubjectType() {

        return subjectType;
    }

    public void setSubjectType(String subjectType) {

        this.subjectType = subjectType;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof SubjectContext)) {
            return false;
        }
        SubjectContext that = (SubjectContext) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(tenantDomain, that.tenantDomain) &&
                Objects.equals(roles, that.roles) &&
                Objects.equals(groups, that.groups) &&
                Objects.equals(claims, that.claims) &&
                Objects.equals(subjectType, that.subjectType);
    }

    @Override
    public int hashCode() {

        return Objects.hash(userId, tenantDomain, roles, groups, claims, subjectType);
    }

    @Override
    public String toString() {

        return "SubjectContext{" +
                "userId='" + userId + '\'' +
                ", tenantDomain='" + tenantDomain + '\'' +
                ", roles=" + roles +
                ", groups=" + groups +
                ", claims=" + claims +
                ", subjectType='" + subjectType + '\'' +
                '}';
    }
}
