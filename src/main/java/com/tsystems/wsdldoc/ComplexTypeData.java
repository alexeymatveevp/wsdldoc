package com.tsystems.wsdldoc;

import java.util.List;

/**
 * By: Alexey Matveev
 * Date: 25.08.2016
 * Time: 17:21
 */
public class ComplexTypeData extends TypeData {

    private List<ElementData> sequence;

    public ComplexTypeData() {
        setType(0);
    }

    public List<ElementData> getSequence() {
        return sequence;
    }

    public void setSequence(List<ElementData> sequence) {
        this.sequence = sequence;
    }
}
