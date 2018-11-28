package com.synopsys.integration.alert.database.field;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;

import com.synopsys.integration.alert.common.enumeration.DescriptorType;

public class FieldGroupingWrapper {
    private final Long groupingId;
    private final String descriptorName;
    private final DescriptorType descriptorType;
    private Map<String, FieldValue> fieldMapping;

    public FieldGroupingWrapper(final GroupingEntity groupingEntity, final Collection<FieldValuesEntity> fieldValues) {
        groupingId = groupingEntity.getId();
        descriptorName = groupingEntity.getDescriptorName();
        descriptorType = EnumUtils.getEnum(DescriptorType.class, groupingEntity.getDescriptorType());
    }

}
