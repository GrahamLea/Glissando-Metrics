package com.grahamlea.glissando.alert;

import com.grahamlea.glissando.metric.Metric;

public class AboveThresholdAlert extends AbstractAlert {

    private final double threshold;

    public AboveThresholdAlert(Metric metric, double threshold, AlertActivationConstraint activationConstraint) {
        super(metric, activationConstraint);
        this.threshold = threshold;
    }

    protected boolean metricValueConsitutesAlert(double value) {
        return value > threshold && value != Double.POSITIVE_INFINITY;
    }

    public String alertTriggerAsString() {
        return getMetricName() + " > " + threshold;
    }

    @Override
    protected String alertText(double currentMetricValue) {
        return getMetricName() + ": " + currentMetricValue + " > " + threshold;
    }
}
