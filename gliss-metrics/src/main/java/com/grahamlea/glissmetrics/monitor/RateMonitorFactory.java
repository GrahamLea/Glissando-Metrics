package com.grahamlea.glissmetrics.monitor;

import com.grahamlea.glissmetrics.MonitoringPeriod;
import com.grahamlea.glissmetrics.MonitoringSampleFrequency;

import java.util.concurrent.TimeUnit;

/**
 * A factory for {@link RateMonitor}s.
 *
 * In dependency injection contexts, it may be easier to create RateMonitors using an instance of
 * this class rather than invoking the RateMonitor constructor.
 */
public class RateMonitorFactory {

    private MonitoringPeriod monitoringPeriod = new MonitoringPeriod(1, TimeUnit.MINUTES);
    private MonitoringSampleFrequency sampleFrequency = new MonitoringSampleFrequency(5, TimeUnit.SECONDS);

    public RateMonitor newInstance(String name) {
        return new RateMonitor(name, monitoringPeriod, sampleFrequency);
    }

    public void setMonitoringPeriod(MonitoringPeriod monitoringPeriod) {
        this.monitoringPeriod = monitoringPeriod;
    }

    public void setSampleFrequency(MonitoringSampleFrequency sampleFrequency) {
        this.sampleFrequency = sampleFrequency;
    }
}
