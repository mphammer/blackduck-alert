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
package com.synopsys.integration.alert.component.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.descriptor.ComponentDescriptor;

@Component
public class SettingsDescriptor extends ComponentDescriptor {
    public static final String SETTINGS_COMPONENT = "component_settings";
    public static final String SETTINGS_LABEL = "Settings";
    public static final String SETTINGS_URL = "settings";
    public static final String SETTINGS_ICON = "cog";

    // Values not stored in the database, but keys must be registered
    public static final String KEY_DEFAULT_SYSTEM_ADMIN_EMAIL = "settings.user.default.admin.email";
    public static final String KEY_DEFAULT_SYSTEM_ADMIN_PWD = "settings.user.default.admin.password";
    public static final String KEY_ENCRYPTION_PWD = "settings.encryption.password";
    public static final String KEY_ENCRYPTION_GLOBAL_SALT = "settings.encryption.global.salt";

    // Proxy Keys
    public static final String KEY_PROXY_HOST = "settings.proxy.host";
    public static final String KEY_PROXY_PORT = "settings.proxy.port";
    public static final String KEY_PROXY_USERNAME = "settings.proxy.username";
    public static final String KEY_PROXY_PWD = "settings.proxy.password";

    // LDAP Keys
    public static final String KEY_LDAP_ENABLED = "settings.ldap.enabled";
    public static final String KEY_LDAP_SERVER = "settings.ldap.server";
    public static final String KEY_LDAP_MANAGER_DN = "settings.ldap.manager.dn";
    public static final String KEY_LDAP_MANAGER_PWD = "settings.ldap.manager.password";
    public static final String KEY_LDAP_AUTHENTICATION_TYPE = "settings.ldap.authentication.type";
    public static final String KEY_LDAP_REFERRAL = "settings.ldap.referral";
    public static final String KEY_LDAP_USER_SEARCH_BASE = "settings.ldap.user.search.base";
    public static final String KEY_LDAP_USER_SEARCH_FILTER = "settings.ldap.user.search.filter";
    public static final String KEY_LDAP_USER_DN_PATTERNS = "settings.ldap.user.dn.patterns";
    public static final String KEY_LDAP_USER_ATTRIBUTES = "settings.ldap.user.attributes";
    public static final String KEY_LDAP_GROUP_SEARCH_BASE = "settings.ldap.group.search.base";
    public static final String KEY_LDAP_GROUP_SEARCH_FILTER = "settings.ldap.group.search.filter";
    public static final String KEY_LDAP_GROUP_ROLE_ATTRIBUTE = "settings.ldap.group.role.attribute";
    public static final String KEY_LDAP_ROLE_PREFIX = "settings.ldap.role.prefix";
    public static final String KEY_STARTUP_ENVIRONMENT_VARIABLE_OVERRIDE = "settings.startup.environment.variable.override";

    public static final String FIELD_ERROR_DEFAULT_USER_PWD = "Default admin user password missing";
    public static final String FIELD_ERROR_DEFAULT_USER_EMAIL = "Default admin user email missing";
    public static final String FIELD_ERROR_ENCRYPTION_PWD = "Encryption password missing";
    public static final String FIELD_ERROR_ENCRYPTION_GLOBAL_SALT = "Encryption global salt missing";

    public static final String FIELD_ERROR_PROXY_HOST_MISSING = "Proxy Host missing";
    public static final String FIELD_ERROR_PROXY_PORT_MISSING = "Proxy Port missing";
    public static final String FIELD_ERROR_PROXY_PORT_INVALID = "Proxy Port not a number greater than 1";
    public static final String FIELD_ERROR_PROXY_USER_MISSING = "Proxy Username missing";
    public static final String FIELD_ERROR_PROXY_PWD_MISSING = "Proxy Password missing";

    public static final String FIELD_ERROR_LDAP_SERVER_MISSING = "LDAP Server missing";
    public static final String FIELD_ERROR_LDAP_USERNAME_MISSING = "LDAP Manager DN is missing";
    public static final String FIELD_ERROR_LDAP_PWD_MISSING = "LDAP Manager DN password is missing";

    @Autowired
    public SettingsDescriptor(final SettingsDescriptorActionApi componentRestApi, final SettingsUIConfig uiConfig) {
        super(SETTINGS_COMPONENT, componentRestApi, uiConfig);
    }
}
