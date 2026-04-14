package org.wso2.carbon.identity.authzen.evalution.internal;

import org.osgi.framework.BundleContext;

public final class EvalutionDataHolder {

    private static BundleContext bundleContext;

    private EvalutionDataHolder() {
    }

    public static BundleContext getBundleContext() {

        return bundleContext;
    }

    public static void setBundleContext(BundleContext bundleContext) {

        EvalutionDataHolder.bundleContext = bundleContext;
    }
}
