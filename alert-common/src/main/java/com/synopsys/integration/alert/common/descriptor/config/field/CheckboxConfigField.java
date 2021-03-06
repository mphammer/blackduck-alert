/**
 * alert-common
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.alert.common.descriptor.config.field;

import java.util.Collection;
import java.util.List;

import com.synopsys.integration.alert.common.rest.model.FieldModel;
import com.synopsys.integration.alert.common.rest.model.FieldValueModel;
import com.synopsys.integration.alert.common.enumeration.FieldGroup;
import com.synopsys.integration.alert.common.enumeration.FieldType;

public class CheckboxConfigField extends ConfigField {
    public CheckboxConfigField(final String key, final String label, final boolean required, final FieldGroup group) {
        super(key, label, FieldType.CHECKBOX_INPUT.getFieldTypeName(), required, false, group);
    }

    public CheckboxConfigField(final String key, final String label, final boolean required, final FieldGroup group, final ConfigValidationFunction validationFunction) {
        super(key, label, FieldType.CHECKBOX_INPUT.getFieldTypeName(), required, false, group, validationFunction);
    }

    public CheckboxConfigField(final String key, final String label, final boolean required) {
        super(key, label, FieldType.CHECKBOX_INPUT.getFieldTypeName(), required, false);
    }

    public CheckboxConfigField(final String key, final String label, final boolean required, final ConfigValidationFunction validationFunction) {
        super(key, label, FieldType.CHECKBOX_INPUT.getFieldTypeName(), required, false, validationFunction);
    }

    public static CheckboxConfigField create(final String key, final String label) {
        return new CheckboxConfigField(key, label, false);
    }

    public static CheckboxConfigField create(final String key, final String label, final ConfigValidationFunction validationFunction) {
        return new CheckboxConfigField(key, label, false, validationFunction);
    }

    public static CheckboxConfigField createRequired(final String key, final String label) {
        return new CheckboxConfigField(key, label, true);
    }

    public static CheckboxConfigField createRequired(final String key, final String label, final ConfigValidationFunction validationFunction) {
        return new CheckboxConfigField(key, label, true, validationFunction);
    }

    public static CheckboxConfigField createGrouped(final String key, final String label, final FieldGroup group) {
        return new CheckboxConfigField(key, label, false, group);
    }

    public static CheckboxConfigField createGrouped(final String key, final String label, final FieldGroup group, final ConfigValidationFunction validationFunction) {
        return new CheckboxConfigField(key, label, false, group, validationFunction);
    }

    @Override
    public Collection<String> validate(final FieldValueModel fieldValueModel, final FieldModel fieldModel) {
        final List<ConfigValidationFunction> validationFunctions;
        if (null != getValidationFunction()) {
            validationFunctions = List.of(this::validateValueIsBoolean, getValidationFunction());
        } else {
            validationFunctions = List.of(this::validateValueIsBoolean);
        }
        return validate(fieldValueModel, fieldModel, validationFunctions);
    }

    private Collection<String> validateValueIsBoolean(final FieldValueModel fieldToValidate, final FieldModel fieldModel) {
        if (fieldToValidate.hasValues()) {
            final String value = fieldToValidate.getValue().orElse("");
            final boolean trueTextPresent = Boolean.TRUE.toString().equalsIgnoreCase(value);
            final boolean falseTextPresent = Boolean.FALSE.toString().equalsIgnoreCase(value);
            if (!trueTextPresent && !falseTextPresent) {
                List.of("Not a boolean value 'true' or 'false'");
            }
        }
        return List.of();
    }
}
