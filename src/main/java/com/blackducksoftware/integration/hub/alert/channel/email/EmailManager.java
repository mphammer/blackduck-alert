/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.alert.channel.email;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.alert.channel.DistributionChannel;
import com.blackducksoftware.integration.hub.alert.channel.SupportedChannels;
import com.blackducksoftware.integration.hub.alert.channel.manager.DistributionChannelManager;
import com.blackducksoftware.integration.hub.alert.datasource.entity.distribution.EmailGroupDistributionConfigEntity;
import com.blackducksoftware.integration.hub.alert.datasource.entity.global.GlobalEmailConfigEntity;
import com.blackducksoftware.integration.hub.alert.digest.model.ProjectData;

@Component
public class EmailManager extends DistributionChannelManager<GlobalEmailConfigEntity, EmailGroupDistributionConfigEntity, EmailGroupEvent> {
    public EmailManager(final DistributionChannel<EmailGroupEvent, GlobalEmailConfigEntity, EmailGroupDistributionConfigEntity> distributionChannel, final JpaRepository<GlobalEmailConfigEntity, Long> globalRepository,
            final JpaRepository<EmailGroupDistributionConfigEntity, Long> localRepository) {
        super(distributionChannel, globalRepository, localRepository);
    }

    @Override
    public boolean isApplicable(final String supportedChannelName) {
        return SupportedChannels.EMAIL_GROUP.equals(supportedChannelName);
    }

    @Override
    public EmailGroupEvent createChannelEvent(final ProjectData projectData, final Long commonDistributionConfigId) {
        return new EmailGroupEvent(projectData, commonDistributionConfigId);
    }

}
