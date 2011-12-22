package com.grahamlea.glissando.metric;

import com.grahamlea.glissando.MonitoringPeriod;
import com.grahamlea.glissando.MonitoringSampleFrequency;
import com.grahamlea.glissando.metric.monitor.ManualMonitorSequencerForTesting;
import com.grahamlea.glissando.metric.monitor.RateMonitor;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RatioMetricTest {

    ManualMonitorSequencerForTesting sequencer =
            new ManualMonitorSequencerForTesting(MonitoringSampleFrequency.milliseconds(250));
    RateMonitor monitor1 = new RateMonitor("monitor1", MonitoringPeriod.seconds(2), sequencer);
    RateMonitor monitor2 = new RateMonitor("monitor2", MonitoringPeriod.seconds(2), sequencer);
    RatioMetric ratioMetric = new RatioMetric(monitor1, monitor2);

    @Test
    public void shouldHandleNoData() throws Exception {
        assertThat(ratioMetric.getValue(), is(Double.NaN));
    }

    @Test
    public void shouldHandleDivideByZero() throws Exception {
        monitor1.increment();
        sequencer.rollAndNotifyWatchers();
        assertThat(ratioMetric.getValue(), is(Double.POSITIVE_INFINITY));
    }

    @Test
    public void shouldReturnRealRatiosForRealValues() throws Exception {
        sequencer.rollAndNotifyWatchers();
        monitor1.increment();
        monitor2.increment();
        monitor2.increment();
        sequencer.rollAndNotifyWatchers();
        assertThat(ratioMetric.getValue(), is(1/2d));

        monitor1.increment();
        monitor2.increment();
        monitor2.increment();
        monitor2.increment();
        sequencer.rollAndNotifyWatchers();
        assertThat(ratioMetric.getValue(), is(2/5d));
    }
}
