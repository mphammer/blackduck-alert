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
package com.synopsys.integration.alert.provider.blackduck;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.AlertProperties;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.database.provider.blackduck.BlackDuckEntity;
import com.synopsys.integration.alert.database.provider.blackduck.GlobalBlackDuckRepository;
import com.synopsys.integration.blackduck.configuration.HubServerConfig;
import com.synopsys.integration.blackduck.configuration.HubServerConfigBuilder;
import com.synopsys.integration.blackduck.rest.BlackduckRestConnection;
import com.synopsys.integration.blackduck.service.HubServicesFactory;
import com.synopsys.integration.exception.EncryptionException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

@Component
public class BlackDuckProperties {
    public static final int DEFAULT_TIMEOUT = 300;
    private final GlobalBlackDuckRepository globalBlackDuckRepository;
    private final AlertProperties alertProperties;

    private boolean needsUpdate;
    private HubServerConfig hubServerConfig;

    // the blackduck product hasn't renamed their environment variables from hub to blackduck
    // need to keep hub in the name until
    @Value("${public.hub.webserver.host:}")
    private String publicBlackDuckWebserverHost;

    @Value("${public.hub.webserver.port:}")
    private String publicBlackDuckWebserverPort;

    @Autowired
    public BlackDuckProperties(final GlobalBlackDuckRepository globalBlackDuckRepository, final AlertProperties alertProperties) {
        this.globalBlackDuckRepository = globalBlackDuckRepository;
        this.alertProperties = alertProperties;
    }

    public Optional<String> getBlackDuckUrl() {
        final Optional<BlackDuckEntity> optionalGlobalBlackDuckConfigEntity = getBlackDuckEntity();
        if (optionalGlobalBlackDuckConfigEntity.isPresent()) {
            final BlackDuckEntity blackDuckConfigEntity = optionalGlobalBlackDuckConfigEntity.get();
            if (StringUtils.isBlank(blackDuckConfigEntity.getBlackDuckUrl())) {
                return Optional.empty();
            } else {
                return Optional.of(blackDuckConfigEntity.getBlackDuckUrl());
            }
        }
        return Optional.empty();
    }

    public Optional<String> getPublicBlackDuckWebserverHost() {
        return getOptionalString(publicBlackDuckWebserverHost);
    }

    public Optional<String> getPublicBlackDuckWebserverPort() {
        return getOptionalString(publicBlackDuckWebserverPort);
    }

    private Optional<String> getOptionalString(final String value) {
        if (StringUtils.isNotBlank(value)) {
            return Optional.of(value);
        }
        return Optional.empty();
    }

    public Integer getBlackDuckTimeout() {
        final Optional<BlackDuckEntity> optionalGlobalBlackDuckConfigEntity = getBlackDuckEntity();
        if (optionalGlobalBlackDuckConfigEntity.isPresent()) {
            final BlackDuckEntity blackDuckConfigEntity = optionalGlobalBlackDuckConfigEntity.get();
            if (blackDuckConfigEntity.getBlackDuckTimeout() == null) {
                return DEFAULT_TIMEOUT;
            } else {
                return optionalGlobalBlackDuckConfigEntity.get().getBlackDuckTimeout();
            }
        }
        return DEFAULT_TIMEOUT;
    }

    public Optional<BlackDuckEntity> getBlackDuckEntity() {
        final List<BlackDuckEntity> configs = globalBlackDuckRepository.findAll();
        if (configs != null && !configs.isEmpty()) {
            return Optional.of(configs.get(0));
        }
        return Optional.empty();
    }

    public HubServicesFactory createBlackDuckServicesFactory(final BlackduckRestConnection restConnection, final IntLogger logger) {
        return new HubServicesFactory(HubServicesFactory.createDefaultGson(), HubServicesFactory.createDefaultJsonParser(), restConnection, logger);
    }

    public BlackduckRestConnection createRestConnection(final Logger logger) throws AlertException {
        final IntLogger intLogger = new Slf4jIntLogger(logger);
        final HubServerConfig foundHubServerConfig = getHubServerConfig(intLogger);
        final Optional<BlackduckRestConnection> optionalBlackDuckRestConnection = createRestConnectionAndLogErrors(intLogger, foundHubServerConfig);
        if (!optionalBlackDuckRestConnection.isPresent()) {
            markForUpdate();
            throw new AlertException("Black Duck connection could not be established.");
        }
        return optionalBlackDuckRestConnection.get();
    }

    public HubServerConfig getHubServerConfig(final IntLogger intLogger) throws AlertException {
        if (needsUpdate) {
            final Optional<HubServerConfig> optionalHubServerConfig = createBlackDuckServerConfig(intLogger);
            if (!optionalHubServerConfig.isPresent()) {
                markForUpdate();
                throw new AlertException("Server configuration is missing.");
            } else {
                setHubServerConfig(optionalHubServerConfig.get());
            }
        }

        return hubServerConfig;
    }

    public void markForUpdate() {
        needsUpdate = true;
    }

    private void setHubServerConfig(final HubServerConfig hubServerConfig) {
        this.hubServerConfig = hubServerConfig;
        needsUpdate = false;
    }

    private Optional<BlackduckRestConnection> createRestConnectionAndLogErrors(final IntLogger logger, final HubServerConfig hubServerConfig) {
        try {
            return Optional.of(hubServerConfig.createRestConnection(logger));
        } catch (final EncryptionException e) {
            logger.error(e.getMessage(), e);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
        return Optional.empty();
    }

    private Optional<HubServerConfig> createBlackDuckServerConfig(final IntLogger logger) throws AlertException {
        final Optional<BlackDuckEntity> optionalGlobalBlackDuckConfigEntity = getBlackDuckEntity();
        if (optionalGlobalBlackDuckConfigEntity.isPresent()) {
            final BlackDuckEntity globalHubConfigEntity = optionalGlobalBlackDuckConfigEntity.get();
            if (globalHubConfigEntity.getBlackDuckTimeout() == null || globalHubConfigEntity.getBlackDuckApiKey() == null) {
                throw new AlertException("Global config settings can not be null.");
            }
            final HubServerConfigBuilder blackDuckServerConfigBuilder = createServerConfigBuilderWithoutAuthentication(logger, globalHubConfigEntity.getBlackDuckTimeout());
            blackDuckServerConfigBuilder.setApiToken(globalHubConfigEntity.getBlackDuckApiKey());

            try {
                return Optional.of(blackDuckServerConfigBuilder.build());
            } catch (final IllegalStateException e) {
                throw new AlertException(e.getMessage(), e);
            }
        }
        return Optional.empty();
    }

    public HubServerConfigBuilder createServerConfigBuilderWithoutAuthentication(final IntLogger logger, final int blackDuckTimeout) {
        final HubServerConfigBuilder blackDuckServerConfigBuilder = new HubServerConfigBuilder();
        blackDuckServerConfigBuilder.setFromProperties(alertProperties.getBlackDuckProperties());
        blackDuckServerConfigBuilder.setLogger(logger);
        blackDuckServerConfigBuilder.setTimeout(blackDuckTimeout);
        blackDuckServerConfigBuilder.setUrl(getBlackDuckUrl().orElse(""));

        return blackDuckServerConfigBuilder;
    }

}
