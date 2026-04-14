package org.wso2.carbon.identity.authzen.pip.internal;

import org.wso2.carbon.identity.authzen.core.exception.AuthZENClientException;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENErrorCode;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENException;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENServerException;
import org.wso2.carbon.identity.authzen.core.model.SubjectEntity;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.multi.attribute.login.mgt.MultiAttributeLoginService;
import org.wso2.carbon.identity.multi.attribute.login.mgt.ResolvedUserResult;
import org.wso2.carbon.identity.multi.attribute.login.mgt.ResolvedUserResult.UserResolvedStatus;
import org.wso2.carbon.identity.authzen.pip.SubjectContext;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.core.UserStoreException;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.common.User;
import org.wso2.carbon.user.core.service.RealmService;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

final class DefaultSubjectResolver {

    @FunctionalInterface
    interface TenantIdResolver {

        int resolve(String tenantDomain);
    }

    private static final String SUBJECT_TYPE_USER = "user";
    private static final String SUPER_TENANT_DOMAIN_NAME = "carbon.super";
    private static final String[] REQUIRED_CLAIMS = {
            "http://wso2.org/claims/emailaddress",
            "http://wso2.org/claims/mobile",
            "http://wso2.org/claims/userid"
    };
    private final TenantIdResolver tenantIdResolver;

    DefaultSubjectResolver() {

        this(IdentityTenantUtil::getTenantId);
    }

    DefaultSubjectResolver(TenantIdResolver tenantIdResolver) {

        this.tenantIdResolver = tenantIdResolver;
    }

    SubjectContext resolve(SubjectEntity subject, String tenantDomain) throws AuthZENException {

        if (subject == null) {
            throw new AuthZENClientException(AuthZENErrorCode.MISSING_SUBJECT, "Subject is required for resolution.");
        }
        if (!SUBJECT_TYPE_USER.equals(subject.getType())) {
            throw new AuthZENClientException(AuthZENErrorCode.UNSUPPORTED_SUBJECT_TYPE,
                    "Only user subjects are supported by PIP at this stage.");
        }
        if (subject.getId() == null || subject.getId().isBlank()) {
            throw new AuthZENClientException(AuthZENErrorCode.INVALID_REQUEST,
                    "Subject id is required for subject resolution.");
        }

        String resolvedTenantDomain = resolveTenantDomain(tenantDomain);
        AbstractUserStoreManager userStoreManager = getUserStoreManager(resolvedTenantDomain);
        User resolvedUser = resolveUser(userStoreManager, subject.getId(), resolvedTenantDomain);
        String userId = resolveUserId(userStoreManager, resolvedUser, subject.getId());
        Map<String, String> claims = resolveClaims(userStoreManager, resolvedUser, userId);

        return new SubjectContext(userId, resolvedTenantDomain, Collections.emptyList(), Collections.emptyList(),
                claims, SUBJECT_TYPE_USER);
    }

    private String resolveTenantDomain(String tenantDomain) {

        if (tenantDomain == null || tenantDomain.isBlank()) {
            return SUPER_TENANT_DOMAIN_NAME;
        }
        return tenantDomain;
    }

    private AbstractUserStoreManager getUserStoreManager(String tenantDomain) throws AuthZENServerException {

        RealmService realmService = PIPDataHolder.getRealmService();
        if (realmService == null) {
            throw new AuthZENServerException(AuthZENErrorCode.INTERNAL_ERROR, "RealmService is not available.");
        }
        try {
            UserRealm userRealm = realmService.getTenantUserRealm(tenantIdResolver.resolve(tenantDomain));
            if (userRealm == null) {
                throw new AuthZENServerException(AuthZENErrorCode.INTERNAL_ERROR,
                        "User realm is not available for tenant: " + tenantDomain);
            }
            return (AbstractUserStoreManager) userRealm.getUserStoreManager();
        } catch (org.wso2.carbon.user.api.UserStoreException e) {
            throw new AuthZENServerException(AuthZENErrorCode.INTERNAL_ERROR,
                    "Error while resolving the user store manager for tenant: " + tenantDomain);
        }
    }

    private User resolveUser(AbstractUserStoreManager userStoreManager, String subjectId, String tenantDomain)
            throws AuthZENException {

        try {
            if (userStoreManager.isExistingUserWithID(subjectId)) {
                User user = userStoreManager.getUser(subjectId, null);
                if (user != null) {
                    return user;
                }
            }

            MultiAttributeLoginService multiAttributeLoginService = PIPDataHolder.getMultiAttributeLoginService();
            if (multiAttributeLoginService != null && multiAttributeLoginService.isEnabled(tenantDomain)) {
                ResolvedUserResult resolvedUserResult = multiAttributeLoginService.resolveUser(subjectId, tenantDomain);
                if (resolvedUserResult != null &&
                        UserResolvedStatus.SUCCESS.equals(resolvedUserResult.getResolvedStatus()) &&
                        resolvedUserResult.getUser() != null) {
                    return resolvedUserResult.getUser();
                }
            }

            Optional<User> resolvedByUsername = resolveUserByUsername(userStoreManager, subjectId);
            if (resolvedByUsername.isPresent()) {
                return resolvedByUsername.get();
            }
        } catch (UserStoreException e) {
            throw new AuthZENServerException(AuthZENErrorCode.INTERNAL_ERROR,
                    "Error while resolving subject: " + subjectId);
        }

        throw new AuthZENClientException(AuthZENErrorCode.MISSING_SUBJECT,
                "User subject not found for id: " + subjectId);
    }

    private String resolveUserId(AbstractUserStoreManager userStoreManager, User user, String subjectId)
            throws AuthZENServerException {

        if (user.getUserID() != null && !user.getUserID().isBlank()) {
            return user.getUserID();
        }
        try {
            return userStoreManager.getUserIDFromUserName(user.getUsername());
        } catch (UserStoreException e) {
            throw new AuthZENServerException(AuthZENErrorCode.INTERNAL_ERROR,
                    "Error while resolving user id for subject: " + subjectId);
        }
    }

    private Map<String, String> resolveClaims(AbstractUserStoreManager userStoreManager, User user, String userId)
            throws AuthZENServerException {

        try {
            if (userId != null && !userId.isBlank()) {
                return userStoreManager.getUserClaimValuesWithID(userId, REQUIRED_CLAIMS, null);
            }
            if (user.getUsername() != null && !user.getUsername().isBlank()) {
                return userStoreManager.getUserClaimValues(user.getUsername(), REQUIRED_CLAIMS, null);
            }
            return Collections.emptyMap();
        } catch (UserStoreException e) {
            throw new AuthZENServerException(AuthZENErrorCode.INTERNAL_ERROR,
                    "Error while resolving claims for user: " + userId);
        }
    }

    private Optional<User> resolveUserByUsername(AbstractUserStoreManager userStoreManager, String username)
            throws UserStoreException {

        AbstractUserStoreManager current = userStoreManager;
        while (current != null) {
            if (current.isExistingUser(username)) {
                return Optional.ofNullable(current.getUser(null, username));
            }
            current = (AbstractUserStoreManager) current.getSecondaryUserStoreManager();
        }
        return Optional.empty();
    }
}
