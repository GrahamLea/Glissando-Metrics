package com.grahamlea.glissando.aspects;

import com.grahamlea.glissando.metric.monitor.RateMonitor;

public abstract class ReturnValueCountingAspect {

    private RateMonitor rateMonitor;

    public void valueReturned(Object returnValue) throws Throwable {
        if (shouldBeCounted(returnValue))
            rateMonitor.increment();
    }

    protected abstract boolean shouldBeCounted(Object returnValue);

    public void setRateMonitor(RateMonitor rateMonitor) {
        this.rateMonitor = rateMonitor;
    }
}
