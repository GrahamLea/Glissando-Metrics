package com.grahamlea.glissando.alert;

import org.junit.Test;

import static com.grahamlea.glissando.alert.AlertActivationConstraint.ActivationRequiresFullPeriod;
import static com.grahamlea.glissando.alert.AlertActivationConstraint.ActivationWithPartialPeriodAllowed;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BelowThresholdAlertTest {
    @Test
    public void shouldNotBeActiveForNan() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(Double.NaN);
        assertThat(new BelowThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod).isActive(), is(false));
    }

    @Test
    public void shouldNotBeActiveForInfinity() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY);
        BelowThresholdAlert alert = new BelowThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod);
        assertThat(alert.isActive(), is(false));
        assertThat(alert.isActive(), is(false));
    }

    @Test
    public void shouldNotBeActiveWhenValueIsAboveThreshold() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(0.51);
        assertThat(new BelowThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod).isActive(), is(false));
    }

    @Test
    public void shouldNotBeActiveWhenValueIsEqualToThreshold() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(0.5);
        assertThat(new BelowThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod).isActive(), is(false));
    }

    @Test
    public void shouldBeActiveWhenValueIsBelowThreshold() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(0.49999999999);
        assertThat(new BelowThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod).isActive(), is(true));
    }

    @Test
    public void shouldBecomeActiveWhenValueChangesToBeBelowThreshold() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(0.51, 0.49);
        BelowThresholdAlert alert = new BelowThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod);
        assertThat(alert.isActive(), is(false));
        assertThat(alert.isActive(), is(true));
    }

    @Test
    public void shouldRemainActiveWhenValueContinuesToBeBelowThreshold() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(0.51, 0.49, 0.45);
        BelowThresholdAlert alert = new BelowThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod);
        assertThat(alert.isActive(), is(false));
        assertThat(alert.isActive(), is(true));
        assertThat(alert.isActive(), is(true));
    }

    @Test
    public void shouldBecomeInactiveWhenValueChangesToExceedThreshold() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(0.49, 0.51);
        BelowThresholdAlert alert = new BelowThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod);
        assertThat(alert.isActive(), is(true));
        assertThat(alert.isActive(), is(false));
    }

    @Test
    public void shouldRemainInactiveWhenValueContinuesToExceedhreshold() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues(0.49, 0.51, 0.55);
        BelowThresholdAlert alert = new BelowThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod);
        assertThat(alert.isActive(), is(true));
        assertThat(alert.isActive(), is(false));
        assertThat(alert.isActive(), is(false));
    }

    @Test
    public void shouldNotBeActiveForPartialPeriodWhenFullPeriodRequired() throws Exception {
        StubMetric metric = StubMetric.withPartialPeriodAndValues(0.49);
        assertThat(new BelowThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod).isActive(), is(false));
    }

    @Test
    public void shouldBeActiveForPartialPeriodWhenAllowed() throws Exception {
        StubMetric metric = StubMetric.withPartialPeriodAndValues(0.49);
        assertThat(new BelowThresholdAlert(metric, 0.5, ActivationWithPartialPeriodAllowed).isActive(), is(true));
    }

    @Test
    public void shouldNotBeActiveForPartialPeriodWhenNotBelowThreshold() throws Exception {
        StubMetric metric = StubMetric.withPartialPeriodAndValues(0.51);
        assertThat(new BelowThresholdAlert(metric, 0.51, ActivationWithPartialPeriodAllowed).isActive(), is(false));
    }

    @Test
    public void shouldBecomeActiveWhenFullPeriodRequiredIfValueRemainsUnderThresholdWhenFullPeriodAchieved() throws Exception {
        StubMetric metric = new StubMetric(new Double[] {0.49}, new Double[] {0.49});
        BelowThresholdAlert alertRequiringFullPeriod = new BelowThresholdAlert(metric, 0.5, ActivationRequiresFullPeriod);
        assertThat(alertRequiringFullPeriod.isActive(), is(false));
        assertThat(alertRequiringFullPeriod.isActive(), is(true));
    }

    @Test
    public void shouldRemainActiveIfPartialPeriodAllowedIfValueRemainsUnderThresholdWhenFullPeriodAchieved() throws Exception {
        StubMetric metric = new StubMetric(new Double[] {0.49}, new Double[] {0.49});
        BelowThresholdAlert alertRequiringPartialPeriod = new BelowThresholdAlert(metric, 0.5, ActivationWithPartialPeriodAllowed);
        assertThat(alertRequiringPartialPeriod.isActive(), is(true));
        assertThat(alertRequiringPartialPeriod.isActive(), is(true));
    }


    @Test
    public void alertTriggerAsString() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues("Metric A");
        assertThat(new BelowThresholdAlert(metric, 1.765, ActivationRequiresFullPeriod).alertTriggerAsString(), is("Metric A < 1.765"));
    }

    @Test
    public void shouldGiveEmptyAlertTextIfActiveWhenNotActive() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues("Metric A", 2.4);
        assertThat(new BelowThresholdAlert(metric, 1.765, ActivationRequiresFullPeriod).alertTextIfActive(), is(""));
    }

    @Test
    public void shouldGiveDescriptiveAlertTextIfActiveWhenActive() throws Exception {
        StubMetric metric = StubMetric.withFullPeriodAndValues("Metric A", 0.98842, 0.98842); // alertTextIfActive() grabs two values
        assertThat(new BelowThresholdAlert(metric, 1.765, ActivationRequiresFullPeriod).alertTextIfActive(), is("Metric A: 0.98842 < 1.765"));
    }
}
