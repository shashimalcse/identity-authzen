package org.wso2.carbon.identity.authzen.pip;

import org.wso2.carbon.identity.authzen.core.exception.AuthZENException;
import org.wso2.carbon.identity.authzen.core.model.ResourceEntity;
import org.wso2.carbon.identity.authzen.core.model.SubjectEntity;

public interface PIPService {

    SubjectContext resolveSubject(SubjectEntity subject, String tenantDomain) throws AuthZENException;

    ResourceContext resolveResource(ResourceEntity resource, String tenantDomain) throws AuthZENException;
}
