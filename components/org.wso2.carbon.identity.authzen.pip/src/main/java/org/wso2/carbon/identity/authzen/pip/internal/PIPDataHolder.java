package org.wso2.carbon.identity.authzen.pip.internal;

import org.wso2.carbon.identity.api.resource.mgt.APIResourceManager;
import org.wso2.carbon.identity.multi.attribute.login.mgt.MultiAttributeLoginService;
import org.wso2.carbon.user.core.service.RealmService;

public final class PIPDataHolder {

    private static RealmService realmService;
    private static MultiAttributeLoginService multiAttributeLoginService;
    private static APIResourceManager apiResourceManager;

    private PIPDataHolder() {
    }

    public static RealmService getRealmService() {

        return realmService;
    }

    public static void setRealmService(RealmService realmService) {

        PIPDataHolder.realmService = realmService;
    }

    public static MultiAttributeLoginService getMultiAttributeLoginService() {

        return multiAttributeLoginService;
    }

    public static void setMultiAttributeLoginService(MultiAttributeLoginService multiAttributeLoginService) {

        PIPDataHolder.multiAttributeLoginService = multiAttributeLoginService;
    }

    public static APIResourceManager getApiResourceManager() {

        return apiResourceManager;
    }

    public static void setApiResourceManager(APIResourceManager apiResourceManager) {

        PIPDataHolder.apiResourceManager = apiResourceManager;
    }
}
