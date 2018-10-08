package com.synopsys.integration.alert.common.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.synopsys.integration.alert.web.model.AboutModel;

public class AboutModelTest {

    @Test
    public void testWithValues() {
        final String version = "1.2.3";
        final String description = "description";
        final String gitHubUrl = "https://www.google.com";

        final AboutModel model = new AboutModel(version, description, gitHubUrl);

        assertEquals(version, model.getVersion());
        assertEquals(description, model.getDescription());
        assertEquals(gitHubUrl, model.getProjectUrl());
    }
}