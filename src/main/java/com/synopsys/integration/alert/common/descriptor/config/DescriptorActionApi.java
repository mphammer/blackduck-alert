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
package com.synopsys.integration.alert.common.descriptor.config;

import java.util.Map;

import com.synopsys.integration.alert.channel.DistributionChannel;
import com.synopsys.integration.alert.channel.event.DistributionEvent;
import com.synopsys.integration.alert.common.field.CommonDistributionFields;
import com.synopsys.integration.exception.IntegrationException;

public abstract class DescriptorActionApi {
    private final DistributionChannel distributionChannel;
    private final StartupComponent startupComponent;

    public DescriptorActionApi(final DistributionChannel distributionChannel) {
        this(distributionChannel, null);
    }

    public DescriptorActionApi(final DistributionChannel distributionChannel, final StartupComponent startupComponent) {
        this.distributionChannel = distributionChannel;
        this.startupComponent = startupComponent;
    }

    public DistributionChannel getDistributionChannel() {
        return distributionChannel;
    }

    public StartupComponent getStartupComponent() {
        return startupComponent;
    }

    public boolean hasStartupProperties() {
        return getStartupComponent() != null;
    }

    public abstract void validateConfig(CommonDistributionFields commonDistributionFields, Map<String, String> fieldErrors);

    public abstract DistributionEvent createTestEvent(CommonDistributionFields commonDistributionFields);

    public void testConfig(final CommonDistributionFields commonDistributionFields) throws IntegrationException {
        final DistributionEvent event = createTestEvent(commonDistributionFields);
        distributionChannel.sendMessage(event);
    }

}
