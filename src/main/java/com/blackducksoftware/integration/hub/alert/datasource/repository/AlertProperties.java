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
package com.blackducksoftware.integration.hub.alert.datasource.repository;

import java.util.List;

import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.alert.datasource.entity.GlobalConfigEntity;

@Component
public class AlertProperties {

    private final GlobalRepository globalRepository;

    public AlertProperties(final GlobalRepository globalRepository) {
        this.globalRepository = globalRepository;
    }

    public GlobalConfigEntity getConfig(final Long id) {
        GlobalConfigEntity globalConfig = null;
        if (id != null && globalRepository.exists(id)) {
            globalConfig = globalRepository.findOne(id);
        } else {
            globalConfig = getConfig();
        }
        return globalConfig;
    }

    public GlobalConfigEntity getConfig() {
        final List<GlobalConfigEntity> configs = globalRepository.findAll();
        if (configs != null && !configs.isEmpty()) {
            return configs.get(0);
        }
        return null;
    }

    public String getHubUrl() {
        final GlobalConfigEntity globalConfig = getConfig();
        if (globalConfig != null) {
            return getConfig().getHubUrl();
        }
        return null;
    }

    public Integer getHubTimeout() {
        final GlobalConfigEntity globalConfig = getConfig();
        if (globalConfig != null) {
            return getConfig().getHubTimeout();
        }
        return null;
    }

    public String getHubUsername() {
        final GlobalConfigEntity globalConfig = getConfig();
        if (globalConfig != null) {
            return getConfig().getHubUsername();
        }
        return null;
    }

    public String getHubPassword() {
        final GlobalConfigEntity globalConfig = getConfig();
        if (globalConfig != null) {
            return getConfig().getHubPassword();
        }
        return null;
    }

    public String getHubProxyHost() {
        final GlobalConfigEntity globalConfig = getConfig();
        if (globalConfig != null) {
            return getConfig().getHubProxyHost();
        }
        return null;
    }

    public String getHubProxyPort() {
        final GlobalConfigEntity globalConfig = getConfig();
        if (globalConfig != null) {
            return getConfig().getHubProxyPort();
        }
        return null;
    }

    public String getHubProxyUsername() {
        final GlobalConfigEntity globalConfig = getConfig();
        if (globalConfig != null) {
            return getConfig().getHubProxyUsername();
        }
        return null;
    }

    public String getHubProxyPassword() {
        final GlobalConfigEntity globalConfig = getConfig();
        if (globalConfig != null) {
            return getConfig().getHubProxyPassword();
        }
        return null;
    }

    public Boolean getHubAlwaysTrustCertificate() {
        final GlobalConfigEntity globalConfig = getConfig();
        if (globalConfig != null) {
            return getConfig().getHubAlwaysTrustCertificate();
        }
        return null;
    }

    public String getAccumulatorCron() {
        final GlobalConfigEntity globalConfig = getConfig();
        if (globalConfig != null) {
            return getConfig().getAccumulatorCron();
        }
        return null;
    }

    public String getDailyDigestCron() {
        final GlobalConfigEntity globalConfig = getConfig();
        if (globalConfig != null) {
            return getConfig().getDailyDigestCron();
        }
        return null;
    }

    public String getRealTimeDigestCron() {
        final GlobalConfigEntity globalConfig = getConfig();
        if (globalConfig != null) {
            return getConfig().getRealTimeDigestCron();
        }
        return null;
    }

}
