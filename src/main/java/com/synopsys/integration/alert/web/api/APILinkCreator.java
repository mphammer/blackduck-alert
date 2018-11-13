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
package com.synopsys.integration.alert.web.api;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.web.config.controller.ConfigController;
import com.synopsys.integration.alert.web.controller.UIComponentController;

@Component
public class APILinkCreator {

    public Link createUIConfigLink(final String descriptorName) {
        return linkTo(methodOn(UIComponentController.class).getUIResourceForDescriptor(descriptorName, ""))
                   .withRel("fields")
                   .withType(HttpMethod.GET.toString());
    }

    public Link createGetConfigLink(final Class<? extends ConfigController> controllerClass, final String descriptorName) {
        return linkTo(methodOn(controllerClass).getConfig(0l, descriptorName))
                   .withRel("config")
                   .withType(HttpMethod.GET.toString());
    }

    public Link createPostConfigLink(final Class<? extends ConfigController> controllerClass, final String descriptorName) {
        return linkTo(methodOn(controllerClass).postConfig(null, descriptorName))
                   .withRel("config")
                   .withType(HttpMethod.POST.toString());
    }

    public Link createPutConfigLink(final Class<? extends ConfigController> controllerClass, final String descriptorName) {
        return linkTo(methodOn(controllerClass).putConfig(null, descriptorName))
                   .withRel("config")
                   .withType(HttpMethod.PUT.toString());
    }

    public Link createDeleteConfigLink(final Class<? extends ConfigController> controllerClass, final String descriptorName) {
        return linkTo(methodOn(controllerClass).deleteConfig(0l, descriptorName))
                   .withRel("config")
                   .withType(HttpMethod.DELETE.toString());
    }

    public Link createValidateConfigLink(final Class<? extends ConfigController> controllerClass, final String descriptorName) {
        return linkTo(methodOn(controllerClass).validateConfig(null, descriptorName))
                   .withRel("config")
                   .withType(HttpMethod.POST.toString());
    }

    public Link createTestConfigLink(final Class<? extends ConfigController> controllerClass, final String descriptorName) {
        return linkTo(methodOn(controllerClass).testConfig(null, descriptorName))
                   .withRel("config")
                   .withType(HttpMethod.POST.toString());
    }

    public void addConfigLinks(final ResourceSupport resourceSupport, final String descriptorName, final Class<? extends ConfigController> controllerClass) {
        final Link getLink = createGetConfigLink(controllerClass, descriptorName);
        final Link postLink = createPostConfigLink(controllerClass, descriptorName);
        final Link putLink = createPutConfigLink(controllerClass, descriptorName);
        final Link deleteLink = createDeleteConfigLink(controllerClass, descriptorName);
        final Link validateLink = createValidateConfigLink(controllerClass, descriptorName);
        final Link testLink = createTestConfigLink(controllerClass, descriptorName);

        resourceSupport.add(getLink, postLink, putLink, deleteLink, validateLink, testLink);
    }

    public void addUILinks(final ResourceSupport resourceSupport, final String descriptorName) {
        final Link uiLink = createUIConfigLink(descriptorName);
        resourceSupport.add(uiLink);
    }

    public void addAllLinks(final ResourceSupport resourceSupport, final String descriptorName, final Class<? extends ConfigController> controllerClass) {
        addConfigLinks(resourceSupport, descriptorName, controllerClass);
        addUILinks(resourceSupport, descriptorName);
    }
}
