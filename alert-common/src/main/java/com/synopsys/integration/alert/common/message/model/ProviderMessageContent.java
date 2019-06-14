/**
 * alert-common
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.alert.common.message.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.builder.Buildable;

public class ProviderMessageContent implements Buildable {
    private static final String KEY_SEPARATOR = "_";
    private final LinkableItem provider;
    private final LinkableItem topic;
    private final LinkableItem subTopic;
    private final String contentKey;
    private final Set<ComponentItem> componentItems;

    private ProviderMessageContent(LinkableItem provider, LinkableItem topic, LinkableItem subTopic, String contentKey, Set<ComponentItem> componentItems) {
        this.provider = provider;
        this.topic = topic;
        this.subTopic = subTopic;
        this.contentKey = contentKey;
        this.componentItems = componentItems;
    }

    public static final String generateContentKey(String providerName, String topicName, String topicValue, String subTopicName, String subTopicValue) {
        final List<String> keyParts;
        if (StringUtils.isNotBlank(subTopicName)) {
            keyParts = List.of(providerName, topicName, topicValue, subTopicName, subTopicValue);
        } else {
            keyParts = List.of(providerName, topicName, topicValue);
        }
        return String.join(KEY_SEPARATOR, keyParts);
    }

    public LinkableItem getProvider() {
        return provider;
    }

    public LinkableItem getTopic() {
        return topic;
    }

    public Optional<LinkableItem> getSubTopic() {
        return Optional.ofNullable(subTopic);
    }

    public String getContentKey() {
        return contentKey;
    }

    public Set<ComponentItem> getComponentItems() {
        return componentItems;
    }

    public static class Builder {
        private static final String LABEL_PROVIDER = "Provider";

        private final Set<ComponentItem> componentItems = new LinkedHashSet<>();
        private String providerName;
        private String providerUrl;
        private String topicName;
        private String topicValue;
        private String topicUrl;
        private String subTopicName;
        private String subTopicValue;
        private String subTopicUrl;

        public ProviderMessageContent build() throws AlertException {
            if (null == providerName || null == topicName || null == topicValue) {
                throw new AlertException("Missing required field(s)");
            }

            final LinkableItem provider = new LinkableItem(LABEL_PROVIDER, providerName, providerUrl);
            final LinkableItem topic = new LinkableItem(topicName, topicValue, topicUrl);
            LinkableItem subTopic = null;
            if (StringUtils.isNotBlank(subTopicName) && StringUtils.isNotBlank(subTopicValue)) {
                subTopic = new LinkableItem(subTopicName, subTopicValue, subTopicUrl);
            }

            return new ProviderMessageContent(provider, topic, subTopic, generateContentKey(), componentItems);
        }

        public String generateContentKey() {
            return ProviderMessageContent.generateContentKey(providerName, topicName, topicValue, subTopicName, subTopicValue);
        }

        public Builder applyProvider(final String providerName) {
            this.providerName = providerName;
            return this;
        }

        public Builder applyProvider(final String providerName, final String providerUrl) {
            this.providerName = providerName;
            this.providerUrl = providerUrl;
            return this;
        }

        public Builder applyProviderUrl(final String providerUrl) {
            this.providerUrl = providerUrl;
            return this;
        }

        public Builder applyTopic(final String topicName, final String topicValue) {
            this.topicName = topicName;
            this.topicValue = topicValue;
            return this;
        }

        public Builder applyTopic(final String topicName, final String topicValue, final String topicUrl) {
            this.topicName = topicName;
            this.topicValue = topicValue;
            this.topicUrl = topicUrl;
            return this;
        }

        public Builder applyTopicUrl(final String topicUrl) {
            this.topicUrl = topicUrl;
            return this;
        }

        public Builder applySubTopic(final String subTopicName, final String subTopicValue) {
            this.subTopicName = subTopicName;
            this.subTopicValue = subTopicValue;
            return this;
        }

        public Builder applySubTopic(final String subTopicName, final String subTopicValue, final String subTopicUrl) {
            this.subTopicName = subTopicName;
            this.subTopicValue = subTopicValue;
            this.subTopicUrl = subTopicUrl;
            return this;
        }

        public Builder applySubTopicUrl(final String subTopicUrl) {
            this.subTopicUrl = subTopicUrl;
            return this;
        }

        public Builder applyComponentItem(final ComponentItem componentItem) {
            this.componentItems.add(componentItem);
            return this;
        }

        public Builder applyAllComponentItems(final Collection<ComponentItem> componentItems) {
            this.componentItems.addAll(componentItems);
            return this;
        }

    }

}
