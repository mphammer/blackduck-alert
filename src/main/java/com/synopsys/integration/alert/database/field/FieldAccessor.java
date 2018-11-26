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

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.database.field.repository.FieldValuesEntityRepository;
import com.synopsys.integration.alert.database.field.repository.GroupingEntityRepository;

@Component
public class FieldAccessor {
    private final FieldValuesEntityRepository fieldValuesEntityRepository;
    private final GroupingEntityRepository groupingEntityRepository;

    @Autowired
    public FieldAccessor(final FieldValuesEntityRepository fieldValuesEntityRepository, final GroupingEntityRepository groupingEntityRepository) {
        this.fieldValuesEntityRepository = fieldValuesEntityRepository;
        this.groupingEntityRepository = groupingEntityRepository;
    }

    public Collection<FieldValuesEntity> findFieldsOfGrouping(final Long groupingId) {
        return fieldValuesEntityRepository.findByGroupingId(groupingId);
    }

    public FieldValuesEntity saveField(final FieldValuesEntity fieldValuesEntity) {
        return fieldValuesEntityRepository.save(fieldValuesEntity);
    }

    public void deleteFieldById(final Long fieldId) {
        fieldValuesEntityRepository.deleteById(fieldId);
    }

    public Collection<GroupingEntity> findGroupingsByDescriptorTypeAndDisplayType(final String descriptorType, final String displayType) {
        return groupingEntityRepository.findByDescriptorTypeAndDisplayType(descriptorType, displayType);
    }

    public Collection<GroupingEntity> findGroupingsByDescriptorType(final String descriptorType) {
        return groupingEntityRepository.findByDescriptorType(descriptorType);
    }

    public Optional<GroupingEntity> findGroupingById(final Long groupingId) {
        return groupingEntityRepository.findById(groupingId);
    }

    public GroupingEntity saveGrouping(final GroupingEntity groupingEntity) {
        return groupingEntityRepository.save(groupingEntity);
    }

    public void deleteGroupingAndFieldsById(final Long groupingId) {
        fieldValuesEntityRepository.deleteByGroupingId(groupingId);
        groupingEntityRepository.deleteById(groupingId);
    }
}