package com.grahamlea.glissmetrics;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Used to specify the frequency at which a Metric is updated.
 */
public final class MonitoringSampleFrequency extends TimeWithUnits {
    public MonitoringSampleFrequency(long units, TimeUnit timeUnit) {
        super(units, timeUnit);
    }

    /**
     * Allows the creation of a MonitoringSampleFrequency from a string containing an integer
     * followed by a {@link TimeUnit}, e.g. "10 seconds" or "1 hour".
     */
    public MonitoringSampleFrequency(String timeString) {
        super(timeString);
    }

    public MonitoringSampleFrequency multiply(int multiplier) {
        return new MonitoringSampleFrequency(getUnits() * multiplier, getTimeUnit());
    }

    public static MonitoringSampleFrequency minutes(int minutes) {
        return new MonitoringSampleFrequency(minutes, TimeUnit.MINUTES);
    }

    public static MonitoringSampleFrequency seconds(int seconds) {
        return new MonitoringSampleFrequency(seconds, TimeUnit.SECONDS);
    }

    public static MonitoringSampleFrequency milliseconds(int milliseconds) {
        return new MonitoringSampleFrequency(milliseconds, TimeUnit.MILLISECONDS);
    }

    public static HashSet<MonitoringSampleFrequency> union(Set<MonitoringSampleFrequency> sampleFrequencies1, Set<MonitoringSampleFrequency> sampleFrequencies2) {
        HashSet<MonitoringSampleFrequency> sampleFrequencies = new HashSet<MonitoringSampleFrequency>(sampleFrequencies1);
        sampleFrequencies.addAll(sampleFrequencies2);
        return sampleFrequencies;
    }
}
