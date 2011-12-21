package com.grahamlea.glissando;

import java.util.concurrent.TimeUnit;

/**
 * Used to specify the time period that a Metric spans.
 */
public final class MonitoringPeriod extends TimeWithUnits {
    public MonitoringPeriod(long units, TimeUnit timeUnit) {
        super(units, timeUnit);
    }

    /**
     * Allows the creation of a MonitoringPeriod from a string containing an integer followed by a
     * {@link TimeUnit}, e.g. "10 seconds" or "1 hour".
     */
    public MonitoringPeriod(String timeString) {
        super(timeString);
    }

    public static MonitoringPeriod hours(int hours) {
        return new MonitoringPeriod(hours, TimeUnit.HOURS);
    }

    public static MonitoringPeriod minutes(int minutes) {
        return new MonitoringPeriod(minutes, TimeUnit.MINUTES);
    }

    public static MonitoringPeriod seconds(int seconds) {
        return new MonitoringPeriod(seconds, TimeUnit.SECONDS);
    }
}
