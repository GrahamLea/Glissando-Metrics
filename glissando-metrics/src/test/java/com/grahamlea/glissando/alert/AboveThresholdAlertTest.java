package com.grahamlea.glissando.alert;

import org.junit.Test;

import static com.grahamlea.glissando.alert.AlertActivationConstraint.ActivationRequiresFullPeriod;
import static com.grahamlea.glissando.alert.AlertActivationConstraint.ActivationWithPartialPeriodAllowed;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AboveThresholdAlertTest {

    @Test
    public void shouldNotBeActiveForNan() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(Double.NaN);
        assertThat(new AboveThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod).isActive(), is(false));
    }

    @Test
    public void shouldNotBeActiveForInfinity() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
        AboveThresholdAlert alert = new AboveThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod);
        assertThat(alert.isActive(), is(false));
        assertThat(alert.isActive(), is(false));
    }

    @Test
    public void shouldNotBeActiveWhenValueIsBelowThreshold() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(0.49);
        assertThat(new AboveThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod).isActive(), is(false));
    }

    @Test
    public void shouldNotBeActiveWhenValueIsEqualToThreshold() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(0.5);
        assertThat(new AboveThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod).isActive(), is(false));
    }

    @Test
    public void shouldBeActiveWhenValueIsAboveThreshold() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(0.5000001);
        assertThat(new AboveThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod).isActive(), is(true));
    }

    @Test
    public void shouldBecomeActiveWhenValueChangesToExceedThreshold() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(0.49, 0.51);
        AboveThresholdAlert alert = new AboveThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod);
        assertThat(alert.isActive(), is(false));
        assertThat(alert.isActive(), is(true));
    }

    @Test
    public void shouldRemainActiveWhenValueContinuesToExceedThreshold() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(0.49, 0.51, 0.55);
        AboveThresholdAlert alert = new AboveThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod);
        assertThat(alert.isActive(), is(false));
        assertThat(alert.isActive(), is(true));
        assertThat(alert.isActive(), is(true));
    }

    @Test
    public void shouldBecomeInactiveWhenValueChangesToBelowThreshold() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(0.51, 0.49);
        AboveThresholdAlert alert = new AboveThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod);
        assertThat(alert.isActive(), is(true));
        assertThat(alert.isActive(), is(false));
    }

    @Test
    public void shouldRemainInactiveWhenValueContinuesToBelowThreshold() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(0.51, 0.49, 0.45);
        AboveThresholdAlert alert = new AboveThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod);
        assertThat(alert.isActive(), is(true));
        assertThat(alert.isActive(), is(false));
        assertThat(alert.isActive(), is(false));
    }

    @Test
    public void shouldNotBeActiveForPartialPeriodWhenFullPeriodRequired() throws Exception {
        StubMetric metric = StubMetric.withPartialPeriodAndValues(0.51);
        assertThat(new AboveThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod).isActive(), is(false));
    }

    @Test
    public void shouldBeActiveForPartialPeriodWhenAllowed() throws Exception {
        StubMetric metric = StubMetric.withPartialPeriodAndValues(0.51);
        assertThat(new AboveThresholdAlert(metric, 0.5, ActivationWithPartialPeriodAllowed).isActive(), is(true));
    }

    @Test
    public void shouldNotBeActiveForPartialPeriodWhenThresholdNotExceeded() throws Exception {
        StubMetric metric = StubMetric.withPartialPeriodAndValues(0.51);
        assertThat(new AboveThresholdAlert(metric, 0.51, ActivationWithPartialPeriodAllowed).isActive(), is(false));
    }

    @Test
    public void shouldBecomeActiveWhenFullPeriodRequiredIfValueRemainsOverThresholdWhenFullPeriodAchieved() throws Exception {
        StubMetric metric = new StubMetric(new Double[] {0.51}, new Double[] {0.51});
        AboveThresholdAlert alertRequiringFullPeriod = new AboveThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod);
        assertThat(alertRequiringFullPeriod.isActive(), is(false));
        assertThat(alertRequiringFullPeriod.isActive(), is(true));
    }

    @Test
    public void shouldRemainActiveIfPartialPeriodAllowedIfValueRemainsOverThresholdWhenFullPeriodAchieved() throws Exception {
        StubMetric metric = new StubMetric(new Double[] {0.51}, new Double[] {0.51});
        AboveThresholdAlert alertRequiringPartialPeriod = new AboveThresholdAlert(metric, 0.5, ActivationWithPartialPeriodAllowed);
        assertThat(alertRequiringPartialPeriod.isActive(), is(true));
        assertThat(alertRequiringPartialPeriod.isActive(), is(true));
    }

    @Test
    public void alertTriggerAsString() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues("Metric A");
        assertThat(new AboveThresholdAlert(metric, 1.765, ActivationRequiresFullPeriod).alertTriggerAsString(), is("Metric A > 1.765"));
    }

    @Test
    public void shouldGiveEmptyAlertTextIfActiveWhenNotActive() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues("Metric A", 0.4);
        assertThat(new AboveThresholdAlert(metric, 1.765, ActivationRequiresFullPeriod).alertTextIfActive(), is(""));
    }

    @Test
    public void shouldGiveDescriptiveAlertTextIfActiveWhenActive() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues("Metric A", 3.98842, 3.98842); // alertTextIfActive() grabs two values
        assertThat(new AboveThresholdAlert(metric, 1.765, ActivationRequiresFullPeriod).alertTextIfActive(), is("Metric A: 3.98842 > 1.765"));
    }
}
