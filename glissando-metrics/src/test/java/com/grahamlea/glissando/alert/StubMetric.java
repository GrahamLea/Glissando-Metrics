package com.grahamlea.glissando.alert;

import com.grahamlea.glissando.MonitoringSampleFrequency;
import com.grahamlea.glissando.metric.Metric;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class StubMetric implements Metric {

    private final String name;
    private final Iterator<Double> partialValues;
    private final Iterator<Double> fullValues;

    public StubMetric(Double[] partialValues, Double[] fullValues) {
        this("StubMetric", partialValues, fullValues);
    }

    public StubMetric(String name, Double[] partialValues, Double[] fullValues) {
        this.name = name;
        this.partialValues = Arrays.asList(partialValues).iterator();
        this.fullValues = Arrays.asList(fullValues).iterator();
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        if (partialValues.hasNext())
            return partialValues.next();
        else
            return fullValues.next();
    }

    public boolean hasFullPeriod() {
        if (partialValues.hasNext()) {
            partialValues.next(); // So that ActivationRequiresFullPeriod alerts get to the full values
            return false;
        } else {
            return true;
        }
    }

    public Set<MonitoringSampleFrequency> getSamplingFrequencies() {
        return Collections.singleton(MonitoringSampleFrequency.milliseconds(1));
    }

    public static StubMetric withFullPeriodAndValues(Double... values) {
        return withFullPeriodAndValues("StubMetric", values);
    }

    public static StubMetric withPartialPeriodAndValues(Double... values) {
        return withPartialPeriodAndValues("StubMetric", values);
    }

    public static StubMetric withFullPeriodAndValues(String name, Double... values) {
        return new StubMetric(name, new Double[0], values);
    }

    public static StubMetric withPartialPeriodAndValues(String name, Double... values) {
        return new StubMetric(name, values, new Double[0]);
    }
}
