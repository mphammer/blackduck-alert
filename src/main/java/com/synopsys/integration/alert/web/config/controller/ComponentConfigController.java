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
package com.synopsys.integration.alert.web.config.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.synopsys.integration.alert.common.descriptor.DescriptorMap;
import com.synopsys.integration.alert.common.descriptor.config.DescriptorActionApi;
import com.synopsys.integration.alert.common.enumeration.ActionApiType;
import com.synopsys.integration.alert.web.api.ControllerResponseFactory;
import com.synopsys.integration.alert.web.config.actions.SingleEntityConfigActions;
import com.synopsys.integration.alert.web.config.controller.handler.ConfigControllerHandler;

@RestController
@RequestMapping(ConfigController.COMPONENT_CONFIG + "/{descriptorName}")
public class ComponentConfigController extends ConfigController {
    private final DescriptorMap descriptorMap;
    private final ConfigControllerHandler configControllerHandler;
    private final SingleEntityConfigActions singleEntityConfigActions;
    private final ControllerResponseFactory controllerResponseFactory;

    @Autowired
    public ComponentConfigController(final DescriptorMap descriptorMap, final SingleEntityConfigActions singleEntityConfigActions, final ConfigControllerHandler configControllerHandler,
        final ControllerResponseFactory controllerResponseFactory) {
        this.descriptorMap = descriptorMap;
        this.configControllerHandler = configControllerHandler;
        this.singleEntityConfigActions = singleEntityConfigActions;
        this.controllerResponseFactory = controllerResponseFactory;
    }

    @Override
    public ResponseEntity<? extends ResourceSupport> getConfig(final Long id, @PathVariable final String descriptorName) {
        final DescriptorActionApi descriptor = descriptorMap.getComponentDescriptor(descriptorName).getRestApi(ActionApiType.COMPONENT_CONFIG);
        return configControllerHandler.getConfig(id, descriptorName, descriptor, singleEntityConfigActions, getClass());
    }

    @Override
    public ResponseEntity<? extends ResourceSupport> postConfig(@RequestBody(required = false) final String config, @PathVariable final String descriptorName) {
        final DescriptorActionApi descriptor = descriptorMap.getComponentDescriptor(descriptorName).getRestApi(ActionApiType.COMPONENT_CONFIG);
        return configControllerHandler.postConfig(descriptor.getConfigFromJson(config), descriptorName, descriptor, singleEntityConfigActions, getClass());
    }

    @Override
    public ResponseEntity<? extends ResourceSupport> putConfig(@RequestBody(required = false) final String config, @PathVariable final String descriptorName) {
        final DescriptorActionApi descriptor = descriptorMap.getComponentDescriptor(descriptorName).getRestApi(ActionApiType.COMPONENT_CONFIG);
        return configControllerHandler.putConfig(descriptor.getConfigFromJson(config), descriptorName, descriptor, singleEntityConfigActions, getClass());
    }

    @Override
    public ResponseEntity<? extends ResourceSupport> validateConfig(@RequestBody(required = false) final String config, @PathVariable final String descriptorName) {
        final DescriptorActionApi descriptor = descriptorMap.getComponentDescriptor(descriptorName).getRestApi(ActionApiType.COMPONENT_CONFIG);
        return configControllerHandler.validateConfig(descriptor.getConfigFromJson(config), descriptorName, descriptor, singleEntityConfigActions, getClass());
    }

    @Override
    public ResponseEntity<? extends ResourceSupport> deleteConfig(final Long id, final @PathVariable String descriptorName) {
        final DescriptorActionApi descriptor = descriptorMap.getComponentDescriptor(descriptorName).getRestApi(ActionApiType.COMPONENT_CONFIG);
        return configControllerHandler.deleteConfig(id, descriptorName, descriptor, singleEntityConfigActions, getClass());
    }

    @Override
    public ResponseEntity<? extends ResourceSupport> testConfig(@RequestBody(required = false) final String config, final @PathVariable String descriptorName) {
        return controllerResponseFactory.createMethodNotAllowed();
    }

}
