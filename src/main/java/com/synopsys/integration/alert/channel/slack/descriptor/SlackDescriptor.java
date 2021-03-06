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
package com.synopsys.integration.alert.channel.slack.descriptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.channel.slack.SlackChannel;
import com.synopsys.integration.alert.common.descriptor.ChannelDescriptor;

@Component
public class SlackDescriptor extends ChannelDescriptor {
    public static final String KEY_WEBHOOK = "channel.slack.webhook";
    public static final String KEY_CHANNEL_NAME = "channel.slack.channel.name";
    public static final String KEY_CHANNEL_USERNAME = "channel.slack.channel.username";

    public static final String SLACK_LABEL = "Slack";
    public static final String SLACK_URL = "slack";
    public static final String SLACK_ICON = "slack";

    @Autowired
    public SlackDescriptor(final SlackChannel channelListener, final SlackDistributionDescriptorActionApi distributionRestApi, final SlackUIConfig slackUIConfig) {
        super(SlackChannel.COMPONENT_NAME, SlackChannel.COMPONENT_NAME, channelListener, distributionRestApi, slackUIConfig);
    }

}
