package org.wso2.carbon.identity.authzen.core.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class AuthZENModelSerializationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldRoundTripEvaluationRequest() throws Exception {

        Map<String, Object> subjectProperties = new LinkedHashMap<>();
        subjectProperties.put("tenant", "carbon.super");

        Map<String, Object> resourceProperties = new LinkedHashMap<>();
        resourceProperties.put("app_id", "course-management-app");

        Map<String, Object> contextProperties = new LinkedHashMap<>();
        contextProperties.put("time", "2026-03-17T14:00:00Z");
        contextProperties.put("ip_address", "10.0.1.50");

        EvaluationRequest request = new EvaluationRequest(
                new SubjectEntity("user", "alice", subjectProperties),
                new ActionEntity("courses:read", null),
                new ResourceEntity("api_resource", "/api/courses", resourceProperties),
                new ContextEntity(contextProperties));

        String json = objectMapper.writeValueAsString(request);
        EvaluationRequest roundTripped = objectMapper.readValue(json, EvaluationRequest.class);

        Assert.assertEquals(roundTripped, request);
        Assert.assertEquals(roundTripped.getContext().getProperties().get("ip_address"), "10.0.1.50");
    }

    @Test
    public void shouldPreserveArbitraryContextMembers() throws Exception {

        String json = "{\"time\":\"2026-03-17T14:00:00Z\",\"device\":\"mobile\"}";

        ContextEntity context = objectMapper.readValue(json, ContextEntity.class);

        Assert.assertEquals(context.getProperties().get("time"), "2026-03-17T14:00:00Z");
        Assert.assertEquals(context.getProperties().get("device"), "mobile");
        Assert.assertTrue(objectMapper.writeValueAsString(context).contains("\"device\":\"mobile\""));
    }

    @Test
    public void shouldRoundTripEvaluationResponse() throws Exception {

        Map<String, Object> responseContext = new LinkedHashMap<>();
        responseContext.put("reason_admin", "Allowed");

        EvaluationResponse response = new EvaluationResponse(true, responseContext);
        String json = objectMapper.writeValueAsString(response);
        EvaluationResponse roundTripped = objectMapper.readValue(json, EvaluationResponse.class);

        Assert.assertEquals(roundTripped, response);
    }

    @Test
    public void shouldImplementEqualityAndHashCodeForEntities() {

        SubjectEntity first = new SubjectEntity("user", "alice", Map.of("key", "value"));
        SubjectEntity second = new SubjectEntity("user", "alice", Map.of("key", "value"));

        Assert.assertEquals(first, second);
        Assert.assertEquals(first.hashCode(), second.hashCode());
        Assert.assertTrue(first.toString().contains("alice"));
    }
}
