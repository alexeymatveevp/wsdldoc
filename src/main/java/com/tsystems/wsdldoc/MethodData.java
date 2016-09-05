package com.tsystems.wsdldoc;

/**
 * By: Alexey Matveev
 * Date: 25.08.2016
 * Time: 17:20
 */
public class MethodData {

    private String name;

    private ComplexTypeData request;

    private ComplexTypeData response;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ComplexTypeData getRequest() {
        return request;
    }

    public void setRequest(ComplexTypeData request) {
        this.request = request;
    }

    public ComplexTypeData getResponse() {
        return response;
    }

    public void setResponse(ComplexTypeData response) {
        this.response = response;
    }
}
