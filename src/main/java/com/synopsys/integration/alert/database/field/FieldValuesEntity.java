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
@Table(schema = "alert", name = "field_values")
public class FieldValuesEntity extends DatabaseEntity {

    @Column(name = "key")
    private final String key;
    @Column(name = "value")
    private final String value;
    @Column(name = "secure")
    private final Boolean secure;
    @Column(name = "grouping_id")
    private Long groupingId;

    public FieldValuesEntity(final Long groupingId, final String key, final String value, final Boolean secure) {
        this.groupingId = groupingId;
        this.key = key;
        this.value = value;
        this.secure = secure;
    }

    public Long getGroupingId() {
        return groupingId;
    }

    public void setGroupingId(final Long groupingId) {
        this.groupingId = groupingId;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public Boolean getSecure() {
        return secure;
    }
}
