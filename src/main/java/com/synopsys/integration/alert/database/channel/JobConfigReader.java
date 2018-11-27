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
package com.synopsys.integration.alert.database.channel;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.enumeration.DescriptorType;
import com.synopsys.integration.alert.common.field.CommonDistributionFields;
import com.synopsys.integration.alert.database.field.FieldAccessor;
import com.synopsys.integration.alert.database.field.FieldEntityWrapper;
import com.synopsys.integration.alert.database.field.GroupingEntity;

@Component
public class JobConfigReader {
    private final FieldAccessor fieldAccessor;

    @Autowired
    public JobConfigReader(final FieldAccessor fieldAccessor) {
        this.fieldAccessor = fieldAccessor;
    }

    public Collection<CommonDistributionFields> getFields() {
        final Collection<GroupingEntity> groupingEntities = fieldAccessor.findGroupingsByDescriptorType(DescriptorType.CHANNEL.name());
        final Collection<FieldEntityWrapper> fieldEntityWrappers = fieldAccessor.createFieldEntityWrappersFromGroupings(groupingEntities);
        return fieldEntityWrappers.stream()
                   .map(fieldEntityWrapper -> new CommonDistributionFields(fieldEntityWrapper))
                   .collect(Collectors.toList());
    }

    public Optional<CommonDistributionFields> getFields(final Long groupingId) {
        if (null == groupingId) {
            return Optional.empty();
        }
        final Optional<GroupingEntity> foundEntity = fieldAccessor.findGroupingById(groupingId);
        if (foundEntity.isPresent()) {
            final GroupingEntity groupingEntity = foundEntity.get();
            final FieldEntityWrapper fieldEntityWrapper = fieldAccessor.createFieldEntityWrapperFromGrouping(groupingEntity);
            return Optional.of(new CommonDistributionFields(fieldEntityWrapper));
        } else {
            return Optional.empty();
        }
    }

}
