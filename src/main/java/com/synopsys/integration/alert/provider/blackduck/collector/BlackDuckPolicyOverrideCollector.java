/**
 * blackduck-alert
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
package com.synopsys.integration.alert.provider.blackduck.collector;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.enumeration.ItemOperation;
import com.synopsys.integration.alert.common.message.model.CategoryItem;
import com.synopsys.integration.alert.common.message.model.LinkableItem;
import com.synopsys.integration.alert.common.rest.model.AlertNotificationWrapper;
import com.synopsys.integration.alert.common.workflow.filter.field.JsonExtractor;
import com.synopsys.integration.alert.common.workflow.filter.field.JsonField;
import com.synopsys.integration.alert.common.workflow.filter.field.JsonFieldAccessor;
import com.synopsys.integration.alert.common.workflow.processor.MessageContentProcessor;
import com.synopsys.integration.alert.provider.blackduck.BlackDuckProviderContentTypes;

@Component
@Scope("prototype")
public class BlackDuckPolicyOverrideCollector extends BlackDuckPolicyCollector {

    @Autowired
    public BlackDuckPolicyOverrideCollector(final JsonExtractor jsonExtractor,
        final List<MessageContentProcessor> messageContentProcessorList) {
        super(jsonExtractor, messageContentProcessorList, Arrays.asList(BlackDuckProviderContentTypes.POLICY_OVERRIDE));
    }

    @Override
    protected void addCategoryItems(final List<CategoryItem> categoryItems, final JsonFieldAccessor jsonFieldAccessor, final List<JsonField<?>> notificationFields, final AlertNotificationWrapper notificationContent) {
        final ItemOperation operation = ItemOperation.DELETE;
        final List<JsonField<String>> categoryFields = getStringFields(notificationFields);
        final List<LinkableItem> policyItems = getLinkableItemsByLabel(jsonFieldAccessor, categoryFields, BlackDuckProviderContentTypes.LABEL_POLICY_NAME);
        final List<LinkableItem> componentItems = getLinkableItemsByLabel(jsonFieldAccessor, categoryFields, BlackDuckProviderContentTypes.LABEL_COMPONENT_NAME);
        final List<LinkableItem> componentVersionItems = getLinkableItemsByLabel(jsonFieldAccessor, categoryFields, BlackDuckProviderContentTypes.LABEL_COMPONENT_VERSION_NAME);
        final Optional<LinkableItem> firstName = getLinkableItemsByLabel(jsonFieldAccessor, categoryFields, BlackDuckProviderContentTypes.LABEL_POLICY_OVERRIDE_FIRST_NAME).stream().findFirst();
        final Optional<LinkableItem> lastName = getLinkableItemsByLabel(jsonFieldAccessor, categoryFields, BlackDuckProviderContentTypes.LABEL_POLICY_OVERRIDE_LAST_NAME).stream().findFirst();
        final SortedSet<LinkableItem> applicableItems = new TreeSet<>();
        applicableItems.addAll(componentItems);
        applicableItems.addAll(componentVersionItems);

        if (firstName.isPresent() && lastName.isPresent()) {
            final String value = String.format("%s %s", firstName.get().getValue(), lastName.get().getValue());
            applicableItems.add(new LinkableItem(BlackDuckProviderContentTypes.LABEL_POLICY_OVERRIDE_BY, value));
        }

        for (final LinkableItem policyItem : policyItems) {
            final String policyUrl = policyItem.getUrl().orElse("");
            addApplicableItems(categoryItems, notificationContent.getId(), policyItem, policyUrl, operation, applicableItems);
        }
    }
}
