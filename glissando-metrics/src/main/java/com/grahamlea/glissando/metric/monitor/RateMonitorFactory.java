package com.grahamlea.glissando.metric.monitor;

import com.grahamlea.glissando.MonitoringPeriod;

import java.util.concurrent.TimeUnit;

/**
 * A factory for {@link RateMonitor}s.
 *
 * In dependency injection contexts, it may be easier to create RateMonitors using an instance of
 * this class rather than invoking the RateMonitor constructor.
 */
public class RateMonitorFactory {

    private MonitoringPeriod monitoringPeriod = new MonitoringPeriod(1, TimeUnit.MINUTES);
    private MonitorSequencer monitorSequencer;

    public RateMonitor newInstance(String name) {
        return new RateMonitor(name, monitoringPeriod, monitorSequencer);
    }

    public void setMonitoringPeriod(MonitoringPeriod monitoringPeriod) {
        this.monitoringPeriod = monitoringPeriod;
    }

    public void setMonitorSequencer(MonitorSequencer monitorSequencer) {
        this.monitorSequencer = monitorSequencer;
    }
}
