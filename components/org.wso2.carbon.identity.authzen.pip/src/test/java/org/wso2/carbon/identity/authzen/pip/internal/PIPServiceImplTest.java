package org.wso2.carbon.identity.authzen.pip.internal;

import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.api.resource.mgt.APIResourceManager;
import org.wso2.carbon.identity.application.common.model.APIResource;
import org.wso2.carbon.identity.application.common.model.Scope;
import org.wso2.carbon.identity.authzen.core.exception.AuthZENClientException;
import org.wso2.carbon.identity.authzen.core.model.ResourceEntity;
import org.wso2.carbon.identity.authzen.core.model.SubjectEntity;
import org.wso2.carbon.identity.multi.attribute.login.mgt.MultiAttributeLoginService;
import org.wso2.carbon.identity.multi.attribute.login.mgt.ResolvedUserResult;
import org.wso2.carbon.identity.multi.attribute.login.mgt.ResolvedUserResult.UserResolvedStatus;
import org.wso2.carbon.identity.authzen.pip.PIPContext;
import org.wso2.carbon.identity.authzen.pip.PIPService;
import org.wso2.carbon.identity.authzen.pip.ResourceContext;
import org.wso2.carbon.identity.authzen.pip.SubjectContext;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.core.common.AbstractUserStoreManager;
import org.wso2.carbon.user.core.common.User;
import org.wso2.carbon.user.core.service.RealmService;

import java.util.List;
import java.util.Map;

public class PIPServiceImplTest {

    @AfterMethod
    public void tearDown() {

        PIPDataHolder.setRealmService(null);
        PIPDataHolder.setMultiAttributeLoginService(null);
        PIPDataHolder.setApiResourceManager(null);
    }

    @Test
    public void shouldResolveSubjectByUserId() throws Exception {

        RealmService realmService = Mockito.mock(RealmService.class);
        UserRealm userRealm = Mockito.mock(UserRealm.class);
        AbstractUserStoreManager userStoreManager = Mockito.mock(AbstractUserStoreManager.class);
        User user = Mockito.mock(User.class);
        PIPDataHolder.setRealmService(realmService);

        Mockito.when(realmService.getTenantUserRealm(1)).thenReturn(userRealm);
        Mockito.when(userRealm.getUserStoreManager()).thenReturn(userStoreManager);
        Mockito.when(userStoreManager.isExistingUserWithID("user-1")).thenReturn(true);
        Mockito.when(userStoreManager.getUser("user-1", null)).thenReturn(user);
        Mockito.when(user.getUserID()).thenReturn("user-1");
        Mockito.when(userStoreManager.getUserClaimValuesWithID(Mockito.eq("user-1"), Mockito.any(String[].class),
                Mockito.isNull())).thenReturn(Map.of("http://wso2.org/claims/emailaddress", "alice@example.com"));

        PIPService pipService = new PIPServiceImpl(new DefaultSubjectResolver(tenantDomain -> 1),
                new DefaultResourceResolver());
        SubjectContext subjectContext = pipService.resolveSubject(new SubjectEntity("user", "user-1", null),
                "carbon.super");

        Assert.assertEquals(subjectContext.getUserId(), "user-1");
        Assert.assertEquals(subjectContext.getTenantDomain(), "carbon.super");
        Assert.assertEquals(subjectContext.getSubjectType(), "user");
        Assert.assertTrue(subjectContext.getRoles().isEmpty());
        Assert.assertEquals(subjectContext.getClaims().get("http://wso2.org/claims/emailaddress"),
                "alice@example.com");
    }

    @Test
    public void shouldResolveSubjectWithMultiAttributeLoginService() throws Exception {

        RealmService realmService = Mockito.mock(RealmService.class);
        UserRealm userRealm = Mockito.mock(UserRealm.class);
        AbstractUserStoreManager userStoreManager = Mockito.mock(AbstractUserStoreManager.class);
        MultiAttributeLoginService multiAttributeLoginService = Mockito.mock(MultiAttributeLoginService.class);
        User user = Mockito.mock(User.class);
        PIPDataHolder.setRealmService(realmService);
        PIPDataHolder.setMultiAttributeLoginService(multiAttributeLoginService);

        Mockito.when(realmService.getTenantUserRealm(1)).thenReturn(userRealm);
        Mockito.when(userRealm.getUserStoreManager()).thenReturn(userStoreManager);
        Mockito.when(userStoreManager.isExistingUserWithID("alice@example.com")).thenReturn(false);
        Mockito.when(multiAttributeLoginService.isEnabled("carbon.super")).thenReturn(true);
        Mockito.when(user.getUserID()).thenReturn("user-2");
        ResolvedUserResult resolvedUserResult = new ResolvedUserResult(UserResolvedStatus.SUCCESS);
        resolvedUserResult.setUser(user);
        Mockito.when(multiAttributeLoginService.resolveUser("alice@example.com", "carbon.super"))
                .thenReturn(resolvedUserResult);
        Mockito.when(userStoreManager.getUserClaimValuesWithID(Mockito.eq("user-2"), Mockito.any(String[].class),
                Mockito.isNull())).thenReturn(Map.of("http://wso2.org/claims/mobile", "0770000000"));

        PIPService pipService = new PIPServiceImpl(new DefaultSubjectResolver(tenantDomain -> 1),
                new DefaultResourceResolver());
        SubjectContext subjectContext = pipService.resolveSubject(
                new SubjectEntity("user", "alice@example.com", null), "carbon.super");

        Assert.assertEquals(subjectContext.getUserId(), "user-2");
        Assert.assertEquals(subjectContext.getClaims().get("http://wso2.org/claims/mobile"), "0770000000");
    }

    @Test
    public void shouldThrowClientExceptionWhenSubjectCannotBeResolved() throws Exception {

        RealmService realmService = Mockito.mock(RealmService.class);
        UserRealm userRealm = Mockito.mock(UserRealm.class);
        AbstractUserStoreManager userStoreManager = Mockito.mock(AbstractUserStoreManager.class);
        PIPDataHolder.setRealmService(realmService);

        Mockito.when(realmService.getTenantUserRealm(1)).thenReturn(userRealm);
        Mockito.when(userRealm.getUserStoreManager()).thenReturn(userStoreManager);
        Mockito.when(userStoreManager.isExistingUserWithID("unknown")).thenReturn(false);
        Mockito.when(userStoreManager.isExistingUser("unknown")).thenReturn(false);
        Mockito.when(userStoreManager.getSecondaryUserStoreManager()).thenReturn(null);

        PIPService pipService = new PIPServiceImpl(new DefaultSubjectResolver(tenantDomain -> 1),
                new DefaultResourceResolver());

        Assert.expectThrows(AuthZENClientException.class,
                () -> pipService.resolveSubject(new SubjectEntity("user", "unknown", null), "carbon.super"));
    }

    @Test
    public void shouldResolveResourceByIdentifier() throws Exception {

        APIResourceManager apiResourceManager = Mockito.mock(APIResourceManager.class);
        APIResource apiResource = Mockito.mock(APIResource.class);
        Scope scope = Mockito.mock(Scope.class);
        PIPDataHolder.setApiResourceManager(apiResourceManager);

        Mockito.when(apiResourceManager.getAPIResourceByIdentifier("/api/courses", "carbon.super"))
                .thenReturn(apiResource);
        Mockito.when(apiResource.getIdentifier()).thenReturn("/api/courses");
        Mockito.when(apiResource.getScopes()).thenReturn(List.of(scope));
        Mockito.when(scope.getName()).thenReturn("courses:read");

        PIPService pipService = new PIPServiceImpl();
        ResourceContext resourceContext = pipService.resolveResource(
                new ResourceEntity("api_resource", "/api/courses", null), "carbon.super");

        Assert.assertEquals(resourceContext.getResourceId(), "/api/courses");
        Assert.assertEquals(resourceContext.getResourceType(), "api_resource");
        Assert.assertEquals(resourceContext.getScopes(), List.of("courses:read"));
    }

    @Test
    public void shouldSupportPipContextPojoBehavior() {

        SubjectContext subjectContext = new SubjectContext("user-1", "carbon.super", List.of(), List.of(),
                Map.of("claim", "value"), "user");
        ResourceContext resourceContext = new ResourceContext("/api/courses", "api_resource",
                List.of("courses:read"), null);
        PIPContext first = new PIPContext(subjectContext, resourceContext);
        PIPContext second = new PIPContext();
        second.setSubjectContext(subjectContext);
        second.setResourceContext(resourceContext);

        Assert.assertEquals(first, second);
        Assert.assertEquals(first.hashCode(), second.hashCode());
        Assert.assertTrue(first.toString().contains("subjectContext"));
    }
}
