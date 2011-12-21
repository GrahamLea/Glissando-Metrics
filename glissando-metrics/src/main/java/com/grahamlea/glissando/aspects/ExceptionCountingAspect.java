package com.grahamlea.glissando.aspects;

import com.grahamlea.glissando.metric.monitor.RateMonitor;

public class ExceptionCountingAspect {

    private final Class<? extends Throwable> exceptionClass;
    private final RateMonitor rateMonitor;

    public ExceptionCountingAspect(Class<? extends Throwable> exceptionClass, RateMonitor rateMonitor) {
        this.exceptionClass = exceptionClass;
        this.rateMonitor = rateMonitor;
    }

    public void exceptionThrown(Throwable e) throws Throwable {
        if (exceptionClass.isAssignableFrom(e.getClass())) {
            rateMonitor.increment();
        }
    }
}
