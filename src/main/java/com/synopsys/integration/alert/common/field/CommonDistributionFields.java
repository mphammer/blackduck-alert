package com.synopsys.integration.alert.common.field;

import org.apache.commons.lang3.EnumUtils;

import com.synopsys.integration.alert.common.enumeration.FormatType;
import com.synopsys.integration.alert.common.enumeration.FrequencyType;
import com.synopsys.integration.alert.database.field.FieldEntityWrapper;

public class CommonDistributionFields {
    public static String KEY_NAME = "channel.name";
    public static String KEY_PROVIDER_NAME = "channel.provider.name";
    public static String KEY_DIGEST_TYPE = "channel.digest.type";
    public static String KEY_PROJECT_NAME_PATTERN = "channel.project.name.pattern";
    public static String KEY_FORMAT_TYPE = "channel.format.type";

    private final FieldEntityWrapper fieldEntityWrapper;

    public CommonDistributionFields(final FieldEntityWrapper fieldEntityWrapper) {
        this.fieldEntityWrapper = fieldEntityWrapper;
    }

    public FieldEntityWrapper getFieldEntityWrapper() {
        return fieldEntityWrapper;
    }

    public Long getGroupingId() {
        return fieldEntityWrapper.getGroupingId();
    }

    public String getName() {
        return getStringValue(KEY_NAME);
    }

    public String getDistributionType() {
        return fieldEntityWrapper.getDescriptorName();
    }

    public String getProviderName() {
        return getStringValue(KEY_PROVIDER_NAME);
    }

    public FrequencyType getDigestType() {
        final String frequencyType = getStringValue(KEY_DIGEST_TYPE);
        return EnumUtils.getEnum(FrequencyType.class, frequencyType);
    }

    public String getProjectNamePattern() {
        return getStringValue(KEY_PROJECT_NAME_PATTERN);
    }

    public FormatType getFormatType() {
        final String formatType = getStringValue(KEY_FORMAT_TYPE);
        return EnumUtils.getEnum(FormatType.class, formatType);
    }

    public String getStringValue(String key) {
        return fieldEntityWrapper.getStringValue(key);
    }
}
