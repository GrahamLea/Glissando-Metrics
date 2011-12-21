package com.grahamlea.glissando.metric.monitor;

import com.grahamlea.glissando.MonitoringPeriod;
import com.grahamlea.glissando.MonitoringSampleFrequency;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RateMonitorTest {
    @Test
    public void reportsToStringBasedOnActiveSegments() throws Exception {
        RateMonitor monitor = new RateMonitor(
                "monitor", new MonitoringPeriod(2, TimeUnit.SECONDS),
                new MonitoringSampleFrequency(500, TimeUnit.MILLISECONDS));
        assertThat(monitor.toString(), is("monitor: No data"));
        Thread.sleep(550);
        assertThat(monitor.toString(), is("monitor: 0 in the last 500 milliseconds"));
        Thread.sleep(550);
        assertThat(monitor.toString(), is("monitor: 0 in the last 1000 milliseconds"));
        Thread.sleep(550);
        assertThat(monitor.toString(), is("monitor: 0 in the last 1500 milliseconds"));
        Thread.sleep(550);
        assertThat(monitor.toString(), is("monitor: 0 in the last 2 seconds"));
        Thread.sleep(2100);
        assertThat(monitor.toString(), is("monitor: 0 in the last 2 seconds"));
    }

    @Test
    public void usesNonPluralWhenUnitIsOne() throws Exception {
        RateMonitor monitor = new RateMonitor(
                "monitor", new MonitoringPeriod(2, TimeUnit.SECONDS),
                new MonitoringSampleFrequency(1, TimeUnit.SECONDS));
        assertThat(monitor.toString(), is("monitor: No data"));
        Thread.sleep(1100);
        assertThat(monitor.toString(), is("monitor: 0 in the last second"));
        Thread.sleep(1100);
        assertThat(monitor.toString(), is("monitor: 0 in the last 2 seconds"));
    }

    @Test
    public void shouldCollectAndDiscardCountsAsTimeProgresses() throws Exception {
        RateMonitor monitor = new RateMonitor(
                "monitor", new MonitoringPeriod(5, TimeUnit.SECONDS),
                new MonitoringSampleFrequency(500, TimeUnit.MILLISECONDS));
        Thread.sleep(600); // At least one roll()
        assertThat(monitor.getTotal(), is(0L));
        assertThat(monitor.toString(), is("monitor: 0 in the last 500 milliseconds"));

        monitor.increment();
        monitor.increment();
        monitor.increment();
        Thread.sleep(600); // At least the next roll
        assertThat(monitor.getTotal(), is(3L));
        assertThat(monitor.toString(), is("monitor: 3 in the last 1000 milliseconds"));
        Thread.sleep(2000); // Create a gap (first roll with value 2600-2100 ago)

        monitor.increment();
        monitor.increment();
        monitor.increment();
        monitor.increment();
        monitor.increment();
        Thread.sleep(600); // At least the next roll (first roll with value now 3200-2700 ago)
        assertThat(monitor.getTotal(), is(8L));
        //noinspection unchecked
        assertThat(monitor.toString(), anyOf(is("monitor: 8 in the last 3000 milliseconds"), is("monitor: 8 in the last 3500 milliseconds")));
        Thread.sleep(2500); // Wait till first roll with value expires (now 5700-5200 ago)
        assertThat(monitor.getTotal(), is(5L));
        assertThat(monitor.toString(), is("monitor: 5 in the last 5 seconds"));
        Thread.sleep(2600); // Wait till the second roll with value expires
        assertThat(monitor.getTotal(), is(0L));
        assertThat(monitor.toString(), is("monitor: 0 in the last 5 seconds"));
    }
}
