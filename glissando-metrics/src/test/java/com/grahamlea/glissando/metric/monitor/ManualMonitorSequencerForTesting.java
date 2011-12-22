package com.grahamlea.glissando.metric.monitor;

import com.grahamlea.glissando.MonitoringSampleFrequency;

public class ManualMonitorSequencerForTesting extends AbstractMonitorSequencer {

    private final MonitorRollerAndWatcherNotifier monitorRollerAndWatcherNotifier = new MonitorRollerAndWatcherNotifier();

    public ManualMonitorSequencerForTesting(MonitoringSampleFrequency sampleFrequency) {
        this(sampleFrequency, DEFAULT_ERROR_LOGGER);
    }

    public ManualMonitorSequencerForTesting(MonitoringSampleFrequency sampleFrequency, ErrorLogger errorLogger) {
        super(sampleFrequency, errorLogger);
    }

    public void rollAndNotifyWatchers() {
        monitorRollerAndWatcherNotifier.run();
    }
}
