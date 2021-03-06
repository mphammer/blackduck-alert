/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.synopsys.integration.alert.audit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.jayway.jsonpath.JsonPath;
import com.synopsys.integration.alert.channel.hipchat.HipChatChannel;
import com.synopsys.integration.alert.common.descriptor.config.ui.ChannelDistributionUIConfig;
import com.synopsys.integration.alert.common.enumeration.AuditEntryStatus;
import com.synopsys.integration.alert.common.enumeration.ConfigContextEnum;
import com.synopsys.integration.alert.common.persistence.accessor.ConfigurationAccessor;
import com.synopsys.integration.alert.common.persistence.model.AuditEntryModel;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationFieldModel;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationJobModel;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationModel;
import com.synopsys.integration.alert.common.rest.model.AlertPagedModel;
import com.synopsys.integration.alert.common.rest.model.NotificationConfig;
import com.synopsys.integration.alert.database.audit.AuditEntryEntity;
import com.synopsys.integration.alert.database.audit.AuditEntryRepository;
import com.synopsys.integration.alert.database.audit.AuditNotificationRelation;
import com.synopsys.integration.alert.database.audit.AuditNotificationRepository;
import com.synopsys.integration.alert.database.configuration.repository.DescriptorConfigRepository;
import com.synopsys.integration.alert.database.configuration.repository.FieldValueRepository;
import com.synopsys.integration.alert.database.notification.NotificationContent;
import com.synopsys.integration.alert.database.notification.NotificationContentRepository;
import com.synopsys.integration.alert.mock.MockConfigurationModelFactory;
import com.synopsys.integration.alert.mock.entity.MockNotificationContent;
import com.synopsys.integration.alert.provider.blackduck.BlackDuckProvider;
import com.synopsys.integration.alert.util.AlertIntegrationTest;
import com.synopsys.integration.alert.web.audit.AuditEntryController;
import com.synopsys.integration.util.ResourceUtil;

@Transactional
public class AuditEntryHandlerTestIT extends AlertIntegrationTest {
    @Autowired
    public Gson gson;
    @Autowired
    public AuditEntryRepository auditEntryRepository;
    @Autowired
    public AuditNotificationRepository auditNotificationRepository;
    @Autowired
    private AuditEntryController auditEntryController;
    @Autowired
    private NotificationContentRepository notificationContentRepository;
    @Autowired
    private ConfigurationAccessor baseConfigurationAccessor;
    @Autowired
    private DescriptorConfigRepository descriptorConfigRepository;
    @Autowired
    private FieldValueRepository fieldValueRepository;

    @BeforeEach
    public void init() {
        auditEntryRepository.deleteAllInBatch();
        notificationContentRepository.deleteAllInBatch();
        descriptorConfigRepository.deleteAllInBatch();
        fieldValueRepository.deleteAllInBatch();

        auditEntryRepository.flush();
    }

    @AfterEach
    public void cleanup() {
        auditEntryRepository.deleteAllInBatch();
        notificationContentRepository.deleteAllInBatch();
        descriptorConfigRepository.deleteAllInBatch();
        fieldValueRepository.deleteAllInBatch();

        auditEntryRepository.flush();
    }

    @Test
    public void getTestIT() throws Exception {
        final MockNotificationContent mockNotification = new MockNotificationContent();
        final NotificationContent savedNotificationEntity = notificationContentRepository.save(mockNotification.createEntity());

        notificationContentRepository.save(new MockNotificationContent(new Date(System.currentTimeMillis()), "provider", new Date(System.currentTimeMillis()), "notificationType", "{}", 234L).createEntity());

        final Collection<ConfigurationFieldModel> hipChatFields = MockConfigurationModelFactory.createHipChatDistributionFields();
        final ConfigurationJobModel configurationJobModel = baseConfigurationAccessor.createJob(Set.of(HipChatChannel.COMPONENT_NAME, BlackDuckProvider.COMPONENT_NAME), hipChatFields);

        final AuditEntryEntity savedAuditEntryEntity = auditEntryRepository.save(
            new AuditEntryEntity(configurationJobModel.getJobId(), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), AuditEntryStatus.SUCCESS.toString(), null, null));

        auditNotificationRepository.save(new AuditNotificationRelation(savedAuditEntryEntity.getId(), savedNotificationEntity.getId()));

        AlertPagedModel<AuditEntryModel> auditEntries = auditEntryController.get(null, null, null, null, null, true);
        assertEquals(1, auditEntries.getContent().size());

        final ResponseEntity<String> auditEntryResponse = auditEntryController.get(savedNotificationEntity.getId());
        assertNotNull(auditEntryResponse);
        assertEquals(HttpStatus.OK, auditEntryResponse.getStatusCode());

        final JsonArray jsonArray = JsonPath.read(auditEntryResponse.getBody(), "$.message");
        final String message = jsonArray.get(0).getAsString();
        final AuditEntryModel auditEntry = gson.fromJson(message, AuditEntryModel.class);
        assertEquals(auditEntry, auditEntries.getContent().get(0));

        assertEquals(savedNotificationEntity.getId().toString(), auditEntry.getId());
        assertFalse(auditEntry.getJobs().isEmpty());
        assertEquals(1, auditEntry.getJobs().size());
        final Map<String, ConfigurationFieldModel> keyToFieldMap = configurationJobModel.createKeyToFieldMap();
        assertEquals(keyToFieldMap.get(ChannelDistributionUIConfig.KEY_CHANNEL_NAME).getFieldValue().get(), auditEntry.getJobs().get(0).getEventType());
        assertEquals(keyToFieldMap.get(ChannelDistributionUIConfig.KEY_NAME).getFieldValue().get(), auditEntry.getJobs().get(0).getName());

        final NotificationConfig notification = auditEntry.getNotification();
        assertEquals(savedNotificationEntity.getCreatedAt().toString(), notification.getCreatedAt());
        assertEquals(savedNotificationEntity.getNotificationType(), notification.getNotificationType());
        assertNotNull(notification.getContent());

        auditEntries = auditEntryController.get(null, null, null, null, null, false);
        assertEquals(2, auditEntries.getContent().size());
    }

    @Test
    public void getGetAuditInfoForJobIT() throws Exception {
        final Collection<ConfigurationFieldModel> hipChatFields = MockConfigurationModelFactory.createHipChatDistributionFields();
        final ConfigurationModel configurationModel = baseConfigurationAccessor.createConfiguration(HipChatChannel.COMPONENT_NAME, ConfigContextEnum.DISTRIBUTION, hipChatFields);
        final UUID jobID = UUID.randomUUID();
        final ConfigurationJobModel configurationJobModel = new ConfigurationJobModel(jobID, Set.of(configurationModel));

        final AuditEntryEntity savedAuditEntryEntity = auditEntryRepository.save(
            new AuditEntryEntity(configurationJobModel.getJobId(), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), AuditEntryStatus.SUCCESS.toString(), null, null));

        final ResponseEntity<String> jobAuditModelResponse = auditEntryController.getAuditInfoForJob(savedAuditEntryEntity.getCommonConfigId());
        assertNotNull(jobAuditModelResponse);
        assertEquals(HttpStatus.OK, jobAuditModelResponse.getStatusCode());

        assertNotNull(jobAuditModelResponse.getBody());
    }

    @Test
    public void resendNotificationTestIT() throws Exception {
        final String content = ResourceUtil.getResourceAsString(getClass(), "/json/policyOverrideNotification.json", StandardCharsets.UTF_8);

        final MockNotificationContent mockNotification = new MockNotificationContent(new java.util.Date(), BlackDuckProvider.COMPONENT_NAME, new java.util.Date(), "POLICY_OVERRIDE", content, 1L);

        final Collection<ConfigurationFieldModel> hipChatFields = MockConfigurationModelFactory.createHipChatDistributionFields();
        final ConfigurationJobModel configurationJobModel = baseConfigurationAccessor.createJob(Set.of(HipChatChannel.COMPONENT_NAME, BlackDuckProvider.COMPONENT_NAME), hipChatFields);

        final NotificationContent savedNotificationEntity = notificationContentRepository.save(mockNotification.createEntity());

        final AuditEntryEntity savedAuditEntryEntity = auditEntryRepository
                                                           .save(new AuditEntryEntity(configurationJobModel.getJobId(), new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()),
                                                               AuditEntryStatus.SUCCESS.toString(),
                                                               null, null));

        auditNotificationRepository.save(new AuditNotificationRelation(savedAuditEntryEntity.getId(), savedNotificationEntity.getId()));

        final ResponseEntity<String> invalidIdResponse = auditEntryController.post(-1L, null);
        assertEquals(HttpStatus.GONE, invalidIdResponse.getStatusCode());

        final ResponseEntity<String> validResponse = auditEntryController.post(savedNotificationEntity.getId(), null);
        assertEquals(HttpStatus.OK, validResponse.getStatusCode());

        final ResponseEntity<String> invalidJobResponse = auditEntryController.post(savedNotificationEntity.getId(), UUID.randomUUID());
        assertEquals(HttpStatus.GONE, invalidJobResponse.getStatusCode());

        final ResponseEntity<String> invalidReferenceResponse_1 = auditEntryController.post(savedNotificationEntity.getId(), null);
        assertEquals(HttpStatus.OK, invalidReferenceResponse_1.getStatusCode());

        final ResponseEntity<String> validJobSpecificResend = auditEntryController.post(savedNotificationEntity.getId(), configurationJobModel.getJobId());
        assertEquals(HttpStatus.OK, validJobSpecificResend.getStatusCode());
    }

}
