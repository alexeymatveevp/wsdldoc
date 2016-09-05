package com.tsystems.wsdldoc;

/**
 * By: Alexey Matveev
 * Date: 25.08.2016
 * Time: 19:45
 */
public class ElementData {

    private String name;

    private String typeName;

    private boolean nativeType;

    private Integer minOccurs;

    private Integer maxOccurs;

    private String description;

    private String schema;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Integer getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(Integer minOccurs) {
        this.minOccurs = minOccurs;
    }

    public Integer getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(Integer maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public boolean isNativeType() {
        return nativeType;
    }

    public void setNativeType(boolean nativeType) {
        this.nativeType = nativeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
