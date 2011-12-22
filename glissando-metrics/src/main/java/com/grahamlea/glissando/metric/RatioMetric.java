package com.grahamlea.glissando.metric;

import com.grahamlea.glissando.MonitoringSampleFrequency;
import com.grahamlea.glissando.metric.monitor.Monitor;
import com.grahamlea.glissando.metric.monitor.MonitorWatcher;

import java.util.List;
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
public class RatioMetric implements Metric, MonitorWatcher {

    private final String name;
    private final WatchableMetric dividend;
    private final WatchableMetric divisor;

    private volatile Double currentValue = null;

    public RatioMetric(WatchableMetric dividend, WatchableMetric divisor) {
        this(dividend.getName() + " / " + divisor, dividend, divisor);
    }

    public RatioMetric(String name, WatchableMetric dividend, WatchableMetric divisor) {
        this.name = name;
        this.dividend = dividend;
        this.divisor = divisor;

        dividend.addWatcher(this);
        divisor.addWatcher(this);
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

    @Override
    public void monitorsRolled(List<Monitor> monitors) {
        currentValue = dividend.getValue() / divisor.getValue();
    }
}
