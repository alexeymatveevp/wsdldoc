package com.tsystems.wsdldoc;

import java.util.List;

/**
 * By: Alexey Matveev
 * Date: 25.08.2016
 * Time: 18:13
 */
public class TypeData {

    private String name;

    private String schema;

    private String description;

    private List<String> superTypes;

    // 0 for complex, 1 for simple
    private int type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchema() {
        return schema;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSuperTypes() {
        return superTypes;
    }

    public void setSuperTypes(List<String> superTypes) {
        this.superTypes = superTypes;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
