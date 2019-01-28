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
package com.synopsys.integration.alert.common;

import java.util.Optional;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.database.BaseConfigurationAccessor;
import com.synopsys.integration.alert.common.enumeration.ConfigContextEnum;
import com.synopsys.integration.alert.common.exception.AlertDatabaseConstraintException;
import com.synopsys.integration.alert.component.settings.SettingsDescriptor;
import com.synopsys.integration.alert.database.api.configuration.model.ConfigurationFieldModel;
import com.synopsys.integration.alert.database.api.configuration.model.ConfigurationModel;
import com.synopsys.integration.rest.credentials.CredentialsBuilder;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.proxy.ProxyInfoBuilder;

@Component
public class ProxyManager {
    private static final Logger logger = LoggerFactory.getLogger(ProxyManager.class);
    private final BaseConfigurationAccessor configurationAccessor;

    @Autowired
    public ProxyManager(final BaseConfigurationAccessor configurationAccessor) {
        this.configurationAccessor = configurationAccessor;
    }

    private Optional<ConfigurationModel> getSettingsConfiguration() {
        try {
            return configurationAccessor.getConfigurationByDescriptorNameAndContext(SettingsDescriptor.SETTINGS_COMPONENT, ConfigContextEnum.GLOBAL)
                       .stream()
                       .findFirst();
        } catch (AlertDatabaseConstraintException ex) {
            logger.error("Could not find the settings configuration for proxy data", ex);
        }
        return Optional.empty();
    }

    public ProxyInfo createProxyInfo() throws IllegalArgumentException {
        Optional<ConfigurationModel> settingsConfiguration = getSettingsConfiguration();
        final Optional<String> alertProxyHost = settingsConfiguration.flatMap(configurationModel -> configurationModel.getField(SettingsDescriptor.KEY_PROXY_HOST)).flatMap(ConfigurationFieldModel::getFieldValue);
        final Optional<String> alertProxyPort = settingsConfiguration.flatMap(configurationModel -> configurationModel.getField(SettingsDescriptor.KEY_PROXY_PORT)).flatMap(ConfigurationFieldModel::getFieldValue);
        final Optional<String> alertProxyUsername = settingsConfiguration.flatMap(configurationModel -> configurationModel.getField(SettingsDescriptor.KEY_PROXY_USERNAME)).flatMap(ConfigurationFieldModel::getFieldValue);
        final Optional<String> alertProxyPassword = settingsConfiguration.flatMap(configurationModel -> configurationModel.getField(SettingsDescriptor.KEY_PROXY_PASSWORD)).flatMap(ConfigurationFieldModel::getFieldValue);

        final ProxyInfoBuilder proxyBuilder = new ProxyInfoBuilder();
        if (alertProxyHost.isPresent()) {
            proxyBuilder.setHost(alertProxyHost.get());
        }
        if (alertProxyPort.isPresent()) {
            proxyBuilder.setPort(NumberUtils.toInt(alertProxyPort.get()));
        }
        final CredentialsBuilder credentialsBuilder = new CredentialsBuilder();
        if (alertProxyUsername.isPresent()) {
            credentialsBuilder.setUsername(alertProxyUsername.get());
        }
        if (alertProxyPassword.isPresent()) {
            credentialsBuilder.setPassword(alertProxyPassword.get());
        }
        proxyBuilder.setCredentials(credentialsBuilder.build());
        return proxyBuilder.build();
    }
}