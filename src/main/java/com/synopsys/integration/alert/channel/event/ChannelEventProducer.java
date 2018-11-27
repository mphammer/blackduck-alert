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
package com.synopsys.integration.alert.channel.event;

import java.util.Collections;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.field.CommonDistributionFields;
import com.synopsys.integration.alert.common.model.AggregateMessageContent;
import com.synopsys.integration.alert.common.model.LinkableItem;
import com.synopsys.integration.alert.web.exception.AlertFieldException;
import com.synopsys.integration.rest.RestConstants;

@Component
public class ChannelEventProducer {

    public DistributionEvent createChannelEvent(final CommonDistributionFields commonDistributionConfig, final AggregateMessageContent messageContent) {
        final String channelName = commonDistributionConfig.getDistributionType();
        final String createdAt = RestConstants.formatDate(new Date());
        final String provider = commonDistributionConfig.getProviderName();
        final String formatType = commonDistributionConfig.getFormatType().name();
        return new DistributionEvent(channelName, createdAt, provider, formatType, messageContent, commonDistributionConfig);
    }

    public DistributionEvent createChannelTestEvent(final CommonDistributionFields commmonDistributionConfig) throws AlertFieldException {
        return null;
    }

    public AggregateMessageContent createTestNotificationContent() {
        final LinkableItem subTopic = new LinkableItem("subTopic", "Alert has sent this test message", null);
        return new AggregateMessageContent("testTopic", "Alert Test Message", null, subTopic, Collections.emptyList());

    }
}
