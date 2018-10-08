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
package com.synopsys.integration.alert.common.security;

import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import com.synopsys.integration.alert.common.AlertProperties;

@Component
public class EncryptionUtility {
    private final String password;
    private final String salt;

    @Autowired
    public EncryptionUtility(final AlertProperties alertProperties) {
        this.password = alertProperties.getAlertEncryptionPassword().orElse(null);
        this.salt = alertProperties.getAlertEncryptionStaticSalt().orElse(null);
    }

    public String encrypt(final String value) {
        final TextEncryptor encryptor = Encryptors.delux(password, getSalt());
        return encryptor.encrypt(value);
    }

    public String decrypt(final String encryptedValue) {
        final TextEncryptor decryptor = Encryptors.delux(password, getSalt());
        return decryptor.decrypt(encryptedValue);
    }

    private String getSalt() {
        final byte[] saltBytes = salt.getBytes(Charsets.UTF_8);
        return Hex.encodeHexString(saltBytes);
    }
}