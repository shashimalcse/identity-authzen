package org.wso2.carbon.identity.authzen.pip.internal;

import org.wso2.carbon.identity.authzen.core.exception.AuthZENException;
import org.wso2.carbon.identity.authzen.core.model.ResourceEntity;
import org.wso2.carbon.identity.authzen.core.model.SubjectEntity;
import org.wso2.carbon.identity.authzen.pip.PIPService;
import org.wso2.carbon.identity.authzen.pip.ResourceContext;
import org.wso2.carbon.identity.authzen.pip.SubjectContext;

public class PIPServiceImpl implements PIPService {

    private final DefaultSubjectResolver subjectResolver;
    private final DefaultResourceResolver resourceResolver;

    public PIPServiceImpl() {

        this(new DefaultSubjectResolver(), new DefaultResourceResolver());
    }

    PIPServiceImpl(DefaultSubjectResolver subjectResolver, DefaultResourceResolver resourceResolver) {

        this.subjectResolver = subjectResolver;
        this.resourceResolver = resourceResolver;
    }

    @Override
    public SubjectContext resolveSubject(SubjectEntity subject, String tenantDomain) throws AuthZENException {

        return subjectResolver.resolve(subject, tenantDomain);
    }

    @Override
    public ResourceContext resolveResource(ResourceEntity resource, String tenantDomain) throws AuthZENException {

        return resourceResolver.resolve(resource, tenantDomain);
    }
}
