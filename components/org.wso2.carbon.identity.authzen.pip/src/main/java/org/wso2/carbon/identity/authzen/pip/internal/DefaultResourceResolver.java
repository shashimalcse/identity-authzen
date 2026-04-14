package org.wso2.carbon.identity.authzen.pip.internal;

import org.wso2.carbon.identity.api.resource.mgt.APIResourceManager;
import org.wso2.carbon.identity.api.resource.mgt.APIResourceMgtException;
import org.wso2.carbon.identity.application.common.model.APIResource;
import org.wso2.carbon.identity.application.common.model.Scope;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENClientException;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENErrorCode;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENException;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENServerException;
import org.wso2.carbon.identity.authzen.core.model.ResourceEntity;
import org.wso2.carbon.identity.authzen.pip.ResourceContext;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class DefaultResourceResolver {

    private static final String RESOURCE_TYPE_API_RESOURCE = "api_resource";

    ResourceContext resolve(ResourceEntity resource, String tenantDomain) throws AuthZENException {

        if (resource == null) {
            throw new AuthZENClientException(AuthZENErrorCode.MISSING_RESOURCE,
                    "Resource is required for resource resolution.");
        }
        if (!RESOURCE_TYPE_API_RESOURCE.equals(resource.getType())) {
            throw new AuthZENClientException(AuthZENErrorCode.UNSUPPORTED_RESOURCE_TYPE,
                    "Only api_resource resources are supported by PIP at this stage.");
        }
        if (resource.getId() == null || resource.getId().isBlank()) {
            throw new AuthZENClientException(AuthZENErrorCode.INVALID_REQUEST,
                    "Resource id is required for resource resolution.");
        }

        APIResourceManager apiResourceManager = PIPDataHolder.getApiResourceManager();
        if (apiResourceManager == null) {
            throw new AuthZENServerException(AuthZENErrorCode.INTERNAL_ERROR,
                    "APIResourceManager is not available.");
        }

        try {
            APIResource apiResource = apiResourceManager.getAPIResourceByIdentifier(resource.getId(), tenantDomain);
            if (apiResource == null) {
                throw new AuthZENClientException(AuthZENErrorCode.MISSING_RESOURCE,
                        "API resource not found for identifier: " + resource.getId());
            }
            return new ResourceContext(
                    apiResource.getIdentifier() != null ? apiResource.getIdentifier() : resource.getId(),
                    RESOURCE_TYPE_API_RESOURCE,
                    resolveScopes(apiResource.getScopes()),
                    null);
        } catch (APIResourceMgtException e) {
            throw new AuthZENServerException(AuthZENErrorCode.INTERNAL_ERROR,
                    "Error while resolving resource: " + resource.getId());
        }
    }

    private List<String> resolveScopes(List<Scope> scopes) {

        if (scopes == null) {
            return Collections.emptyList();
        }
        return scopes.stream()
                .map(Scope::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
