/**
 * blackduck-alert
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.alert.channel;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.alert.channel.event.ChannelEvent;
import com.blackducksoftware.integration.alert.common.ContentConverter;
import com.blackducksoftware.integration.alert.common.digest.model.DigestModel;
import com.blackducksoftware.integration.alert.common.digest.model.ProjectData;
import com.blackducksoftware.integration.alert.common.enumeration.AuditEntryStatus;
import com.blackducksoftware.integration.alert.common.event.AlertEvent;
import com.blackducksoftware.integration.alert.database.audit.AuditEntryEntity;
import com.blackducksoftware.integration.alert.database.audit.AuditEntryRepository;
import com.blackducksoftware.integration.alert.database.audit.AuditNotificationRepository;
import com.blackducksoftware.integration.alert.database.audit.relation.AuditNotificationRelation;
import com.google.gson.Gson;

@Transactional
@Component
public class ChannelTemplateManager {
    private final Gson gson;
    private final JmsTemplate jmsTemplate;
    private final AuditEntryRepository auditEntryRepository;
    private final AuditNotificationRepository auditNotificationRepository;
    private final ContentConverter contentConverter;

    @Autowired
    public ChannelTemplateManager(final Gson gson, final AuditEntryRepository auditEntryRepository, final AuditNotificationRepository auditNotificationRepository, final JmsTemplate jmsTemplate,
            final ContentConverter contentConverter) {
        this.gson = gson;
        this.auditEntryRepository = auditEntryRepository;
        this.auditNotificationRepository = auditNotificationRepository;
        this.jmsTemplate = jmsTemplate;
        this.contentConverter = contentConverter;
    }

    public void sendEvents(final List<? extends AlertEvent> eventList) {
        if (!eventList.isEmpty()) {
            eventList.forEach(this::sendEvent);
        }
    }

    public boolean sendEvent(final AlertEvent event) {
        final String destination = event.getDestination();
        if (event instanceof ChannelEvent) {
            final ChannelEvent channelEvent = (ChannelEvent) event;
            AuditEntryEntity auditEntryEntity = new AuditEntryEntity(channelEvent.getCommonDistributionConfigId(), new Date(System.currentTimeMillis()), null, null, null, null);

            if (channelEvent.getAuditEntryId() != null) {
                auditEntryEntity = auditEntryRepository.findById(channelEvent.getAuditEntryId()).orElse(auditEntryEntity);
            }

            auditEntryEntity.setStatus(AuditEntryStatus.PENDING);
            final AuditEntryEntity savedAuditEntryEntity = auditEntryRepository.save(auditEntryEntity);
            channelEvent.setAuditEntryId(savedAuditEntryEntity.getId());
            final Optional<DigestModel> optionalModel = contentConverter.getContent(channelEvent.getContent(), DigestModel.class);
            if (!optionalModel.isPresent()) {
                return false;
            } else {
                final Collection<ProjectData> projectDataCollection = optionalModel.get().getProjectDataCollection();
                projectDataCollection.stream()
                        .map(ProjectData::getNotificationIds)
                        .flatMap(Collection::stream)
                        .map(notificationId -> new AuditNotificationRelation(savedAuditEntryEntity.getId(), notificationId))
                        .forEach(auditNotificationRelation -> auditNotificationRepository.save(auditNotificationRelation));

                final String jsonMessage = gson.toJson(channelEvent);
                jmsTemplate.convertAndSend(destination, jsonMessage);
            }
        } else {
            final String jsonMessage = gson.toJson(event);
            jmsTemplate.convertAndSend(destination, jsonMessage);
        }
        return true;
    }
}
