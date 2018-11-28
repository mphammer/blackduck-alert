package com.synopsys.integration.alert.database.field;

public class FieldValue {
    private Long fieldId;
    private String value;
    private Boolean secure;

    public FieldValue(final Long fieldId, final String value, final Boolean secure) {
        this.fieldId = fieldId;
        this.value = value;
        this.secure = secure;
    }

    public Long getFieldId() {
        return fieldId;
    }

    public void setFieldId(final Long fieldId) {
        this.fieldId = fieldId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public Boolean getSecure() {
        return secure;
    }

    public void setSecure(final Boolean secure) {
        this.secure = secure;
    }
}
