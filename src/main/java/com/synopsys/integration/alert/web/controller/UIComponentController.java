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
package com.synopsys.integration.alert.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synopsys.integration.alert.common.descriptor.ChannelDescriptor;
import com.synopsys.integration.alert.common.descriptor.Descriptor;
import com.synopsys.integration.alert.common.descriptor.DescriptorMap;
import com.synopsys.integration.alert.common.descriptor.ProviderDescriptor;
import com.synopsys.integration.alert.common.descriptor.config.UIComponent;
import com.synopsys.integration.alert.common.descriptor.config.UIConfig;
import com.synopsys.integration.alert.common.descriptor.config.field.ConfigField;
import com.synopsys.integration.alert.common.descriptor.config.field.SelectConfigField;
import com.synopsys.integration.alert.common.descriptor.config.field.TextInputConfigField;
import com.synopsys.integration.alert.common.enumeration.ActionApiType;
import com.synopsys.integration.alert.common.enumeration.FrequencyType;
import com.synopsys.integration.alert.web.api.ResourceLinks;
import com.synopsys.integration.alert.web.model.UIResource;

@RestController
@RequestMapping(UIComponentController.DESCRIPTOR_PATH)
public class UIComponentController extends BaseController {
    public static final String DESCRIPTOR_PATH = BASE_PATH + "/descriptor";
    private final DescriptorMap descriptorMap;
    private final ResourceLinks resourceLinks;

    @Autowired
    public UIComponentController(final DescriptorMap descriptorMap, final ResourceLinks resourceLinks) {
        this.descriptorMap = descriptorMap;
        this.resourceLinks = resourceLinks;
    }

    @GetMapping
    public Collection<UIResource> getDescriptors(@RequestParam(value = "descriptorName", required = false) final String descriptorName, @RequestParam(value = "descriptorType", required = false) final String descriptorConfigType) {
        // filter by name
        if (StringUtils.isNotBlank(descriptorName)) {
            final Descriptor descriptor = descriptorMap.getDescriptor(descriptorName);
            if (descriptor != null) {
                // filter by type also
                if (StringUtils.isNotBlank(descriptorConfigType)) {
                    final ActionApiType descriptorType = ActionApiType.getRestApiType(descriptorConfigType);
                    final UIConfig uiConfig = descriptor.getUIConfig(descriptorType);
                    if (uiConfig != null) {
                        final UIComponent uiComponent = uiConfig.generateUIComponent();
                        return Arrays.asList(createUIResourceWithLinks(uiComponent, descriptor));
                    } else {
                        return Collections.emptyList();
                    }
                } else {
                    // name only
                    final List<UIComponent> uiComponents = descriptor.getAllUIConfigs().stream().map(UIConfig::generateUIComponent).collect(Collectors.toList());
                    return createUIResourcesWithLinks(uiComponents, descriptor);
                }
            } else {
                return Collections.emptyList();
            }
        } else if (StringUtils.isNotBlank(descriptorConfigType)) {
            //            final ActionApiType descriptorConfigTypeEnum = ActionApiType.getRestApiType(descriptorConfigType);
            //            List<UIComponent> uiComponents = descriptorMap.getUIComponents(descriptorConfigTypeEnum);
            //            return createUIResourcesWithLinks(uiComponents, des);
        } else {
            //            return descriptorMap.getAllUIComponents();
        }
        return Collections.emptyList();
    }

    @GetMapping("/distribution")
    public UIResource getDistributionUIComponent(@RequestParam(value = "providerName", required = true) final String providerName, @RequestParam(value = "channelName", required = true) final String channelName) {
        if (StringUtils.isBlank(providerName) || StringUtils.isBlank(channelName)) {
            return null;
        } else {
            final ProviderDescriptor providerDescriptor = descriptorMap.getProviderDescriptor(providerName);
            final ChannelDescriptor channelDescriptor = descriptorMap.getChannelDescriptor(channelName);
            final UIConfig channelUIConfig = channelDescriptor.getUIConfig(ActionApiType.CHANNEL_DISTRIBUTION_CONFIG);
            final UIConfig providerUIConfig = providerDescriptor.getUIConfig(ActionApiType.PROVIDER_DISTRIBUTION_CONFIG);
            final UIComponent channelUIComponent = channelUIConfig.generateUIComponent();
            final UIComponent providerUIComponent = providerUIConfig.generateUIComponent();
            final List<ConfigField> combinedFields = new ArrayList<>();
            final ConfigField name = new TextInputConfigField("name", "Name", true, false);
            final ConfigField frequency = new SelectConfigField("frequency", "Frequency", true, false, Arrays.stream(FrequencyType.values()).map(type -> type.getDisplayName()).collect(Collectors.toList()));
            combinedFields.add(name);
            combinedFields.add(frequency);
            combinedFields.addAll(channelUIComponent.getFields());
            combinedFields.addAll(providerUIComponent.getFields());

            final UIComponent combinedUIComponent = new UIComponent(channelUIComponent.getLabel(), channelUIComponent.getUrlName(), channelUIComponent.getDescriptorName(), channelUIComponent.getFontAwesomeIcon(),
                channelUIComponent.isAutomaticallyGenerateUI(), combinedFields);
            final UIResource uiResource = createUIResourceWithLinks(combinedUIComponent, channelDescriptor);
            return uiResource;
        }
    }

    private UIResource createUIResourceWithLinks(final UIComponent uiComponent, final Descriptor descriptor) {
        final UIResource uiResource = new UIResource(uiComponent);
        final String link = descriptor.getApiLink();
        final String testLink = descriptor.getTestLink();
        final String validationLink = descriptor.getValidateLink();
        resourceLinks.addRelationWithTitle(uiResource, link, descriptor.getTypeValue(), "config");
        resourceLinks.addRelationWithTitle(uiResource, testLink, descriptor.getTypeValue(), "test");
        resourceLinks.addRelationWithTitle(uiResource, validationLink, descriptor.getTypeValue(), "validate");
        return uiResource;
    }

    private List<UIResource> createUIResourcesWithLinks(final List<UIComponent> uiComponents, final Descriptor descriptor) {
        return uiComponents.stream()
                   .map(uiComponent -> createUIResourceWithLinks(uiComponent, descriptor))
                   .collect(Collectors.toList());
    }

}
