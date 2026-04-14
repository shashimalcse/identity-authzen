package org.wso2.carbon.identity.authzen.core.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ContextEntity {

    private Map<String, Object> properties = new LinkedHashMap<>();

    public ContextEntity() {
    }

    public ContextEntity(Map<String, Object> properties) {

        setProperties(properties);
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {

        return properties;
    }

    @JsonIgnore
    public void setProperties(Map<String, Object> properties) {

        this.properties = properties == null ? new LinkedHashMap<>() : new LinkedHashMap<>(properties);
    }

    @JsonAnySetter
    public void addProperty(String key, Object value) {

        properties.put(key, value);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof ContextEntity)) {
            return false;
        }
        ContextEntity that = (ContextEntity) o;
        return Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {

        return Objects.hash(properties);
    }

    @Override
    public String toString() {

        return "ContextEntity{" +
                "properties=" + properties +
                '}';
    }
}
