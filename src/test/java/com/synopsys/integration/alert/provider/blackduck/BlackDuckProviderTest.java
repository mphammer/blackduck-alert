package com.synopsys.integration.alert.provider.blackduck;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.mockito.Mockito;

import com.synopsys.integration.alert.common.enumeration.FormatType;
import com.synopsys.integration.alert.provider.blackduck.tasks.BlackDuckAccumulator;
import com.synopsys.integration.alert.provider.blackduck.tasks.ProjectSyncTask;
import com.synopsys.integration.blackduck.api.generated.enumeration.NotificationType;

public class BlackDuckProviderTest {

    @Test
    public void testInitialize() {
        final BlackDuckAccumulator accumulatorTask = Mockito.mock(BlackDuckAccumulator.class);
        final ProjectSyncTask projectSyncTask = Mockito.mock(ProjectSyncTask.class);
        final BlackDuckProvider provider = new BlackDuckProvider(accumulatorTask, projectSyncTask);
        provider.initialize();
        Mockito.verify(accumulatorTask).scheduleExecution(BlackDuckAccumulator.DEFAULT_CRON_EXPRESSION);
        Mockito.verify(projectSyncTask).scheduleExecution(BlackDuckAccumulator.DEFAULT_CRON_EXPRESSION);

    }

    @Test
    public void testDestroy() {
        final BlackDuckAccumulator accumulatorTask = Mockito.mock(BlackDuckAccumulator.class);
        final ProjectSyncTask projectSyncTask = Mockito.mock(ProjectSyncTask.class);
        final BlackDuckProvider provider = new BlackDuckProvider(accumulatorTask, projectSyncTask);
        provider.destroy();
        Mockito.verify(accumulatorTask).scheduleExecution(BlackDuckAccumulator.STOP_SCHEDULE_EXPRESSION);
        Mockito.verify(projectSyncTask).scheduleExecution(BlackDuckAccumulator.STOP_SCHEDULE_EXPRESSION);

    }

    @Test
    public void testGetNotificationTypes() {
        final BlackDuckAccumulator accumulatorTask = Mockito.mock(BlackDuckAccumulator.class);
        final ProjectSyncTask projectSyncTask = Mockito.mock(ProjectSyncTask.class);
        final BlackDuckProvider provider = new BlackDuckProvider(accumulatorTask, projectSyncTask);
        final Set<String> expectedNotificationTypes = Arrays.stream(NotificationType.values()).map(NotificationType::name).collect(Collectors.toSet());
        final Set<String> providerNotificationTypes = provider.getProviderContentTypes().stream().map(contentType -> contentType.getNotificationType()).collect(Collectors.toSet());
        assertEquals(expectedNotificationTypes, providerNotificationTypes);
    }

    @Test
    public void testGetSupportedFormatTypes() {
        final BlackDuckAccumulator accumulatorTask = Mockito.mock(BlackDuckAccumulator.class);
        final ProjectSyncTask projectSyncTask = Mockito.mock(ProjectSyncTask.class);
        final BlackDuckProvider provider = new BlackDuckProvider(accumulatorTask, projectSyncTask);
        final Set<FormatType> expectedNotificationTypes = EnumSet.of(FormatType.DEFAULT, FormatType.DIGEST);
        final Set<FormatType> providerNotificationTypes = provider.getSupportedFormatTypes();
        assertEquals(expectedNotificationTypes, providerNotificationTypes);
    }
}