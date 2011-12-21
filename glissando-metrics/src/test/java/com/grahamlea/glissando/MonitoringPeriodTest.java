package com.grahamlea.glissando;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MonitoringPeriodTest {
    @Test
    public void constructWithString() throws Exception {
        MonitoringPeriod oneMinute = new MonitoringPeriod("1 minute");
        assertThat(oneMinute.getUnits(), is(1L));
        assertThat(oneMinute.getTimeUnit(), is(TimeUnit.MINUTES));

        MonitoringPeriod minutes27 = new MonitoringPeriod("27 minutes");
        assertThat(minutes27.getUnits(), is(27L));
        assertThat(minutes27.getTimeUnit(), is(TimeUnit.MINUTES));

        MonitoringPeriod seconds27 = new MonitoringPeriod("27 seconds");
        assertThat(seconds27.getUnits(), is(27L));
        assertThat(seconds27.getTimeUnit(), is(TimeUnit.SECONDS));

        MonitoringPeriod milliseconds27 = new MonitoringPeriod("27 milliseconds");
        assertThat(milliseconds27.getUnits(), is(27L));
        assertThat(milliseconds27.getTimeUnit(), is(TimeUnit.MILLISECONDS));

        MonitoringPeriod hours27 = new MonitoringPeriod("27 hours");
        assertThat(hours27.getUnits(), is(27L));
        assertThat(hours27.getTimeUnit(), is(TimeUnit.HOURS));

        MonitoringPeriod days27 = new MonitoringPeriod("27 days");
        assertThat(days27.getUnits(), is(27L));
        assertThat(days27.getTimeUnit(), is(TimeUnit.DAYS));
    }
}
