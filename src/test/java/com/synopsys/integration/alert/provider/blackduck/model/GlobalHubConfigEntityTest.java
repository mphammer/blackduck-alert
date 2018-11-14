/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.synopsys.integration.alert.provider.blackduck.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.synopsys.integration.alert.database.entity.GlobalEntityTest;
import com.synopsys.integration.alert.database.provider.blackduck.BlackDuckEntity;
import com.synopsys.integration.alert.provider.blackduck.mock.MockGlobalBlackDuckEntity;

public class GlobalHubConfigEntityTest extends GlobalEntityTest<BlackDuckEntity> {

    @Override
    public Class<BlackDuckEntity> getGlobalEntityClass() {
        return BlackDuckEntity.class;
    }

    @Override
    public void assertGlobalEntityFieldsNull(final BlackDuckEntity entity) {
        assertNull(entity.getBlackDuckTimeout());
        assertNull(entity.getBlackDuckApiKey());
    }

    @Override
    public void assertGlobalEntityFieldsFull(final BlackDuckEntity entity) {
        assertEquals(getMockUtil().getBlackDuckTimeout(), entity.getBlackDuckTimeout());
        assertEquals(getMockUtil().getBlackDuckApiKey(), entity.getBlackDuckApiKey());
    }

    @Override
    public MockGlobalBlackDuckEntity getMockUtil() {
        return new MockGlobalBlackDuckEntity();
    }
}
