package org.wso2.carbon.identity.authzen.core;

public final class AuthZENConstants {

    public static final String ACCESS_EVALUATION_PATH = "/access/v1/evaluation";
    public static final String ACCESS_EVALUATIONS_PATH = "/access/v1/evaluations";
    public static final String SEARCH_SUBJECT_PATH = "/access/v1/search/subject";
    public static final String SEARCH_RESOURCE_PATH = "/access/v1/search/resource";
    public static final String SEARCH_ACTION_PATH = "/access/v1/search/action";
    public static final String PDP_METADATA_PATH = "/.well-known/authzen-configuration";

    public static final String SUBJECT_TYPE_USER = "user";
    public static final String SUBJECT_TYPE_APPLICATION = "application";

    public static final String RESOURCE_TYPE_API_RESOURCE = "api_resource";
    public static final String RESOURCE_TYPE_APPLICATION = "application";

    public static final String HEADER_X_REQUEST_ID = "X-Request-ID";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    public static final String SCOPE_INTERNAL_AUTHZEN_EVALUATE = "internal_authzen_evaluate";
    public static final String SCOPE_INTERNAL_AUTHZEN_SEARCH = "internal_authzen_search";

    private AuthZENConstants() {
    }
}
