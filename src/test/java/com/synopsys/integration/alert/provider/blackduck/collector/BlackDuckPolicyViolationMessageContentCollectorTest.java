package com.synopsys.integration.alert.provider.blackduck.collector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;

import com.google.gson.Gson;
import com.synopsys.integration.alert.common.enumeration.FormatType;
import com.synopsys.integration.alert.common.enumeration.ItemOperation;
import com.synopsys.integration.alert.common.message.model.AggregateMessageContent;
import com.synopsys.integration.alert.common.message.model.CategoryItem;
import com.synopsys.integration.alert.common.message.model.LinkableItem;
import com.synopsys.integration.alert.common.workflow.filter.field.JsonExtractor;
import com.synopsys.integration.alert.common.workflow.processor.DefaultMessageContentProcessor;
import com.synopsys.integration.alert.common.workflow.processor.DigestMessageContentProcessor;
import com.synopsys.integration.alert.common.workflow.processor.MessageContentProcessor;
import com.synopsys.integration.alert.database.notification.NotificationContent;
import com.synopsys.integration.alert.provider.blackduck.BlackDuckProvider;
import com.synopsys.integration.blackduck.api.generated.enumeration.NotificationType;

public class BlackDuckPolicyViolationMessageContentCollectorTest {
    private final JsonExtractor jsonExtractor = new JsonExtractor(new Gson());
    private final List<MessageContentProcessor> messageContentProcessorList = Arrays.asList(new DefaultMessageContentProcessor(), new DigestMessageContentProcessor());

    public static final void insertAndAssertCountsOnTopic(final BlackDuckPolicyCollector collector, final NotificationContent notification, final String topicName, final int expectedCategoryItemsCount,
        final int expectedLinkableItemsCount) {
        collector.insert(notification);
        final AggregateMessageContent content = collector.collect(FormatType.DEFAULT).stream().filter(topicContent -> topicName.equals(topicContent.getValue())).findFirst().orElse(null);
        final List<CategoryItem> items = content.getCategoryItemList();
        Assert.assertEquals(expectedCategoryItemsCount, items.size());
        Assert.assertEquals(expectedLinkableItemsCount, getCategoryItemLinkableItemsCount(items));
    }

    public static int getCategoryItemLinkableItemsCount(final List<CategoryItem> items) {
        int count = 0;
        for (final CategoryItem item : items) {
            count += item.getItems().size();
        }
        return count;
    }

    @Test
    public void insertRuleViolationClearedNotificationTest() throws Exception {
        final BlackDuckPolicyCollector collector = createPolicyViolationCollector();
        runSingleTest(collector, "json/policyRuleClearedNotification.json", NotificationType.RULE_VIOLATION_CLEARED);
    }

    @Test
    public void insertRuleViolationNotificationTest() throws Exception {
        final BlackDuckPolicyCollector collector = createPolicyViolationCollector();
        runSingleTest(collector, "json/policyRuleClearedNotification.json", NotificationType.RULE_VIOLATION);
    }

    @Test
    public void insertMultipleAndVerifyCorrectNumberOfCategoryItemsTest() throws Exception {
        final String topicName = "example";
        final int numberOfRulesCleared = 4;

        // there are 3 possible linkable items per notification in the test data
        // 1- policy rule
        // 2- component
        // 3- component version or policy override user
        final int linkableItemsPerCategory = 3;

        final String ruleContent = getNotificationContentFromFile("json/policyRuleClearedNotification.json");

        final NotificationContent n0 = createNotification(ruleContent, NotificationType.RULE_VIOLATION_CLEARED);
        final NotificationContent n1 = createNotification(ruleContent, NotificationType.RULE_VIOLATION_CLEARED);

        final BlackDuckPolicyCollector collector = createPolicyViolationCollector();

        int categoryCount = numberOfRulesCleared;
        // add 1 item for the policy override name linkable items
        int linkableItemsCount = categoryCount * linkableItemsPerCategory;
        insertAndAssertCountsOnTopic(collector, n0, topicName, categoryCount, linkableItemsCount);

        categoryCount = numberOfRulesCleared;
        linkableItemsCount = categoryCount * linkableItemsPerCategory;
        insertAndAssertCountsOnTopic(collector, n1, topicName, categoryCount, linkableItemsCount);

        Assert.assertEquals(1, collector.collect(FormatType.DEFAULT).size());
    }

    @Test
    public void testOperationCircuitBreaker() throws Exception {
        final String ruleContent = getNotificationContentFromFile("json/notification01.json");
        final NotificationContent n0 = createNotification(ruleContent, NotificationType.BOM_EDIT);
        final BlackDuckPolicyCollector collector = createPolicyViolationCollector();
        collector.insert(n0);
        Assert.assertEquals(0, collector.collect(FormatType.DEFAULT).size());
    }

    @Test
    public void insertionExceptionTest() throws Exception {
        final BlackDuckPolicyViolationCollector collector = createPolicyViolationCollector();
        final BlackDuckPolicyViolationCollector spiedCollector = Mockito.spy(collector);
        final String overrideContent = getNotificationContentFromFile("json/policyOverrideNotification.json");
        final NotificationContent n0 = createNotification(overrideContent, NotificationType.POLICY_OVERRIDE);
        Mockito.doThrow(new IllegalArgumentException("Insertion Error Exception Test")).when(spiedCollector)
            .addApplicableItems(Mockito.anyList(), Mockito.anyLong(), Mockito.any(LinkableItem.class), Mockito.anyString(), Mockito.any(ItemOperation.class), Mockito.any(SortedSet.class));
        spiedCollector.insert(n0);
        final List<AggregateMessageContent> contentList = spiedCollector.collect(FormatType.DEFAULT);
        assertTrue(contentList.isEmpty());
    }

    @Test
    public void collectEmptyMapTest() {
        final BlackDuckPolicyCollector collector = createPolicyViolationCollector();
        final BlackDuckPolicyCollector spiedCollector = Mockito.spy(collector);
        final List<AggregateMessageContent> contentList = spiedCollector.collect(FormatType.DEFAULT);
        assertTrue(contentList.isEmpty());
    }

    private void runSingleTest(final BlackDuckPolicyCollector collector, final String notificationJsonFileName, final NotificationType notificationType) throws Exception {
        final String content = getNotificationContentFromFile("json/policyRuleClearedNotification.json");
        final NotificationContent notificationContent = createNotification(content, notificationType);
        test(collector, notificationContent);
    }

    private BlackDuckPolicyViolationCollector createPolicyViolationCollector() {
        return new BlackDuckPolicyViolationCollector(jsonExtractor, messageContentProcessorList);
    }

    private String getNotificationContentFromFile(final String notificationJsonFileName) throws Exception {
        final ClassPathResource classPathResource = new ClassPathResource(notificationJsonFileName);
        final File jsonFile = classPathResource.getFile();
        return FileUtils.readFileToString(jsonFile, Charset.defaultCharset());
    }

    private NotificationContent createNotification(final String notificationContent, final NotificationType type) {
        final Date creationDate = Date.from(Instant.now());
        return new NotificationContent(creationDate, BlackDuckProvider.COMPONENT_NAME, creationDate, type.name(), notificationContent);
    }

    private void test(final BlackDuckPolicyCollector collector, final NotificationContent notification) {
        collector.insert(notification);
        final List<AggregateMessageContent> aggregateMessageContentList = collector.collect(FormatType.DEFAULT);
        assertFalse(aggregateMessageContentList.isEmpty());
    }
}
