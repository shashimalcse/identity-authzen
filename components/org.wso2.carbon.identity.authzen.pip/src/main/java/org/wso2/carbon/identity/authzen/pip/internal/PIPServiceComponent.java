package org.wso2.carbon.identity.authzen.pip.internal;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.api.resource.mgt.APIResourceManager;
import org.wso2.carbon.identity.application.mgt.AuthorizedAPIManagementService;
import org.wso2.carbon.identity.authzen.pip.PIPService;
import org.wso2.carbon.identity.multi.attribute.login.mgt.MultiAttributeLoginService;
import org.wso2.carbon.user.core.service.RealmService;

@Component(
        name = "org.wso2.carbon.identity.authzen.pip.component",
        immediate = true
)
public class PIPServiceComponent {

    @Activate
    protected void activate(BundleContext bundleContext) {

        bundleContext.registerService(PIPService.class.getName(), new PIPServiceImpl(), null);
    }

    @Deactivate
    protected void deactivate() {

        PIPDataHolder.setRealmService(null);
        PIPDataHolder.setMultiAttributeLoginService(null);
        PIPDataHolder.setApiResourceManager(null);
        PIPDataHolder.setAuthorizedAPIManagementService(null);
    }

    @Reference(
            name = "org.wso2.carbon.user.core.service.RealmService",
            service = RealmService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetRealmService"
    )
    protected void setRealmService(RealmService realmService) {

        PIPDataHolder.setRealmService(realmService);
    }

    protected void unsetRealmService(RealmService realmService) {

        PIPDataHolder.setRealmService(null);
    }

    @Reference(
            name = "org.wso2.carbon.identity.multi.attribute.login.mgt.MultiAttributeLoginService",
            service = MultiAttributeLoginService.class,
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetMultiAttributeLoginService"
    )
    protected void setMultiAttributeLoginService(MultiAttributeLoginService multiAttributeLoginService) {

        PIPDataHolder.setMultiAttributeLoginService(multiAttributeLoginService);
    }

    protected void unsetMultiAttributeLoginService(MultiAttributeLoginService multiAttributeLoginService) {

        PIPDataHolder.setMultiAttributeLoginService(null);
    }

    @Reference(
            name = "org.wso2.carbon.identity.api.resource.mgt.APIResourceManager",
            service = APIResourceManager.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetAPIResourceManager"
    )
    protected void setAPIResourceManager(APIResourceManager apiResourceManager) {

        PIPDataHolder.setApiResourceManager(apiResourceManager);
    }

    protected void unsetAPIResourceManager(APIResourceManager apiResourceManager) {

        PIPDataHolder.setApiResourceManager(null);
    }

    @Reference(
            name = "org.wso2.carbon.identity.application.mgt.AuthorizedAPIManagementService",
            service = AuthorizedAPIManagementService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetAuthorizedAPIManagementService"
    )
    protected void setAuthorizedAPIManagementService(
            AuthorizedAPIManagementService authorizedAPIManagementService) {

        PIPDataHolder.setAuthorizedAPIManagementService(authorizedAPIManagementService);
    }

    protected void unsetAuthorizedAPIManagementService(
            AuthorizedAPIManagementService authorizedAPIManagementService) {

        PIPDataHolder.setAuthorizedAPIManagementService(null);
    }
}
