package com.synopsys.integration.alert.workflow.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

import com.synopsys.integration.alert.common.configuration.CommonDistributionConfiguration;
import com.synopsys.integration.alert.common.descriptor.ProviderDescriptor;
import com.synopsys.integration.alert.common.enumeration.FrequencyType;
import com.synopsys.integration.alert.common.model.AggregateMessageContent;
import com.synopsys.integration.alert.database.api.configuration.ConfigurationAccessor.ConfigurationModel;
import com.synopsys.integration.alert.database.channel.JobConfigReader;
import com.synopsys.integration.alert.database.entity.NotificationContent;
import com.synopsys.integration.alert.provider.blackduck.BlackDuckProvider;
import com.synopsys.integration.alert.util.AlertIntegrationTest;
import com.synopsys.integration.alert.workflow.filter.NotificationFilter;
import com.synopsys.integration.blackduck.api.generated.enumeration.NotificationType;

public class MessageContentAggregatorTest extends AlertIntegrationTest {

    @Autowired
    private List<ProviderDescriptor> providerDescriptors;
    @Autowired
    private JobConfigReader jobConfigReader;
    @Autowired
    private NotificationFilter notificationFilter;

    @Test
    public void testNoJobProcessing() throws Exception {
        final FrequencyType frequencyType = FrequencyType.REAL_TIME;
        final String policyContent = getNotificationContentFromFile("json/policyRuleClearedNotification.json");
        final NotificationContent policyNotification = createNotification(BlackDuckProvider.COMPONENT_NAME, policyContent, NotificationType.RULE_VIOLATION_CLEARED);

        final String vulnerabilityContent = getNotificationContentFromFile("json/vulnerabilityTest.json");
        final NotificationContent vulnerabilityNotification = createNotification(BlackDuckProvider.COMPONENT_NAME, vulnerabilityContent, NotificationType.VULNERABILITY);

        final List<NotificationContent> notificationContentList = Arrays.asList(policyNotification, vulnerabilityNotification);
        final MessageContentAggregator messageContentAggregator = new MessageContentAggregator(jobConfigReader, providerDescriptors, notificationFilter);
        final Map<CommonDistributionConfiguration, List<AggregateMessageContent>> topicContentMap = messageContentAggregator.processNotifications(frequencyType, notificationContentList);

        assertTrue(topicContentMap.isEmpty());
    }

    @Test
    public void testJobProcessing() throws Exception {
        final FrequencyType frequencyType = FrequencyType.REAL_TIME;
        final String policyContent = getNotificationContentFromFile("json/policyRuleClearedNotification.json");
        final NotificationContent policyNotification = createNotification(BlackDuckProvider.COMPONENT_NAME, policyContent, NotificationType.RULE_VIOLATION_CLEARED);

        final String vulnerabilityContent = getNotificationContentFromFile("json/vulnerabilityTest.json");
        final NotificationContent vulnerabilityNotification = createNotification(BlackDuckProvider.COMPONENT_NAME, vulnerabilityContent, NotificationType.VULNERABILITY);

        final List<NotificationContent> notificationContentList = Arrays.asList(policyNotification, vulnerabilityNotification);

        final List<String> projectList = Arrays.asList("example", "alert-test-project", "alert-test-project-2");
        final List<String> notificationTypesLIst = Arrays.asList(NotificationType.RULE_VIOLATION_CLEARED.name(), NotificationType.VULNERABILITY.name());
        final CommonDistributionConfiguration jobConfig = createCommonDistributionConfiguration();
        final JobConfigReader spiedReader = Mockito.spy(jobConfigReader);
        Mockito.doReturn(Arrays.asList(jobConfig)).when(spiedReader).getPopulatedConfigs();

        final MessageContentAggregator messageContentAggregator = new MessageContentAggregator(spiedReader, providerDescriptors, notificationFilter);
        final Map<CommonDistributionConfiguration, List<AggregateMessageContent>> topicContentMap = messageContentAggregator.processNotifications(frequencyType, notificationContentList);

        assertFalse(topicContentMap.isEmpty());
        assertEquals(1, topicContentMap.size());
        assertTrue(topicContentMap.containsKey(jobConfig));
        assertEquals(4, topicContentMap.get(jobConfig).size());
    }

    @Test
    public void testJobProcessingFrequencyMismatch() throws Exception {
        final FrequencyType frequencyType = FrequencyType.REAL_TIME;
        final String policyContent = getNotificationContentFromFile("json/policyRuleClearedNotification.json");
        final NotificationContent policyNotification = createNotification(BlackDuckProvider.COMPONENT_NAME, policyContent, NotificationType.RULE_VIOLATION_CLEARED);

        final String vulnerabilityContent = getNotificationContentFromFile("json/vulnerabilityTest.json");
        final NotificationContent vulnerabilityNotification = createNotification(BlackDuckProvider.COMPONENT_NAME, vulnerabilityContent, NotificationType.VULNERABILITY);

        final List<NotificationContent> notificationContentList = Arrays.asList(policyNotification, vulnerabilityNotification);

        final List<String> projectList = Arrays.asList("example", "alert-test-project", "alert-test-project-2");
        final List<String> notificationTypesLIst = Arrays.asList(NotificationType.RULE_VIOLATION_CLEARED.name(), NotificationType.VULNERABILITY.name());
        final CommonDistributionConfiguration jobConfig = createCommonDistributionConfiguration();

        final JobConfigReader spiedReader = Mockito.spy(jobConfigReader);
        Mockito.doReturn(Arrays.asList(jobConfig)).when(spiedReader).getPopulatedConfigs();

        final MessageContentAggregator messageContentAggregator = new MessageContentAggregator(spiedReader, providerDescriptors, notificationFilter);
        final Map<CommonDistributionConfiguration, List<AggregateMessageContent>> topicContentMap = messageContentAggregator.processNotifications(frequencyType, notificationContentList);

        assertTrue(topicContentMap.isEmpty());
    }

    @Test
    public void testJobProcessingProviderMismatch() throws Exception {
        final String unknownProvider = "unknown_provider";
        final FrequencyType frequencyType = FrequencyType.REAL_TIME;
        final String policyContent = getNotificationContentFromFile("json/policyRuleClearedNotification.json");
        final NotificationContent policyNotification = createNotification(unknownProvider, policyContent, NotificationType.RULE_VIOLATION_CLEARED);

        final String vulnerabilityContent = getNotificationContentFromFile("json/vulnerabilityTest.json");
        final NotificationContent vulnerabilityNotification = createNotification(unknownProvider, vulnerabilityContent, NotificationType.VULNERABILITY);

        final List<NotificationContent> notificationContentList = Arrays.asList(policyNotification, vulnerabilityNotification);

        final List<String> projectList = Arrays.asList("bad-project");
        final List<String> notificationTypesLIst = Arrays.asList(NotificationType.RULE_VIOLATION_CLEARED.name(), NotificationType.VULNERABILITY.name());
        final CommonDistributionConfiguration jobConfig = createCommonDistributionConfiguration();

        final JobConfigReader spiedReader = Mockito.spy(jobConfigReader);
        Mockito.doReturn(Arrays.asList(jobConfig)).when(spiedReader).getPopulatedConfigs();

        final MessageContentAggregator messageContentAggregator = new MessageContentAggregator(spiedReader, providerDescriptors, notificationFilter);
        final Map<CommonDistributionConfiguration, List<AggregateMessageContent>> topicContentMap = messageContentAggregator.processNotifications(frequencyType, notificationContentList);

        assertTrue(topicContentMap.containsKey(jobConfig));
        assertTrue(topicContentMap.get(jobConfig).isEmpty());
    }

    @Test
    public void testJobProcessingNotificationTypeMismatch() throws Exception {
        final String unknownProvider = "unknown_provider";
        final FrequencyType frequencyType = FrequencyType.REAL_TIME;
        final String policyContent = getNotificationContentFromFile("json/policyRuleClearedNotification.json");
        final NotificationContent policyNotification = createNotification(unknownProvider, policyContent, NotificationType.RULE_VIOLATION_CLEARED);

        final String vulnerabilityContent = getNotificationContentFromFile("json/vulnerabilityTest.json");
        final NotificationContent vulnerabilityNotification = createNotification(unknownProvider, vulnerabilityContent, NotificationType.VULNERABILITY);

        final List<NotificationContent> notificationContentList = Arrays.asList(policyNotification, vulnerabilityNotification);

        final List<String> projectList = Arrays.asList("bad-project");
        final List<String> notificationTypesLIst = Arrays.asList(NotificationType.RULE_VIOLATION_CLEARED.name(), NotificationType.VULNERABILITY.name());
        final CommonDistributionConfiguration jobConfig = createCommonDistributionConfiguration();

        final JobConfigReader spiedReader = Mockito.spy(jobConfigReader);
        Mockito.doReturn(Arrays.asList(jobConfig)).when(spiedReader).getPopulatedConfigs();

        final MessageContentAggregator messageContentAggregator = new MessageContentAggregator(spiedReader, providerDescriptors, notificationFilter);
        final Map<CommonDistributionConfiguration, List<AggregateMessageContent>> topicContentMap = messageContentAggregator.processNotifications(frequencyType, notificationContentList);

        assertTrue(topicContentMap.containsKey(jobConfig));
        assertTrue(topicContentMap.get(jobConfig).isEmpty());
    }

    private CommonDistributionConfiguration createCommonDistributionConfiguration() {
        final ConfigurationModel configurationModel = Mockito.mock(ConfigurationModel.class);

        Mockito.when(configurationModel.getConfigurationId()).thenReturn(1L);
        Mockito.when(configurationModel.getDescriptorId()).thenReturn(1L);
        // Use this to mock fields if necessarily:
        // final String fieldName;
        // final String expectedValue;
        // Mockito.when(configurationModel.getField(fieldName)).thenReturn(expectedValue);
        return new CommonDistributionConfiguration(configurationModel);
    }

    private String getNotificationContentFromFile(final String notificationJsonFileName) throws Exception {
        final ClassPathResource classPathResource = new ClassPathResource(notificationJsonFileName);
        final File jsonFile = classPathResource.getFile();
        return FileUtils.readFileToString(jsonFile, Charset.defaultCharset());
    }

    private NotificationContent createNotification(final String providerName, final String notificationContent, final NotificationType type) {
        return new NotificationContent(new Date(), providerName, new Date(), type.name(), notificationContent);
    }
}
