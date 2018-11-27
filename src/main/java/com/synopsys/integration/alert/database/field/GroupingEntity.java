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
package com.synopsys.integration.alert.database.field;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.synopsys.integration.alert.database.entity.DatabaseEntity;

@Entity
@Table(schema = "alert", name = "grouping")
public class GroupingEntity extends DatabaseEntity {

    @Column(name = "descriptor_type")
    private final String descriptorType;

    @Column(name = "descriptor_name")
    private final String descriptorName;

    @Column(name = "display_type")
    private final String displayType;

    public GroupingEntity(final String descriptorType, final String descriptorName, final String displayType) {
        this.descriptorType = descriptorType;
        this.descriptorName = descriptorName;
        this.displayType = displayType;
    }

    public String getDescriptorType() {
        return descriptorType;
    }

    public String getDescriptorName() {
        return descriptorName;
    }

    public String getDisplayType() {
        return displayType;
    }
}