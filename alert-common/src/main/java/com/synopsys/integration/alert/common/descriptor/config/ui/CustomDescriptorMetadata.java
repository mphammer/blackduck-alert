/**
 * alert-common
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
package com.synopsys.integration.alert.common.descriptor.config.ui;

import java.util.Collections;

import com.synopsys.integration.alert.common.enumeration.ConfigContextEnum;
import com.synopsys.integration.alert.common.enumeration.DescriptorType;

public class CustomDescriptorMetadata extends DescriptorMetadata {
    private String uiComponentName;

    public CustomDescriptorMetadata(final String label, final String urlName, final String name, final DescriptorType type, final ConfigContextEnum context, final String fontAwesomeIcon, final String uiComponentName) {
        super(label, urlName, name, type, context, fontAwesomeIcon, false, Collections.emptyList());
        this.uiComponentName = uiComponentName;
    }

    public String getUiComponentName() {
        return uiComponentName;
    }

    public void setUiComponentName(final String uiComponentName) {
        this.uiComponentName = uiComponentName;
    }

}
