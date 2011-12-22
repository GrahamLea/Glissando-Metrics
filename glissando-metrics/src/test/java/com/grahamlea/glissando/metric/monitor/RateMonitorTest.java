package com.grahamlea.glissando.metric.monitor;

import com.grahamlea.glissando.MonitoringPeriod;
import com.grahamlea.glissando.MonitoringSampleFrequency;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RateMonitorTest {
    @Test
    public void reportsToStringBasedOnActiveSegments() throws Exception {
        ManualMonitorSequencerForTesting sequencer = new ManualMonitorSequencerForTesting(MonitoringSampleFrequency.milliseconds(500));
        RateMonitor monitor = new RateMonitor("monitor", MonitoringPeriod.seconds(2), sequencer);
        assertThat(monitor.toString(), is("monitor: No data"));
        sequencer.rollAndNotifyWatchers();
        assertThat(monitor.toString(), is("monitor: 0 in the last 500 milliseconds"));
        sequencer.rollAndNotifyWatchers();
        assertThat(monitor.toString(), is("monitor: 0 in the last 1000 milliseconds"));
        sequencer.rollAndNotifyWatchers();
        assertThat(monitor.toString(), is("monitor: 0 in the last 1500 milliseconds"));
        sequencer.rollAndNotifyWatchers();
        assertThat(monitor.toString(), is("monitor: 0 in the last 2 seconds"));
        sequencer.rollAndNotifyWatchers();
        sequencer.rollAndNotifyWatchers();
        sequencer.rollAndNotifyWatchers();
        assertThat(monitor.toString(), is("monitor: 0 in the last 2 seconds"));
    }

    @Test
    public void usesNonPluralWhenUnitIsOne() throws Exception {
        ManualMonitorSequencerForTesting sequencer = new ManualMonitorSequencerForTesting(MonitoringSampleFrequency.seconds(1));
        RateMonitor monitor = new RateMonitor("monitor", MonitoringPeriod.seconds(2), sequencer);
        assertThat(monitor.toString(), is("monitor: No data"));
        sequencer.rollAndNotifyWatchers();
        assertThat(monitor.toString(), is("monitor: 0 in the last second"));
        sequencer.rollAndNotifyWatchers();
        assertThat(monitor.toString(), is("monitor: 0 in the last 2 seconds"));
    }

    @Test
    public void shouldCollectAndDiscardCountsAsTimeProgresses() throws Exception {
        ManualMonitorSequencerForTesting sequencer = new ManualMonitorSequencerForTesting(MonitoringSampleFrequency.milliseconds(500));
        RateMonitor monitor = new RateMonitor("monitor", MonitoringPeriod.seconds(5), sequencer);

        sequencer.rollAndNotifyWatchers(); // 500
        assertThat(monitor.getTotal(), is(0L));
        assertThat(monitor.toString(), is("monitor: 0 in the last 500 milliseconds"));

        monitor.increment();
        monitor.increment();
        monitor.increment();
        sequencer.rollAndNotifyWatchers(); // 1000
        assertThat(monitor.getTotal(), is(3L));
        assertThat(monitor.toString(), is("monitor: 3 in the last 1000 milliseconds"));

        sequencer.rollAndNotifyWatchers(); // 1500
        sequencer.rollAndNotifyWatchers(); // 2000
        sequencer.rollAndNotifyWatchers(); // 2500

        monitor.increment();
        monitor.increment();
        monitor.increment();
        monitor.increment();
        monitor.increment();

        sequencer.rollAndNotifyWatchers(); // 3000

        assertThat(monitor.getTotal(), is(8L));
        assertThat(monitor.toString(), is("monitor: 8 in the last 3000 milliseconds"));

        // Wait till first roll with value expires
        sequencer.rollAndNotifyWatchers(); // 3500
        sequencer.rollAndNotifyWatchers(); // 4000
        sequencer.rollAndNotifyWatchers(); // 4500
        sequencer.rollAndNotifyWatchers(); // 5000
        sequencer.rollAndNotifyWatchers(); // 5500
        sequencer.rollAndNotifyWatchers(); // 6000

        assertThat(monitor.getTotal(), is(5L));
        assertThat(monitor.toString(), is("monitor: 5 in the last 5 seconds"));

        // Wait till the second roll with value expires
        sequencer.rollAndNotifyWatchers(); // 6500
        sequencer.rollAndNotifyWatchers(); // 7000
        sequencer.rollAndNotifyWatchers(); // 7500
        sequencer.rollAndNotifyWatchers(); // 8000

        assertThat(monitor.getTotal(), is(0L));
        assertThat(monitor.toString(), is("monitor: 0 in the last 5 seconds"));
    }

}
