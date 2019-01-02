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
package com.synopsys.integration.alert.channel.hipchat.descriptor;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.channel.hipchat.HipChatChannel;
import com.synopsys.integration.alert.common.descriptor.config.DescriptorActionApi;
import com.synopsys.integration.alert.database.channel.hipchat.HipChatGlobalRepositoryAccessor;
import com.synopsys.integration.alert.web.channel.model.HipChatGlobalConfig;
import com.synopsys.integration.alert.web.model.Config;
import com.synopsys.integration.alert.web.model.TestConfigModel;
import com.synopsys.integration.exception.IntegrationException;

@Component
public class HipChatGlobalDescriptorActionApi extends DescriptorActionApi {
    private final HipChatChannel hipChatChannel;

    @Autowired
    public HipChatGlobalDescriptorActionApi(final HipChatGlobalTypeConverter databaseContentConverter, final HipChatGlobalRepositoryAccessor repositoryAccessor, final HipChatChannel hipChatChannel,
            final HipChatStartupComponent hipChatStartupComponent) {
        super(databaseContentConverter, repositoryAccessor, hipChatStartupComponent);
        this.hipChatChannel = hipChatChannel;
    }

    @Override
    public void validateConfig(final Config restModel, final Map<String, String> fieldErrors) {
        final HipChatGlobalConfig hipChatRestModel = (HipChatGlobalConfig) restModel;
        if (StringUtils.isBlank(hipChatRestModel.getApiKey())) {
            fieldErrors.put("apiKey", "ApiKey can't be blank");
        }
    }

    @Override
    public void testConfig(final TestConfigModel testConfig) throws IntegrationException {
        hipChatChannel.testGlobalConfig(testConfig);
    }

}
