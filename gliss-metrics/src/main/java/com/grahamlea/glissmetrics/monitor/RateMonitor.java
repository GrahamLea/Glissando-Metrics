package com.grahamlea.glissmetrics.monitor;

import com.grahamlea.glissmetrics.MonitoringThreads;
import com.grahamlea.glissmetrics.MonitoringPeriod;
import com.grahamlea.glissmetrics.MonitoringSampleFrequency;
import com.grahamlea.glissmetrics.metric.Metric;

import java.util.Collections;
import java.util.Set;

/**
 * A monitor for efficiently collecting the count of a single metric within a period time in a
 * highly concurrent environment.
 *
 * RateMonitor satisfies the need to record a rate, e.g. "number of transactions per minute", that
 * is updated on a regular basis, e.g. every 5 seconds.
 */
public class RateMonitor implements Metric {

    private final String name;
    private final MonitorBuffer monitorBuffer;
    private final MonitoringPeriod monitoringPeriod;
    private final MonitoringSampleFrequency sampleFrequency;
    private final StringBuilder toStringSuffix;

    public RateMonitor(String name, MonitoringPeriod monitoringPeriod, MonitoringSampleFrequency sampleFrequency) {
        this.name = name;
        this.monitoringPeriod = monitoringPeriod;
        this.sampleFrequency = sampleFrequency;
        if (sampleFrequency.asMillis() > monitoringPeriod.asMillis())
            throw new IllegalArgumentException("Sampling period must be <= the monitoring period");
        if (monitoringPeriod.asMillis() % sampleFrequency.asMillis() != 0)
            throw new IllegalArgumentException("Sampling period must be an integral factor of the monitoring period");

        int samplingSegments = (int) (monitoringPeriod.asMillis() / sampleFrequency.asMillis());
        this.monitorBuffer = new MonitorBuffer(samplingSegments);
        toStringSuffix = new StringBuilder(" in the last " + monitoringPeriod);
        MonitoringThreads.scheduleAtFixedRate(new MonitorRollTask(monitorBuffer), sampleFrequency);
    }

    public String getName() {
        return name;
    }

    public void increment() {
        monitorBuffer.increment();
    }

    public long getTotal() {
        return monitorBuffer.getTotal();
    }

    public double getValue() {
        return getTotal();
    }

    public Set<MonitoringSampleFrequency> getSamplingFrequencies() {
        return Collections.singleton(sampleFrequency);
    }

    public boolean hasFullPeriod() {
        return monitorBuffer.isFull();
    }

    public MonitoringPeriod getMonitoringPeriod() {
        return monitoringPeriod;
    }

    @Override
    public String toString() {
        if (monitorBuffer.isFull())
            return name + ": " + monitorBuffer.getTotal() + toStringSuffix;
        else if (monitorBuffer.getActiveSegmentCount() == 0)
            return name + ": No data";
        else
            return name + ": " + monitorBuffer.getTotal() + " in the last " + sampleFrequency.multiply(monitorBuffer.getActiveSegmentCount());
    }

    private static final class MonitorRollTask implements Runnable {

        private final MonitorBuffer monitorBuffer;

        private MonitorRollTask(MonitorBuffer monitorBuffer) {
            this.monitorBuffer = monitorBuffer;
        }

        public void run() {
            monitorBuffer.roll();
        }
    }
}
