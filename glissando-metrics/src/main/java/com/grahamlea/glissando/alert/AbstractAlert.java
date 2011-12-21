package com.grahamlea.glissando.alert;

import com.grahamlea.glissando.metric.Metric;

public abstract class AbstractAlert implements Alert {

    private final Metric metric;
    private final boolean reportPartial;

    public AbstractAlert(Metric metric, AlertActivationConstraint activationConstraint) {
        this.metric = metric;
        this.reportPartial = activationConstraint == AlertActivationConstraint.ActivationWithPartialPeriodAllowed;
    }

    protected String getMetricName() {
        return metric.getName();
    }

    public boolean isActive() {
        // Note: StubMetric kind of assumes that this is the order in which things will be evaluated...
        return (reportPartial || metric.hasFullPeriod()) && metricValueConsitutesAlert(metric.getValue());
    }

    public String alertTextIfActive() {
        return  isActive() ? alertText(metric.getValue()) : "";
    }

    /**
     * Called when the alert is active to obtain a description of the Alert and why it is active.
     * You should override this to provide more detail value if possible.
     *
     * @param currentMetricValue The current value of the metric
     *
     * @return text describing the alert and why it is currently active
     */
    protected String alertText(double currentMetricValue) {
        return alertTriggerAsString();
    }

    protected abstract boolean metricValueConsitutesAlert(double value);
}
