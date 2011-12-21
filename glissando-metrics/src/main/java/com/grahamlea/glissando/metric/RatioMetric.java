package com.grahamlea.glissando.metric;

import com.grahamlea.glissando.MonitoringSampleFrequency;
import com.grahamlea.glissando.MonitoringThreads;

import java.util.Set;

import static com.grahamlea.glissando.MonitoringSampleFrequency.union;

/**
 * <p>A Metric that monitors the ratio between two other Metrics.
 * The value of a RatioMetric is the value of the first specified Metric divided by the value of
 * the second specified Metric.</p>
 *
 * Example:
 * <code>
 *     RateMonitorFactory monitorFactory = new RateMonitorFactory();
 *     Metric requestsMonitor = monitorFactory.newInstance("requests");
 *     Metric timeoutsMonitor = monitorFactory.newInstance("timeouts");
 *     Metric timeoutRatioMetric = new RatioMetric(timeoutsMonitor, requestsMonitor);
 * </code>
 *
 * Once values have been collected from the above monitors, the toString() of the timeoutRatioMetric
 * would be something like: "timeouts/transactions: 0.015"
 */
public class RatioMetric implements Metric {

    private final String name;
    private final Metric dividend;
    private final Metric divisor;

    private volatile Double currentValue = null;

    public RatioMetric(Metric dividend, Metric divisor) {
        this(dividend.getName() + " / " + divisor, dividend, divisor);
    }

    public RatioMetric(String name, Metric dividend, Metric divisor) {
        this.name = name;
        this.dividend = dividend;
        this.divisor = divisor;

        MonitoringThreads.scheduleAtFixedRates(new Updater(), union(dividend.getSamplingFrequencies(), divisor.getSamplingFrequencies()));
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return currentValue == null ? Double.NaN : currentValue;
    }

    public boolean hasFullPeriod() {
        return dividend.hasFullPeriod() && divisor.hasFullPeriod();
    }

    public Set<MonitoringSampleFrequency> getSamplingFrequencies() {
        return union(dividend.getSamplingFrequencies(), divisor.getSamplingFrequencies());
    }

    @Override
    public String toString() {
        return getName() + ": " + getValue();
    }

    private final class Updater implements Runnable {
        public void run() {
            currentValue = dividend.getValue() / divisor.getValue();
        }
    }
}
