/**
 * alert-database
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
package com.synopsys.integration.alert.database.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.synopsys.integration.alert.common.exception.AlertDatabaseConstraintException;
import com.synopsys.integration.alert.common.persistence.accessor.ConfigurationAccessor;
import com.synopsys.integration.alert.common.rest.model.CommonDistributionConfiguration;

@Component
public class JobConfigReader {
    private final ConfigurationAccessor configurationAccessor;

    @Autowired
    public JobConfigReader(final ConfigurationAccessor configurationAccessor) {
        this.configurationAccessor = configurationAccessor;
    }

    @Transactional
    public List<CommonDistributionConfiguration> getPopulatedJobConfigs() {
        return configurationAccessor.getAllJobs().stream()
                   .map(CommonDistributionConfiguration::new)
                   .collect(Collectors.toList());
    }

    @Transactional
    public Optional<CommonDistributionConfiguration> getPopulatedJobConfig(final UUID configId) {
        if (null == configId) {
            return Optional.empty();
        }
        try {
            return configurationAccessor.getJobById(configId).map(CommonDistributionConfiguration::new);
        } catch (final AlertDatabaseConstraintException e) {
            return Optional.empty();
        }
    }

}
