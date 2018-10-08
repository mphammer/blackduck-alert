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
package com.synopsys.integration.alert.channel.email.descriptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.channel.email.EmailEventProducer;
import com.synopsys.integration.alert.channel.email.EmailGroupChannel;
import com.synopsys.integration.alert.common.descriptor.ChannelDescriptor;
import com.synopsys.integration.alert.database.channel.email.EmailDistributionRepositoryAccessor;

@Component
public class EmailDescriptor extends ChannelDescriptor {

    @Autowired
    public EmailDescriptor(final EmailGroupChannel channelListener, final EmailGlobalDescriptorActionApi globalRestApi, final EmailGlobalUIConfig emailGlobalUIConfig,
        final EmailDistributionDescriptorActionApi distributionRestApi, final EmailDistributionUIConfig emailDistributionUIConfig, final EmailDistributionRepositoryAccessor emailDistributionRepositoryAccessor,
        final EmailEventProducer emailEventProducer) {
        super(EmailGroupChannel.COMPONENT_NAME, EmailGroupChannel.COMPONENT_NAME, channelListener, distributionRestApi, emailDistributionUIConfig, globalRestApi, emailGlobalUIConfig, emailDistributionRepositoryAccessor, emailEventProducer);
    }
}