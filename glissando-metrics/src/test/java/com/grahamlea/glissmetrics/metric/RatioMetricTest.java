package com.grahamlea.glissmetrics.metric;

import com.grahamlea.glissmetrics.MonitoringPeriod;
import com.grahamlea.glissmetrics.MonitoringSampleFrequency;
import com.grahamlea.glissmetrics.monitor.RateMonitor;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RatioMetricTest {

    RateMonitor monitor1 = new RateMonitor("monitor1", MonitoringPeriod.seconds(2), MonitoringSampleFrequency.milliseconds(250));
    RateMonitor monitor2 = new RateMonitor("monitor2", MonitoringPeriod.seconds(2), MonitoringSampleFrequency.milliseconds(250));
    RatioMetric ratioMetric = new RatioMetric(monitor1, monitor2);

    @Test
    public void shouldHandleNoData() throws Exception {
        assertThat(ratioMetric.getValue(), is(Double.NaN));
    }

    @Test
    public void shouldHandleDivideByZero() throws Exception {
        monitor1.increment();
        Thread.sleep(300);
        assertThat(ratioMetric.getValue(), is(Double.POSITIVE_INFINITY));
    }

    @Test
    public void shouldReturnRealRatiosForRealValues() throws Exception {
        Thread.sleep(300);
        monitor1.increment();
        monitor2.increment();
        monitor2.increment();
        Thread.sleep(300);
        assertThat(ratioMetric.getValue(), is(1/2d));

        monitor1.increment();
        monitor2.increment();
        monitor2.increment();
        monitor2.increment();
        Thread.sleep(300);
        assertThat(ratioMetric.getValue(), is(2/5d));
    }
}
