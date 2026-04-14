package org.wso2.carbon.identity.authzen.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubjectEntity {

    private String type;

    private String id;

    private Map<String, Object> properties;

    public SubjectEntity() {
    }

    public SubjectEntity(String type, String id, Map<String, Object> properties) {

        this.type = type;
        this.id = id;
        this.properties = properties;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
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
        if (!(o instanceof SubjectEntity)) {
            return false;
        }
        SubjectEntity that = (SubjectEntity) o;
        return Objects.equals(type, that.type) &&
                Objects.equals(id, that.id) &&
                Objects.equals(properties, that.properties);
    }

    @Override
    public int hashCode() {

        return Objects.hash(type, id, properties);
    }

    @Override
    public String toString() {

        return "SubjectEntity{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", properties=" + properties +
                '}';
    }
}
