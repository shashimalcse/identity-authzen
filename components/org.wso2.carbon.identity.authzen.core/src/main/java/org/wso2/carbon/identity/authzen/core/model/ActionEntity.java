package org.wso2.carbon.identity.authzen.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionEntity {

    private String name;

    private Map<String, Object> properties;

    public ActionEntity() {
    }

    public ActionEntity(String name, Map<String, Object> properties) {

        this.name = name;
        this.properties = properties;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public Map<String, Object> getProperties() {

        return properties;
    }

    public void setProperties(Map<String, Object> properties) {

        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (!(o instanceof ActionEntity)) {
            return false;
        }
        ActionEntity that = (ActionEntity) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, properties);
    }

    @Override
    public String toString() {

        return "ActionEntity{" +
                "name='" + name + '\'' +
                ", properties=" + properties +
                '}';
    }
}
