package com.blackducksoftware.integration.hub.alert;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.blackducksoftware.integration.hub.alert.config.AccumulatorConfig;
import com.blackducksoftware.integration.hub.alert.config.DailyDigestBatchConfig;
import com.blackducksoftware.integration.hub.alert.config.PurgeConfig;
import com.blackducksoftware.integration.hub.alert.scheduled.task.PhoneHomeTask;
import com.blackducksoftware.integration.hub.alert.scheduling.mock.MockGlobalSchedulingEntity;
import com.blackducksoftware.integration.hub.alert.scheduling.repository.global.GlobalSchedulingConfigEntity;
import com.blackducksoftware.integration.hub.alert.scheduling.repository.global.GlobalSchedulingRepositoryWrapper;
import com.blackducksoftware.integration.hub.alert.startup.StartupManager;

public class StartupManagerTest {
    private OutputLogger outputLogger;

    @Before
    public void init() throws IOException {
        outputLogger = new OutputLogger();
    }

    @After
    public void cleanup() throws IOException {
        if (outputLogger != null) {
            outputLogger.cleanup();
        }
    }

    @Test
    public void testLogConfiguration() throws IOException {
        final TestGlobalProperties testGlobalProperties = new TestGlobalProperties();
        final TestGlobalProperties mockTestGlobalProperties = Mockito.spy(testGlobalProperties);
        Mockito.when(mockTestGlobalProperties.getHubProxyPassword()).thenReturn("not_blank_data");
        final StartupManager startupManager = new StartupManager(null, mockTestGlobalProperties, null, null, null, null, null);

        startupManager.logConfiguration();
        assertTrue(outputLogger.isLineContainingText("Hub Proxy Authenticated: true"));
    }

    @Test
    public void testInitializeCronJobs() throws IOException {
        final PhoneHomeTask phoneHomeTask = Mockito.mock(PhoneHomeTask.class);
        Mockito.doNothing().when(phoneHomeTask).scheduleExecution(Mockito.anyString());
        final AccumulatorConfig accumulatorConfig = Mockito.mock(AccumulatorConfig.class);
        Mockito.doNothing().when(accumulatorConfig).scheduleExecution(Mockito.anyString());
        Mockito.doReturn(1L).when(accumulatorConfig).getMillisecondsToNextRun();
        final DailyDigestBatchConfig dailyDigestBatchConfig = Mockito.mock(DailyDigestBatchConfig.class);
        Mockito.doNothing().when(dailyDigestBatchConfig).scheduleExecution(Mockito.anyString());
        Mockito.doReturn("time").when(dailyDigestBatchConfig).getFormatedNextRunTime();
        final PurgeConfig purgeConfig = Mockito.mock(PurgeConfig.class);
        Mockito.doNothing().when(purgeConfig).scheduleExecution(Mockito.anyString());
        Mockito.doReturn("time").when(purgeConfig).getFormatedNextRunTime();
        final GlobalSchedulingRepositoryWrapper globalSchedulingRepositoryWrapper = Mockito.mock(GlobalSchedulingRepositoryWrapper.class);
        final MockGlobalSchedulingEntity mockGlobalSchedulingEntity = new MockGlobalSchedulingEntity();
        final GlobalSchedulingConfigEntity entity = mockGlobalSchedulingEntity.createGlobalEntity();
        Mockito.when(globalSchedulingRepositoryWrapper.save(Mockito.any(GlobalSchedulingConfigEntity.class))).thenReturn(entity);
        final StartupManager startupManager = new StartupManager(globalSchedulingRepositoryWrapper, null, accumulatorConfig, dailyDigestBatchConfig, purgeConfig, phoneHomeTask, null);

        startupManager.initializeCronJobs();

        final String expectedLog = entity.toString();
        assertTrue(outputLogger.isLineContainingText(expectedLog));
    }
}
