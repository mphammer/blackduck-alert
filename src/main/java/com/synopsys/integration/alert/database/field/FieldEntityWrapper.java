package com.synopsys.integration.alert.database.field;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.EnumUtils;

import com.synopsys.integration.alert.common.enumeration.DescriptorType;

public class FieldEntityWrapper {
    private final Map<String, FieldValuesEntity> fieldValuesMapping;
    private final GroupingEntity groupingEntity;

    public FieldEntityWrapper(final GroupingEntity groupingEntity, final Collection<FieldValuesEntity> fieldValues) {
        this.groupingEntity = groupingEntity;
        fieldValuesMapping = fieldValues.stream().collect(Collectors.toMap(FieldValuesEntity::getKey, Function.identity()));
    }

    public String getDescriptorName() {
        return groupingEntity.getDescriptorName();
    }

    public DescriptorType getDescriptorType() {
        return EnumUtils.getEnum(DescriptorType.class, groupingEntity.getDescriptorType());
    }

    public Long getGroupingId() {
        return groupingEntity.getId();
    }

    public Map<String, FieldValuesEntity> getFieldValuesMapping() {
        return fieldValuesMapping;
    }

    public FieldValuesEntity getFieldValuesEntity(final String key) {
        return getFieldValuesMapping().get(key);
    }

    public String getStringValue(final String key) {
        final FieldValuesEntity fieldValuesEntity = getFieldValuesEntity(key);
        return fieldValuesEntity.getValue();
    }
}
