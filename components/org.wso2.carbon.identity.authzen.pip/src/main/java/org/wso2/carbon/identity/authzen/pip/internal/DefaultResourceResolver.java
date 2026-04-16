package org.wso2.carbon.identity.authzen.pip.internal;

import org.wso2.carbon.identity.api.resource.mgt.APIResourceManager;
import org.wso2.carbon.identity.api.resource.mgt.APIResourceMgtException;
import org.wso2.carbon.identity.application.common.model.APIResource;
import org.wso2.carbon.identity.application.common.model.AuthorizedAPI;
import org.wso2.carbon.identity.application.common.model.Scope;
import org.wso2.carbon.identity.application.mgt.AuthorizedAPIManagementService;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENClientException;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENErrorCode;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENException;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENServerException;
import org.wso2.carbon.identity.authzen.core.model.ResourceEntity;
import org.wso2.carbon.identity.authzen.pip.ResourceContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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

            String appId = extractAppId(resource);
            List<String> scopes = appId != null
                    ? resolveAppScopedScopes(appId, apiResource.getId(), tenantDomain)
                    : resolveScopes(apiResource.getScopes());

            return new ResourceContext(
                    apiResource.getIdentifier() != null ? apiResource.getIdentifier() : resource.getId(),
                    RESOURCE_TYPE_API_RESOURCE,
                    scopes,
                    appId);
        } catch (APIResourceMgtException e) {
            throw new AuthZENServerException(AuthZENErrorCode.INTERNAL_ERROR,
                    "Error while resolving resource: " + resource.getId());
        }
    }

    private String extractAppId(ResourceEntity resource) {

        Map<String, Object> properties = resource.getProperties();
        if (properties == null) {
            return null;
        }
        Object appId = properties.get("app_id");
        return (appId instanceof String && !((String) appId).isBlank()) ? (String) appId : null;
    }

    private List<String> resolveAppScopedScopes(String appId, String apiResourceId, String tenantDomain)
            throws AuthZENServerException {

        AuthorizedAPIManagementService authorizedAPIManagementService =
                PIPDataHolder.getAuthorizedAPIManagementService();
        if (authorizedAPIManagementService == null) {
            throw new AuthZENServerException(AuthZENErrorCode.INTERNAL_ERROR,
                    "AuthorizedAPIManagementService is not available.");
        }

        try {
            AuthorizedAPI authorizedAPI =
                    authorizedAPIManagementService.getAuthorizedAPI(appId, apiResourceId, tenantDomain);
            if (authorizedAPI == null) {
                return Collections.emptyList();
            }
            return resolveScopes(authorizedAPI.getScopes());
        } catch (Exception e) {
            throw new AuthZENServerException(AuthZENErrorCode.INTERNAL_ERROR,
                    "Error while resolving app-scoped API resource for app: " + appId);
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
