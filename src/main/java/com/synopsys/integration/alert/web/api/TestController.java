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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synopsys.integration.alert.web.config.controller.ChannelDistributionConfigController;
import com.synopsys.integration.alert.web.controller.TestResponseBody;

@RestController
public class TestController {

    @RequestMapping("api/testing")
    public HttpEntity<TestResponseBody> testing(@RequestParam(value = "name", required = false, defaultValue = "Empty") final String content) {
        final TestResponseBody testResponseBody = new TestResponseBody(content);
        testResponseBody.add(linkTo(methodOn(TestController.class).testing(content)).withSelfRel());
        testResponseBody.add(linkTo(ChannelDistributionConfigController.class).slash("Test").withRel("distribution"));
        testResponseBody.add(linkTo(methodOn(ChannelDistributionConfigController.class).testConfig("Testing", "MyOwnDescriptor")).withRel("distribution"));
        testResponseBody.add(linkTo(methodOn(ChannelDistributionConfigController.class).validateConfig("Validating", "MyOwnDescriptor")).withRel("distribution"));
        testResponseBody.add(new Link("https://google.com", "paulo\test").withTitle("Title"));

        return new ResponseEntity<>(testResponseBody, HttpStatus.OK);
    }
}
