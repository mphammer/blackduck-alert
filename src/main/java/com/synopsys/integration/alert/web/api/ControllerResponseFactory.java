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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.ContentConverter;
import com.synopsys.integration.alert.web.config.controller.ConfigController;
import com.synopsys.integration.alert.web.model.Config;
import com.synopsys.integration.alert.web.model.ResponseBodyBuilder;

@Component
public class ControllerResponseFactory {
    private final APILinkCreator apiLinkCreator;
    private final ContentConverter contentConverter;

    @Autowired
    public ControllerResponseFactory(final APILinkCreator apiLinkCreator, final ContentConverter contentConverter) {
        this.apiLinkCreator = apiLinkCreator;
        this.contentConverter = contentConverter;
    }

    public ContentConverter getContentConverter() {
        return contentConverter;
    }

    public ResponseEntity<ConfigResource> createConfigResourcesWithAllLinks(final List<? extends Config> config, final String descriptorName, final Class<? extends ConfigController> controllerClass) {
        final ConfigResource configResource = new ConfigResource(config);
        apiLinkCreator.addAllLinks(configResource, descriptorName, controllerClass);

        return ResponseEntity.ok(configResource);
    }

    public ResponseEntity<MessageResource> createResponseWithAllLinks(final HttpStatus httpStatus, final Long id, final String message, final String descriptorName, final Class<? extends ConfigController> controllerClass) {
        final String convertedId = contentConverter.getStringValue(id);
        return createResponseWithAllLinks(httpStatus, convertedId, message, descriptorName, controllerClass);
    }

    public ResponseEntity<MessageResource> createResponseWithAllLinks(final HttpStatus httpStatus, final String id, final String message, final String descriptorName, final Class<? extends ConfigController> controllerClass) {
        final MessageResource messageResource = createMessageResource(id, message, Collections.emptyMap());
        apiLinkCreator.addAllLinks(messageResource, descriptorName, controllerClass);

        return ResponseEntity.status(httpStatus).body(messageResource);
    }

    public MessageResource createMessageResource(final String id, final String message, final Map<String, String> errors) {
        final Long convertedId = contentConverter.getLongValue(id);
        final ResponseBodyBuilder responseBodyBuilder = new ResponseBodyBuilder(convertedId, message);
        responseBodyBuilder.putErrors(errors);
        return new MessageResource(responseBodyBuilder.build());
    }

    public ResponseEntity<MessageResource> createResponseWithoutLinks(final HttpStatus httpStatus, final Long id, final String message) {
        final String convertedId = contentConverter.getStringValue(id);
        return createResponseWithoutLinksWithErrors(httpStatus, convertedId, message, Collections.emptyMap());
    }

    public ResponseEntity<MessageResource> createResponseWithoutLinks(final HttpStatus httpStatus, final String id, final String message) {
        return createResponseWithoutLinksWithErrors(httpStatus, id, message, Collections.emptyMap());
    }

    public ResponseEntity<MessageResource> createResponseWithoutLinksWithErrors(final HttpStatus httpStatus, final String id, final String message, final Map<String, String> errors) {
        final MessageResource messageResource = createMessageResource(id, message, errors);
        return ResponseEntity.status(httpStatus).body(messageResource);
    }

    public ResponseEntity<MessageResource> createMethodNotAllowed() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    public ResponseEntity<MessageResource> createMethodNotAllowed(final String id, final String message) {
        final MessageResource messageResource = createMessageResource(id, message, Collections.emptyMap());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(messageResource);
    }

    public ResponseEntity<MessageResource> createMissingRequestBody() {
        final MessageResource messageResource = new MessageResource("Required request body is missing.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageResource);
    }

}
