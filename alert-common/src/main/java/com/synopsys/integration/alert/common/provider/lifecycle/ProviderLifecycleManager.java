/**
 * alert-common
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.alert.common.provider.lifecycle;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.enumeration.ConfigContextEnum;
import com.synopsys.integration.alert.common.exception.AlertDatabaseConstraintException;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.common.persistence.accessor.ConfigurationAccessor;
import com.synopsys.integration.alert.common.persistence.accessor.DescriptorAccessor;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationModel;
import com.synopsys.integration.alert.common.provider.Provider;
import com.synopsys.integration.alert.common.provider.ProviderProperties;
import com.synopsys.integration.alert.common.workflow.task.ScheduledTask;
import com.synopsys.integration.alert.common.workflow.task.TaskManager;

@Component
public class ProviderLifecycleManager {
    private final Logger logger = LoggerFactory.getLogger(ProviderLifecycleManager.class);

    private List<Provider> providers;
    private TaskManager taskManager;
    private ConfigurationAccessor configurationAccessor;
    private DescriptorAccessor descriptorAccessor;

    @Autowired
    public ProviderLifecycleManager(List<Provider> providers, TaskManager taskManager, ConfigurationAccessor configurationAccessor, DescriptorAccessor descriptorAccessor) {
        this.providers = providers;
        this.taskManager = taskManager;
        this.configurationAccessor = configurationAccessor;
        this.descriptorAccessor = descriptorAccessor;
    }

    public List<ProviderTask> initializeConfiguredProviders() {
        List<ProviderTask> initializedTasks = new ArrayList<>();
        for (Provider provider : providers) {
            try {
                List<ConfigurationModel> providerConfigurations = configurationAccessor.getConfigurationByDescriptorKeyAndContext(provider.getKey(), ConfigContextEnum.GLOBAL);
                List<ProviderTask> initializedTasksForProvider = initializeConfiguredProviders(provider, providerConfigurations);
                initializedTasks.addAll(initializedTasksForProvider);
            } catch (AlertDatabaseConstraintException e) {
                logger.error("Could not retrieve provider config: ", e);
            }
        }
        return initializedTasks;
    }

    public List<ProviderTask> scheduleTasksForProviderConfig(Provider provider, ConfigurationModel providerConfig) throws AlertException {
        logger.debug("Performing scheduling task for config with id {} and descriptor id {}", providerConfig.getConfigurationId(), providerConfig.getDescriptorId());
        List<ProviderTask> acceptedTasks = new ArrayList<>();

        ProviderProperties providerProperties = provider.createProperties(providerConfig);
        if (providerProperties.isConfigEnabled()) {
            throw new AlertException(String.format("The provider configuration '%s' cannot have tasks scheduled while it is disabled.", providerProperties.getConfigName()));
        }

        List<ProviderTask> providerTasks = provider.createProviderTasks(providerProperties);
        for (ProviderTask task : providerTasks) {
            if (taskManager.getNextRunTime(task.getTaskName()).isEmpty()) {
                scheduleTask(task);
                acceptedTasks.add(task);
            }
        }
        return acceptedTasks;
    }

    public void unscheduleTasksForProviderConfig(Provider provider, String providerConfigName) {
        logger.debug("Performing unscheduling task for provider config: {}", providerConfigName);

        List<ProviderTask> tasks = taskManager.getTasksByClass(ProviderTask.class).stream()
                                       .filter(task -> task.getProviderProperties().getConfigName().equals(providerConfigName))
                                       .collect(Collectors.toList());

        for (ProviderTask task : tasks) {
            String taskName = task.getTaskName();
            if (taskManager.getNextRunTime(taskName).isPresent()) {
                unscheduleTask(taskName);
            }
        }
    }

    private List<ProviderTask> initializeConfiguredProviders(Provider provider, List<ConfigurationModel> providerConfigurations) {
        List<ProviderTask> initializedTasks = new ArrayList<>();
        for (ConfigurationModel providerConfig : providerConfigurations) {
            ProviderProperties properties = provider.createProperties(providerConfig);
            try {
                if (properties.isConfigEnabled()) {
                    List<ProviderTask> initializedTasksForConfig = scheduleTasksForProviderConfig(provider, providerConfig);
                    initializedTasks.addAll(initializedTasksForConfig);
                } else {
                    logger.debug("The provider configuration '{}' was disabled. No tasks will be scheduled for this config.", properties.getConfigName());
                }
            } catch (AlertException e) {
                logger.error("Something went wrong while attempting to schedule provider tasks", e);
            }
        }
        return initializedTasks;
    }

    private void scheduleTask(ProviderTask task) {
        taskManager.registerTask(task);
        taskManager.scheduleCronTask(ScheduledTask.EVERY_MINUTE_CRON_EXPRESSION, task.getTaskName());
    }

    private void unscheduleTask(String taskName) {
        taskManager.unregisterTask(taskName);
    }

}
