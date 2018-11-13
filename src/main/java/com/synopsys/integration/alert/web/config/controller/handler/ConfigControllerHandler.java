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
package com.synopsys.integration.alert.web.config.controller.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.descriptor.config.DescriptorActionApi;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.database.entity.DatabaseEntity;
import com.synopsys.integration.alert.web.api.ControllerResponseFactory;
import com.synopsys.integration.alert.web.config.actions.DescriptorConfigActions;
import com.synopsys.integration.alert.web.config.controller.ConfigController;
import com.synopsys.integration.alert.web.exception.AlertFieldException;
import com.synopsys.integration.alert.web.model.Config;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.exception.IntegrationRestException;

@Component
public class ConfigControllerHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ControllerResponseFactory controllerResponseFactory;

    @Autowired
    public ConfigControllerHandler(final ControllerResponseFactory controllerResponseFactory) {
        this.controllerResponseFactory = controllerResponseFactory;
    }

    public ResponseEntity<? extends ResourceSupport> getConfig(final Long id, final String descriptorName, final DescriptorActionApi descriptor, final DescriptorConfigActions descriptorConfigActions,
        final Class<? extends ConfigController> controllerClass) {
        try {
            final List<? extends Config> configs = descriptorConfigActions.getConfig(id, descriptor);
            return controllerResponseFactory.createConfigResourcesWithAllLinks(configs, descriptorName, controllerClass);
        } catch (final AlertException e) {
            logger.error(e.getMessage(), e);
        }
        return controllerResponseFactory.createConfigResourcesWithAllLinks(Collections.emptyList(), descriptorName, controllerClass);
    }

    public ResponseEntity<? extends ResourceSupport> postConfig(final Config restModel, final String descriptorName, final DescriptorActionApi descriptor, final DescriptorConfigActions descriptorConfigActions,
        final Class<? extends ConfigController> controllerClass) {
        if (restModel == null) {
            return controllerResponseFactory.createMissingRequestBody();
        }
        if (!descriptorConfigActions.doesConfigExist(restModel.getId(), descriptor)) {
            try {
                descriptorConfigActions.validateConfig(restModel, descriptor, new HashMap<>());
                final DatabaseEntity updatedEntity = descriptorConfigActions.saveConfig(restModel, descriptor);
                return controllerResponseFactory.createResponseWithAllLinks(HttpStatus.CREATED, updatedEntity.getId(), "Created", descriptorName, controllerClass);
            } catch (final AlertFieldException e) {
                return controllerResponseFactory.createResponseWithoutLinksWithErrors(HttpStatus.BAD_REQUEST, restModel.getId(), "There were errors with the configuration.", e.getFieldErrors());
            }
        }
        return controllerResponseFactory.createResponseWithoutLinks(HttpStatus.CONFLICT, restModel.getId(), "Provided id must not be in use. To update an existing configuration, use PUT.");
    }

    public ResponseEntity<? extends ResourceSupport> putConfig(final Config restModel, final String descriptorName, final DescriptorActionApi descriptor, final DescriptorConfigActions descriptorConfigActions,
        final Class<? extends ConfigController> controllerClass) {
        if (restModel == null) {
            return controllerResponseFactory.createMissingRequestBody();
        }
        if (descriptorConfigActions.doesConfigExist(restModel.getId(), descriptor)) {
            try {
                descriptorConfigActions.validateConfig(restModel, descriptor, new HashMap<>());
                try {
                    final DatabaseEntity updatedEntity = descriptorConfigActions.updateConfig(restModel, descriptor);
                    return controllerResponseFactory.createResponseWithAllLinks(HttpStatus.ACCEPTED, updatedEntity.getId(), "Updated", descriptorName, controllerClass);
                } catch (final AlertException e) {
                    logger.error(e.getMessage(), e);
                    return controllerResponseFactory.createResponseWithoutLinks(HttpStatus.INTERNAL_SERVER_ERROR, restModel.getId(), e.getMessage());
                }
            } catch (final AlertFieldException e) {
                return controllerResponseFactory.createResponseWithoutLinksWithErrors(HttpStatus.BAD_REQUEST, restModel.getId(), "There were errors with the configuration.", e.getFieldErrors());
            }
        }
        return controllerResponseFactory.createResponseWithoutLinks(HttpStatus.BAD_REQUEST, restModel.getId(), "No configuration with the specified id.");
    }

    public ResponseEntity<? extends ResourceSupport> deleteConfig(final Long id, final String descriptorName, final DescriptorActionApi descriptor, final DescriptorConfigActions descriptorConfigActions,
        final Class<? extends ConfigController> controllerClass) {
        if (id != null && descriptorConfigActions.doesConfigExist(id, descriptor)) {
            descriptorConfigActions.deleteConfig(id, descriptor);
            return controllerResponseFactory.createResponseWithAllLinks(HttpStatus.ACCEPTED, id, "Deleted", descriptorName, controllerClass);
        }
        return controllerResponseFactory.createResponseWithoutLinks(HttpStatus.BAD_REQUEST, id, "No configuration with the specified id.");
    }

    public ResponseEntity<? extends ResourceSupport> validateConfig(final Config restModel, final String descriptorName, final DescriptorActionApi descriptor, final DescriptorConfigActions descriptorConfigActions,
        final Class<? extends ConfigController> controllerClass) {
        if (restModel == null) {
            return controllerResponseFactory.createMissingRequestBody();
        }
        try {
            final String responseMessage = descriptorConfigActions.validateConfig(restModel, descriptor, new HashMap<>());
            return controllerResponseFactory.createResponseWithAllLinks(HttpStatus.OK, restModel.getId(), responseMessage, descriptorName, controllerClass);
        } catch (final AlertFieldException e) {
            return controllerResponseFactory.createResponseWithoutLinksWithErrors(HttpStatus.BAD_REQUEST, restModel.getId(), e.getMessage(), e.getFieldErrors());
        }
    }

    public ResponseEntity<? extends ResourceSupport> testConfig(final Config restModel, final String descriptorName, final DescriptorActionApi descriptor, final DescriptorConfigActions descriptorConfigActions,
        final Class<? extends ConfigController> controllerClass) {
        if (restModel == null) {
            return controllerResponseFactory.createMissingRequestBody();
        }
        try {
            final String responseMessage = descriptorConfigActions.testConfig(restModel, descriptor);
            return controllerResponseFactory.createResponseWithAllLinks(HttpStatus.OK, restModel.getId(), responseMessage, descriptorName, controllerClass);
        } catch (final IntegrationRestException e) {
            logger.error(e.getMessage(), e);
            final String errorMessage = e.getHttpStatusMessage() + " : " + e.getMessage();
            final HttpStatus httpStatus = HttpStatus.valueOf(e.getHttpStatusCode());
            return controllerResponseFactory.createResponseWithoutLinks(httpStatus, restModel.getId(), errorMessage);
        } catch (final AlertFieldException e) {
            return controllerResponseFactory.createResponseWithoutLinksWithErrors(HttpStatus.BAD_REQUEST, restModel.getId(), e.getMessage(), e.getFieldErrors());
        } catch (final AlertException e) {
            return controllerResponseFactory.createResponseWithoutLinks(HttpStatus.BAD_REQUEST, restModel.getId(), e.getMessage());
        } catch (final IntegrationException e) {
            logger.error(e.getMessage(), e);
            return controllerResponseFactory.createMethodNotAllowed(restModel.getId(), e.getMessage());
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            return controllerResponseFactory.createResponseWithoutLinks(HttpStatus.INTERNAL_SERVER_ERROR, restModel.getId(), e.getMessage());
        }
    }
}
