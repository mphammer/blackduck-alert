package com.blackducksoftware.integration.alert.digest.filter;

import static org.junit.Assert.*;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import com.blackducksoftware.integration.alert.Application;
import com.blackducksoftware.integration.alert.channel.email.EmailGroupChannel;
import com.blackducksoftware.integration.alert.channel.hipchat.HipChatChannel;
import com.blackducksoftware.integration.alert.channel.slack.SlackChannel;
import com.blackducksoftware.integration.alert.config.DataSourceConfig;
import com.blackducksoftware.integration.alert.database.entity.CommonDistributionConfigEntity;
import com.blackducksoftware.integration.alert.database.entity.NotificationCategoryEnum;
import com.blackducksoftware.integration.alert.database.entity.NotificationEntity;
import com.blackducksoftware.integration.alert.database.entity.NotificationTypeEntity;
import com.blackducksoftware.integration.alert.database.entity.VulnerabilityEntity;
import com.blackducksoftware.integration.alert.database.entity.repository.CommonDistributionRepository;
import com.blackducksoftware.integration.alert.database.entity.repository.NotificationTypeRepository;
import com.blackducksoftware.integration.alert.database.relation.DistributionNotificationTypeRelation;
import com.blackducksoftware.integration.alert.database.relation.repository.DistributionNotificationTypeRepository;
import com.blackducksoftware.integration.alert.enumeration.DigestTypeEnum;
import com.blackducksoftware.integration.alert.event.ChannelEvent;
import com.blackducksoftware.integration.alert.model.NotificationModel;
import com.blackducksoftware.integration.test.annotation.DatabaseConnectionTest;
import com.blackducksoftware.integration.test.annotation.ExternalConnectionTest;
import com.github.springtestdbunit.DbUnitTestExecutionListener;

@Category({ DatabaseConnectionTest.class, ExternalConnectionTest.class })
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { Application.class, DataSourceConfig.class })
@TestPropertySource(locations = "classpath:spring-test.properties")
@Transactional
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class NotificationEventManagerTest {

    @Autowired
    private CommonDistributionRepository commonDistributionRepository;

    @Autowired
    private NotificationEventManager notificationEventMananger;

    @Autowired
    private DistributionNotificationTypeRepository distributionNotificationTypeRepository;

    @Autowired
    private NotificationTypeRepository notificationTypeRepository;

    @Before
    public void initializeConfig() {
        cleanUp();

        long configId = 1;
        CommonDistributionConfigEntity slackDistributionConfig = new CommonDistributionConfigEntity(configId++, SlackChannel.COMPONENT_NAME, "Slack Config", DigestTypeEnum.REAL_TIME, false);
        CommonDistributionConfigEntity hipChatDistributionConfig = new CommonDistributionConfigEntity(configId++, HipChatChannel.COMPONENT_NAME, "HipChat Config", DigestTypeEnum.REAL_TIME, false);
        CommonDistributionConfigEntity emailDistributionConfig = new CommonDistributionConfigEntity(configId++, EmailGroupChannel.COMPONENT_NAME, "Email Config", DigestTypeEnum.REAL_TIME, false);

        slackDistributionConfig = commonDistributionRepository.save(slackDistributionConfig);
        hipChatDistributionConfig = commonDistributionRepository.save(hipChatDistributionConfig);
        emailDistributionConfig = commonDistributionRepository.save(emailDistributionConfig);

        for (final NotificationCategoryEnum notificationCategoryEnum : NotificationCategoryEnum.values()) {
            final NotificationTypeEntity notificationTypeEntity = new NotificationTypeEntity(notificationCategoryEnum);
            final NotificationTypeEntity savedNotificationType = notificationTypeRepository.save(notificationTypeEntity);
            saveDistributionNotificationTypeRelation(slackDistributionConfig.getId(), savedNotificationType.getId());
            saveDistributionNotificationTypeRelation(hipChatDistributionConfig.getId(), savedNotificationType.getId());
            saveDistributionNotificationTypeRelation(emailDistributionConfig.getId(), savedNotificationType.getId());
        }
    }

    public void cleanUp() {
        commonDistributionRepository.deleteAll();
        distributionNotificationTypeRepository.deleteAll();
        notificationTypeRepository.deleteAll();
    }

    private void saveDistributionNotificationTypeRelation(final Long commonDistributionConfigId, final Long notificationTypeId) {
        final DistributionNotificationTypeRelation notificationRelation = new DistributionNotificationTypeRelation(commonDistributionConfigId, notificationTypeId);
        distributionNotificationTypeRepository.save(notificationRelation);
    }

    @Test
    public void createInvalidDigestTypeTest() {
        final NotificationModel notificationModel = createNotificationModel("Project_1", "1.0.0", NotificationCategoryEnum.POLICY_VIOLATION);
        final List<NotificationModel> notificationModels = Arrays.asList(notificationModel);
        final List<ChannelEvent> channelEvents = notificationEventMananger.createChannelEvents(DigestTypeEnum.DAILY, notificationModels);
        assertTrue(channelEvents.isEmpty());
    }

    @Test
    public void createChannelEventTest() throws Exception {
        final List<CommonDistributionConfigEntity> configEntityList = commonDistributionRepository.findAll();

        final NotificationModel notification_1 = createNotificationModel("Project_1", "1.0.0", NotificationCategoryEnum.POLICY_VIOLATION);
        final NotificationModel notification_2 = createNotificationModel("Project_2", "1.0.0", NotificationCategoryEnum.POLICY_VIOLATION);
        final NotificationModel notification_3 = createNotificationModel("Project_1", "2.0.0", NotificationCategoryEnum.POLICY_VIOLATION);
        final List<NotificationModel> notificationModelList = Arrays.asList(notification_1, notification_2, notification_3);
        final List<ChannelEvent> channelEvents = notificationEventMananger.createChannelEvents(DigestTypeEnum.REAL_TIME, notificationModelList);
        assertEquals(configEntityList.size(), channelEvents.size());

        channelEvents.forEach(event -> {
            assertNotNull(event.getContent());
        });
    }

    private NotificationModel createNotificationModel(final String projectName, final String projectVersion, final NotificationCategoryEnum notificationType) {
        final String eventKey = "event key";
        final Date createdAt = Date.from(ZonedDateTime.now().toInstant());
        final String projectUrl = "project url";
        final String projectVersionUrl = "project version url";
        final String componentName = "component name";
        final String componentVersion = "component version";
        final String policyRuleName = "policy rule";
        final String policyRuleUser = "policy user";
        final NotificationEntity notification = new NotificationEntity(eventKey, createdAt, notificationType, projectName, projectUrl, projectVersion, projectVersionUrl,
                componentName, componentVersion, policyRuleName, policyRuleUser);

        final Collection<VulnerabilityEntity> vulnerabilities = Collections.emptyList();
        final NotificationModel model = new NotificationModel(notification, vulnerabilities);
        return model;
    }
}
