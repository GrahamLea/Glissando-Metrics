package com.grahamlea.glissando.metric;

import com.grahamlea.glissando.MonitoringSampleFrequency;

import java.util.Set;

public interface Metric {
    String getName();
    double getValue();

    /**
     * Returns true if this Metric has collected a full period of data.
     *
     * For instance, a {@link com.grahamlea.glissando.metric.monitor.RateMonitor} that has been configured
     * to record 30 seconds of data and update at a frequency 10 seconds will, after 10 seconds, have
     * a value available, but will not have a full period until at least 30 seconds has passed.
     */
    boolean hasFullPeriod();

    /**
     * Returns all the monitoring sample frequencies applicable to this Metric.
     *
     * Simple Metrics will only have a single sample frequency, while those that are derived from
     * a relationship between multiple other metrics may have more than one.
     */
    Set<MonitoringSampleFrequency> getSamplingFrequencies();
}
