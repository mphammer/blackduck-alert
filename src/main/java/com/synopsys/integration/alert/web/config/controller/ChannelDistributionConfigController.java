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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.synopsys.integration.alert.common.descriptor.DescriptorMap;
import com.synopsys.integration.alert.common.descriptor.config.DescriptorActionApi;
import com.synopsys.integration.alert.common.enumeration.ActionApiType;
import com.synopsys.integration.alert.web.channel.actions.ChannelDistributionConfigActions;
import com.synopsys.integration.alert.web.config.controller.handler.ConfigControllerHandler;
import com.synopsys.integration.alert.web.model.CommonDistributionConfig;

@RestController
@RequestMapping(ConfigController.CHANNEL_CONFIG + "/distribution")
public class ChannelDistributionConfigController extends ConfigController {
    private final ConfigControllerHandler configControllerHandler;
    private final DescriptorMap descriptorMap;
    private final ChannelDistributionConfigActions channelDistributionConfigActions;

    @Autowired
    public ChannelDistributionConfigController(final DescriptorMap descriptorMap, final ChannelDistributionConfigActions channelDistributionConfigActions, final ConfigControllerHandler configControllerHandler) {
        this.descriptorMap = descriptorMap;
        this.channelDistributionConfigActions = channelDistributionConfigActions;
        this.configControllerHandler = configControllerHandler;
    }

    @GetMapping()
    public List<ResponseEntity<? extends ResourceSupport>> getConfig() {
        final List<ResponseEntity<? extends ResourceSupport>> resources = new ArrayList<>();
        final Set<String> descriptorNames = descriptorMap.getChannelDescriptorMap().keySet();
        for (final String descriptorName : descriptorNames) {
            final ResponseEntity<? extends ResourceSupport> response = getConfig(null, descriptorName);
            resources.add(response);
        }
        return resources;
    }

    @Override
    @GetMapping("/{descriptorName}")
    public ResponseEntity<? extends ResourceSupport> getConfig(final Long id, @PathVariable final String descriptorName) {
        final DescriptorActionApi descriptor = descriptorMap.getChannelDescriptor(descriptorName).getRestApi(ActionApiType.CHANNEL_DISTRIBUTION_CONFIG);
        return configControllerHandler.getConfig(id, descriptorName, descriptor, channelDistributionConfigActions, getClass());
    }

    @Override
    @PostMapping("/{descriptorName}")
    public ResponseEntity<? extends ResourceSupport> postConfig(@RequestBody(required = false) final String restModel, @PathVariable final String descriptorName) {
        final DescriptorActionApi descriptor = descriptorMap.getChannelDescriptor(descriptorName).getRestApi(ActionApiType.CHANNEL_DISTRIBUTION_CONFIG);
        final CommonDistributionConfig parsedRestModel = (CommonDistributionConfig) descriptor.getConfigFromJson(restModel);
        return configControllerHandler.postConfig(parsedRestModel, descriptorName, descriptor, channelDistributionConfigActions, getClass());
    }

    @Override
    @PutMapping("/{descriptorName}")
    public ResponseEntity<? extends ResourceSupport> putConfig(@RequestBody(required = false) final String restModel, @PathVariable final String descriptorName) {
        final DescriptorActionApi descriptor = descriptorMap.getChannelDescriptor(descriptorName).getRestApi(ActionApiType.CHANNEL_DISTRIBUTION_CONFIG);
        final CommonDistributionConfig parsedRestModel = (CommonDistributionConfig) descriptor.getConfigFromJson(restModel);
        return configControllerHandler.putConfig(parsedRestModel, descriptorName, descriptor, channelDistributionConfigActions, getClass());
    }

    @Override
    @PostMapping("/{descriptorName}/validate")
    public ResponseEntity<? extends ResourceSupport> validateConfig(@RequestBody(required = false) final String restModel, @PathVariable final String descriptorName) {
        final DescriptorActionApi descriptor = descriptorMap.getChannelDescriptor(descriptorName).getRestApi(ActionApiType.CHANNEL_DISTRIBUTION_CONFIG);
        final CommonDistributionConfig parsedRestModel = (CommonDistributionConfig) descriptor.getConfigFromJson(restModel);
        return configControllerHandler.validateConfig(parsedRestModel, descriptorName, descriptor, channelDistributionConfigActions, getClass());
    }

    @Override
    @DeleteMapping("/{descriptorName}")
    public ResponseEntity<? extends ResourceSupport> deleteConfig(final Long id, @PathVariable final String descriptorName) {
        final DescriptorActionApi descriptor = descriptorMap.getChannelDescriptor(descriptorName).getRestApi(ActionApiType.CHANNEL_DISTRIBUTION_CONFIG);
        return configControllerHandler.deleteConfig(id, descriptorName, descriptor, channelDistributionConfigActions, getClass());
    }

    @Override
    @PostMapping("/{descriptorName}/test")
    public ResponseEntity<? extends ResourceSupport> testConfig(@RequestBody(required = false) final String restModel, @PathVariable final String descriptorName) {
        final DescriptorActionApi descriptor = descriptorMap.getChannelDescriptor(descriptorName).getRestApi(ActionApiType.CHANNEL_DISTRIBUTION_CONFIG);
        final CommonDistributionConfig parsedRestModel = (CommonDistributionConfig) descriptor.getConfigFromJson(restModel);
        return configControllerHandler.testConfig(parsedRestModel, descriptorName, descriptor, channelDistributionConfigActions, getClass());
    }

}
