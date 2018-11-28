package com.synopsys.integration.alert.common.field;

import com.google.gson.Gson;
import com.synopsys.integration.alert.database.field.FieldGroupingWrapper;

public class BasicFields {
    private final FieldGroupingWrapper fieldGroupingWrapper;
    private final Gson gson;

    public BasicFields(final FieldGroupingWrapper fieldGroupingWrapper, final Gson gson) {
        this.fieldGroupingWrapper = fieldGroupingWrapper;
        this.gson = gson;
    }

    public FieldGroupingWrapper getFieldGroupingWrapper() {
        return fieldGroupingWrapper;
    }

    public String getStringValue(final String key) {
        return getJsonElement(key).getAsString();
    }

}
