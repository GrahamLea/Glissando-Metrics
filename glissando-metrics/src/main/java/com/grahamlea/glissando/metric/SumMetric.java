package com.grahamlea.glissando.metric;

import com.grahamlea.glissando.MonitoringPeriod;
import com.grahamlea.glissando.MonitoringSampleFrequency;
import com.grahamlea.glissando.metric.monitor.RateMonitor;

import java.util.*;

/**
 * <p>A Metric that monitors the sum of a collection of other Metrics.</p>
 *
 * Example:
 * <code>
 *     RateMonitorFactory monitorFactory = new RateMonitorFactory();
 *     Metric link1RequestsMonitor = monitorFactory.newInstance("Link 1 requests");
 *     Metric link2RequestsMonitor = monitorFactory.newInstance("Link 2 requests");
 *     Metric totalRequestsMetric = new SumMetric("Total requests", Arrays.asList(link1RequestsMonitor, link2RequestsMonitor));
 * </code>
 */
public class SumMetric implements Metric {
    //TODO: Needs to implement WatchableMetric in order for ExampleApplication to work
    //TODO: Test!
    private final String name;
    private final List<Metric> metrics;
    private final Set<MonitoringSampleFrequency> frequencies;
    private final String toStringSuffix;

    private volatile Double currentValue = null;

    public SumMetric(String name, List<Metric> metrics) {
        if (metrics.isEmpty()) {
            throw new IllegalArgumentException("'metrics' cannot be empty");
        }
        this.name = name;
        this.metrics = new LinkedList<Metric>(metrics);
        Set<MonitoringSampleFrequency> frequencies = new HashSet<MonitoringSampleFrequency>();
        Set<MonitoringPeriod> monitoringPeriods = new HashSet<MonitoringPeriod>();
        for (Metric metric : metrics) {
            frequencies.addAll(metric.getSamplingFrequencies());
            if (metric instanceof RateMonitor) {
                monitoringPeriods.add(((RateMonitor) metric).getMonitoringPeriod());
            }
        }
        this.frequencies = frequencies;
        if (monitoringPeriods.size() > 1) {
            reportDifferingMonitoringPeriods(monitoringPeriods);
            toStringSuffix = "";
        } else if (!monitoringPeriods.isEmpty()) {
            toStringSuffix = " in the last " + monitoringPeriods.iterator().next();
        } else {
            toStringSuffix = "";
        }

//        MonitoringThreads.scheduleAtFixedRates(new Updater(), getSamplingFrequencies());
    }

    private void reportDifferingMonitoringPeriods(Set<MonitoringPeriod> monitoringPeriods) {
        Iterator<MonitoringPeriod> iterator = monitoringPeriods.iterator();
        MonitoringPeriod period1 = iterator.next();
        MonitoringPeriod period2 = iterator.next();
        System.err.println("******* WARNING *******");
        System.err.println("You have created a SumMetric using RateMonitors that have different");
        System.err.println("MonitoringPeriods. This usually doesn't make sense to do as you are");
        System.err.println("adding a count from the last " + period1 + " to a count from");
        System.err.println("the last " + period2 + ".");
        System.err.println("***********************");
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return currentValue == null ? Double.NaN : currentValue;
    }

    public boolean hasFullPeriod() {
        for (Metric metric : metrics) {
            if (!metric.hasFullPeriod())
                return false;
        }
        return true;
    }

    public Set<MonitoringSampleFrequency> getSamplingFrequencies() {
        return frequencies;
    }

    @Override
    public String toString() {
        return name + ": " + (currentValue == null ? "no data" : (getValue() + toStringSuffix));
    }

    private final class Updater implements Runnable {
        public void run() {
            double total = 0;
            for (Metric metric : metrics) {
                total += metric.getValue();
            }
            currentValue = total;
        }
    }
}
