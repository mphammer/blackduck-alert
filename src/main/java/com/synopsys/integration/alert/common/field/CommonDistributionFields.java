package com.synopsys.integration.alert.common.field;

import java.util.List;

import org.apache.commons.lang3.EnumUtils;

import com.synopsys.integration.alert.common.enumeration.FormatType;
import com.synopsys.integration.alert.common.enumeration.FrequencyType;
import com.synopsys.integration.alert.database.field.FieldGroupingWrapper;

public class CommonDistributionFields extends BasicFields {
    public static String KEY_NAME = "distribution.channel.name";
    public static String KEY_PROVIDER_NAME = "distribution.channel.provider.name";
    public static String KEY_DIGEST_TYPE = "distribution.channel.digest.type";
    public static String KEY_PROJECT_NAME_PATTERN = "distribution.channel.project.name.pattern";
    public static String KEY_FORMAT_TYPE = "distribution.channel.format.type";
    public static String KEY_CONFIGURED_PROJECTS = "distribution.channel.configured.projects";
    public static String KEY_NOTIFICATION_TYPES = "distribution.channel.notification.types";

    public CommonDistributionFields(final FieldGroupingWrapper fieldGroupingWrapper) {
        super(fieldGroupingWrapper);
    }

    public Long getGroupingId() {
        return getFieldGroupingWrapper().getGroupingId();
    }

    public String getName() {
        return getStringValue(KEY_NAME);
    }

    public String getDistributionType() {
        return getFieldGroupingWrapper().getDescriptorName();
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

    public List<String> getNotificationTypes() {

    }

    public List<String> getConfiguredProjects() {}

}
