/**
 * blackduck-alert
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
package com.synopsys.integration.alert.database.audit;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.synopsys.integration.alert.common.enumeration.AuditEntryStatus;
import com.synopsys.integration.alert.common.model.AggregateMessageContent;
import com.synopsys.integration.alert.common.model.CategoryItem;
import com.synopsys.integration.alert.database.audit.relation.AuditNotificationRelation;

@Component
public class AuditUtility {
    private static final Logger logger = LoggerFactory.getLogger(AuditUtility.class);
    private final AuditEntryRepository auditEntryRepository;
    private final AuditNotificationRepository auditNotificationRepository;

    @Autowired
    public AuditUtility(final AuditEntryRepository auditEntryRepository, final AuditNotificationRepository auditNotificationRepository) {
        this.auditEntryRepository = auditEntryRepository;
        this.auditNotificationRepository = auditNotificationRepository;
    }

    @Transactional
    public Long createAuditEntry(final Long auditEntryId, final Long commonDistributionId, final AggregateMessageContent content) {
        AuditEntryEntity auditEntryEntity = new AuditEntryEntity(commonDistributionId, new Date(System.currentTimeMillis()), null, null, null, null);

        if (null != auditEntryId) {
            auditEntryEntity = auditEntryRepository.findById(auditEntryId).orElse(auditEntryEntity);
        }

        auditEntryEntity.setStatus(AuditEntryStatus.PENDING);
        final AuditEntryEntity savedAuditEntryEntity = auditEntryRepository.save(auditEntryEntity);

        for (final CategoryItem item : content.getCategoryItemList()) {
            final AuditNotificationRelation auditNotificationRelation = new AuditNotificationRelation(savedAuditEntryEntity.getId(), item.getNotificationId());
            auditNotificationRepository.save(auditNotificationRelation);
        }

        return savedAuditEntryEntity.getId();
    }

    @Transactional
    public void setAuditEntrySuccess(final Long auditEntryId) {
        if (auditEntryId != null) {
            try {
                final Optional<AuditEntryEntity> auditEntryEntityOptional = auditEntryRepository.findById(auditEntryId);
                if (!auditEntryEntityOptional.isPresent()) {
                    logger.error("Could not find the audit entry {} to set the success status.", auditEntryId);
                }
                final AuditEntryEntity auditEntryEntity = auditEntryEntityOptional.orElse(new AuditEntryEntity());
                auditEntryEntity.setStatus(AuditEntryStatus.SUCCESS);
                auditEntryEntity.setErrorMessage(null);
                auditEntryEntity.setErrorStackTrace(null);
                auditEntryEntity.setTimeLastSent(new Date(System.currentTimeMillis()));
                auditEntryRepository.save(auditEntryEntity);
            } catch (final Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    @Transactional
    public void setAuditEntryFailure(final Long auditEntryId, final String errorMessage, final Throwable t) {
        if (auditEntryId != null) {
            try {
                final Optional<AuditEntryEntity> auditEntryEntityOptional = auditEntryRepository.findById(auditEntryId);
                if (!auditEntryEntityOptional.isPresent()) {
                    logger.error("Could not find the audit entry {} to set the failure status. Error: {}", auditEntryId, errorMessage);
                }
                final AuditEntryEntity auditEntryEntity = auditEntryEntityOptional.orElse(new AuditEntryEntity());
                auditEntryEntity.setId(auditEntryId);
                auditEntryEntity.setStatus(AuditEntryStatus.FAILURE);
                auditEntryEntity.setErrorMessage(errorMessage);
                final String[] rootCause = ExceptionUtils.getRootCauseStackTrace(t);
                String exceptionStackTrace = "";
                for (final String line : rootCause) {
                    if (exceptionStackTrace.length() + line.length() < AuditEntryEntity.STACK_TRACE_CHAR_LIMIT) {
                        exceptionStackTrace = String.format("%s%s%s", exceptionStackTrace, line, System.lineSeparator());
                    } else {
                        break;
                    }
                }
                auditEntryEntity.setErrorStackTrace(exceptionStackTrace);
                auditEntryEntity.setTimeLastSent(new Date(System.currentTimeMillis()));
                auditEntryRepository.save(auditEntryEntity);
            } catch (final Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

}
