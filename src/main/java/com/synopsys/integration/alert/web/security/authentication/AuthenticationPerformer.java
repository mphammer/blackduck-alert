/**
 * blackduck-alert
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
package com.synopsys.integration.alert.web.security.authentication;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.synopsys.integration.alert.common.descriptor.accessor.AuthorizationUtility;
import com.synopsys.integration.alert.common.enumeration.AuthenticationPriority;
import com.synopsys.integration.alert.common.persistence.model.UserRoleModel;
import com.synopsys.integration.alert.web.security.authentication.event.AuthenticationEventManager;

public abstract class AuthenticationPerformer implements Comparable<AuthenticationPerformer> {
    private AuthenticationPriority priority;
    private AuthenticationEventManager authenticationEventManager;
    private AuthorizationUtility authorizationUtility;

    protected AuthenticationPerformer(AuthenticationPriority priority, AuthenticationEventManager authenticationEventManager, AuthorizationUtility authorizationUtility) {
        this.priority = priority;
        this.authenticationEventManager = authenticationEventManager;
        this.authorizationUtility = authorizationUtility;
    }

    public final Optional<UsernamePasswordAuthenticationToken> performAuthentication(Authentication authentication) {
        Authentication authenticationResult = authenticateWithProvider(authentication);
        if (authenticationResult.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = isAuthorized(authenticationResult) ? authenticationResult.getAuthorities() : List.of();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authenticationResult.getPrincipal(), authenticationResult.getCredentials(), authorities);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            return Optional.of(authenticationToken);
        }
        return Optional.empty();
    }

    public abstract Authentication authenticateWithProvider(Authentication pendingAuthentication);

    @Override
    public int compareTo(AuthenticationPerformer other) {
        return this.getPriority().compareTo(other.getPriority());
    }

    protected AuthenticationPriority getPriority() {
        return priority;
    }

    private boolean isAuthorized(Authentication authentication) {
        Set<String> allowedRoles = authorizationUtility.getRoles()
                                       .stream()
                                       .map(UserRoleModel::getName)
                                       .collect(Collectors.toSet());
        return authentication.getAuthorities()
                   .stream()
                   .map(authenticationEventManager::getRoleFromAuthority)
                   .flatMap(Optional::stream)
                   .anyMatch(allowedRoles::contains);
    }

}
