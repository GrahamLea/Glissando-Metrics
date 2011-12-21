package com.grahamlea.glissmetrics.alert;

import com.grahamlea.glissmetrics.metric.Metric;

public class BelowThresholdAlert extends AbstractAlert {

    private final double threshold;

    public BelowThresholdAlert(Metric metric, double threshold, AlertActivationConstraint activationConstraint) {
        super(metric, activationConstraint);
        this.threshold = threshold;
    }

    protected boolean metricValueConsitutesAlert(double value) {
        return value < threshold && value != Double.NEGATIVE_INFINITY;
    }

    public String alertTriggerAsString() {
        return getMetricName() + " < " + threshold;
    }

    @Override
    protected String alertText(double currentMetricValue) {
        return getMetricName() + ": " + currentMetricValue + " < " + threshold;
    }
}
