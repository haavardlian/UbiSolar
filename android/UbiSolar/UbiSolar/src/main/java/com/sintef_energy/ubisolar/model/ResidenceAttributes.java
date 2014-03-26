package com.sintef_energy.ubisolar.model;

/**
 * Created by baier on 3/26/14.
 */
public class ResidenceAttributes {

    private String residenceAttributeLabel;
    private int value;

    public ResidenceAttributes(String residenceAttributeLabel) {
        this.residenceAttributeLabel = residenceAttributeLabel;
    }

    public String getResidenceAttributeLabel() {
        return residenceAttributeLabel;
    }

    public void setResidenceAttributeLabel(String residenceAttributeLabel) {
        this.residenceAttributeLabel = residenceAttributeLabel;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
