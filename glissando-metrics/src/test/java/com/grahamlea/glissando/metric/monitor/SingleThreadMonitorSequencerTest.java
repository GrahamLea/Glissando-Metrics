package com.grahamlea.glissando.metric.monitor;

import com.grahamlea.glissando.MonitoringSampleFrequency;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

public class SingleThreadMonitorSequencerTest {

    @Test
    public void shouldRollMonitorsContinuouslyAtTheSpecifiedFrequency() throws Exception {
        RollTimeRecordingMonitor monitor = new RollTimeRecordingMonitor();

        SingleThreadMonitorSequencer sequencer =
            new SingleThreadMonitorSequencer(MonitoringSampleFrequency.milliseconds(1700));
        sequencer.add(monitor);

        Thread.sleep((long) (1700 * 3.5));

        Long time1 = monitor.rollTimes.get(0);
        Long time2 = monitor.rollTimes.get(1);
        Long time3 = monitor.rollTimes.get(2);

        long period1 = time2 - time1;
        assertThat(period1 > 1690, is(true));
        assertThat(period1 < 1710, is(true));

        long period2 = time3 - time2;
        assertThat(period2 > 1690, is(true));
        assertThat(period2 < 1710, is(true));
    }

    @Test
    public void shouldRollAllMonitorsAtTheSameTimeEvenIfAddedAtDifferentTimes() throws Exception {
        RollTimeRecordingMonitor monitor1 = new RollTimeRecordingMonitor();
        RollTimeRecordingMonitor monitor2 = new RollTimeRecordingMonitor();

        SingleThreadMonitorSequencer sequencer =
            new SingleThreadMonitorSequencer(MonitoringSampleFrequency.milliseconds(1700));
        sequencer.add(monitor1);

        Thread.sleep(500);
        monitor1.rollTimes.clear();
        sequencer.add(monitor2);

        Thread.sleep(1700 * 2);

        Long time1 = monitor1.rollTimes.get(0);
        Long time2 = monitor2.rollTimes.get(0);
        assertThat(Math.abs(time2 - time1) < 10, is(true));
    }

    @Test
    public void shouldLogAndIgnoreExceptionsThrownWhenRollingMonitors() throws Exception {

        SavingErrorLogger errorLogger = new SavingErrorLogger();

        RuntimeException error = new IllegalArgumentException("Test exception");

        Monitor monitor = new ErrorThrowingMonitor(error);

        SingleThreadMonitorSequencer sequencer =
            new SingleThreadMonitorSequencer(MonitoringSampleFrequency.milliseconds(200), errorLogger);
        sequencer.add(monitor);

        Thread.sleep(300);
        assertThat(errorLogger.reportedMonitor, is(sameInstance(monitor)));
        assertThat(errorLogger.reportedError, is(sameInstance((Throwable) error)));
    }

    @Test
    public void shouldNotifyAMonitorWatcherOfRolling() throws Exception {
        List<Object> callLog = new ArrayList<Object>();
        AtomicReference<List<Monitor>> parameterLog = new AtomicReference<List<Monitor>>();

        MonitorWatcher watcher = new CallLoggingMonitorWatcher(callLog, parameterLog);

        Monitor monitor = new CallLoggingMonitorWithWatchers(callLog, watcher);

        SingleThreadMonitorSequencer sequencer =
            new SingleThreadMonitorSequencer(MonitoringSampleFrequency.milliseconds(200));
        sequencer.add(monitor);

        Thread.sleep((long) (200 * 1.1));

        assertThat(callLog.get(0), is(sameInstance((Object) monitor)));
        assertThat(callLog.get(1), is(sameInstance((Object) watcher)));
        assertThat(parameterLog.get(), is(Arrays.asList(monitor)));
    }

    @Test
    public void shouldNotifyAllWatchersAfterRollingAllMonitors() throws Exception {

        List<Object> callLog = new ArrayList<Object>();

        MonitorWatcher watcher1 = new CallLoggingMonitorWatcher(callLog);
        MonitorWatcher watcher2 = new CallLoggingMonitorWatcher(callLog);

        Monitor monitor1 = new CallLoggingMonitorWithWatchers(callLog, watcher1);
        Monitor monitor2 = new CallLoggingMonitorWithWatchers(callLog, watcher2);

        SingleThreadMonitorSequencer sequencer =
            new SingleThreadMonitorSequencer(MonitoringSampleFrequency.milliseconds(200));
        sequencer.add(monitor1);
        sequencer.add(monitor2);

        Thread.sleep((long) (200 * 1.1));

        System.out.println("callLog = " + callLog);
        assertThat(callLog.get(0), is(sameInstance((Object) monitor1)));
        assertThat(callLog.get(1), is(sameInstance((Object) monitor2)));
        assertThat(callLog.get(2), is(sameInstance((Object) watcher1)));
        assertThat(callLog.get(3), is(sameInstance((Object) watcher2)));
    }

    @Test
    public void shouldNotifyWatchersOfMonitorsOfOnlyThoseTheyWatch() throws Exception {
        List<Object> callLog = new ArrayList<Object>();
        AtomicReference<List<Monitor>> watcher1ParameterLog = new AtomicReference<List<Monitor>>();
        AtomicReference<List<Monitor>> watcher2ParameterLog = new AtomicReference<List<Monitor>>();

        MonitorWatcher watcher1 = new CallLoggingMonitorWatcher(callLog, watcher1ParameterLog);
        MonitorWatcher watcher2 = new CallLoggingMonitorWatcher(callLog, watcher2ParameterLog);

        Monitor monitor1 = new CallLoggingMonitorWithWatchers(callLog, watcher1);
        Monitor monitor2 = new CallLoggingMonitorWithWatchers(callLog, watcher2);

        SingleThreadMonitorSequencer sequencer =
            new SingleThreadMonitorSequencer(MonitoringSampleFrequency.milliseconds(200));
        sequencer.add(monitor1);
        sequencer.add(monitor2);

        Thread.sleep((long) (200 * 1.1));

        assertThat(watcher1ParameterLog.get(), is(Arrays.asList(monitor1)));
        assertThat(watcher2ParameterLog.get(), is(Arrays.asList(monitor2)));
    }

    @Test
    public void shouldNotifyWatchersOfMultipleMonitorsAboutAllMonitorsInOneCall() throws Exception {
        List<Object> callLog = new ArrayList<Object>();
        AtomicReference<List<Monitor>> watcherParameterLog = new AtomicReference<List<Monitor>>();

        MonitorWatcher watcher = new CallLoggingMonitorWatcher(callLog, watcherParameterLog);

        Monitor monitor1 = new CallLoggingMonitorWithWatchers(callLog, watcher);
        Monitor monitor2 = new CallLoggingMonitorWithWatchers(callLog, watcher);

        SingleThreadMonitorSequencer sequencer =
            new SingleThreadMonitorSequencer(MonitoringSampleFrequency.milliseconds(200));
        sequencer.add(monitor1);
        sequencer.add(monitor2);

        Thread.sleep((long) (200 * 1.1));

        assertThat(callLog.get(0), is(sameInstance((Object) monitor1)));
        assertThat(callLog.get(1), is(sameInstance((Object) monitor2)));
        assertThat(callLog.get(2), is(sameInstance((Object) watcher)));
        assertThat(watcherParameterLog.get(), is(Arrays.asList(monitor1, monitor2)));
        assertThat(callLog.size(), is(3));
    }

    private static class RollTimeRecordingMonitor implements Monitor {
        private final List<Long> rollTimes = new ArrayList<Long>();

        @Override
        public String getName() {
            return getClass().getSimpleName();
        }

        @Override
        public void rollSamples() {
            rollTimes.add(System.currentTimeMillis());
        }

        @Override
        public MonitorWatcher[] getWatchers() {
            return new MonitorWatcher[0];
        }
    }

    private static class ErrorThrowingMonitor implements Monitor {
        private final RuntimeException error;

        public ErrorThrowingMonitor(RuntimeException error) {
            this.error = error;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void rollSamples() {
            throw error;
        }

        @Override
        public MonitorWatcher[] getWatchers() {
            return new MonitorWatcher[0];
        }
    }

    private static class SavingErrorLogger implements AbstractMonitorSequencer.ErrorLogger {
        private volatile Monitor reportedMonitor = null;
        private volatile MonitorWatcher reportedMonitorWatcher = null;
        private volatile Throwable reportedError = null;

        @Override
        public void errorRolling(Monitor monitor, Throwable error) {
            reportedMonitor = monitor;
            reportedError = error;
        }

        // TODO: Test this is called
        @Override
        public void errorNotifyingWatcher(MonitorWatcher monitorWatcher, Throwable error) {
            reportedMonitorWatcher = monitorWatcher;
            reportedError = error;
        }
    }

    private static abstract class MonitorWithWatchers implements Monitor {

        private final MonitorWatcher[] watchers;

        public MonitorWithWatchers(MonitorWatcher... watchers) {
            this.watchers = watchers;
        }

        @Override
        public String getName() {
            return null;
        }

        public MonitorWatcher[] getWatchers() {
            return watchers;
        }
    }

    private static class CallLoggingMonitorWithWatchers extends MonitorWithWatchers {
        private final List<Object> callLog;

        public CallLoggingMonitorWithWatchers(List<Object> callLog, MonitorWatcher... watchers) {
            super(watchers);
            this.callLog = callLog;
        }

        @Override
        public void rollSamples() {
            callLog.add(this);
        }
    }

    private static class CallLoggingMonitorWatcher implements MonitorWatcher {
        private final List<Object> callLog;
        private final AtomicReference<List<Monitor>> parameterLog;

        public CallLoggingMonitorWatcher(List<Object> callLog) {
            this(callLog, null);
        }

        public CallLoggingMonitorWatcher(List<Object> callLog, AtomicReference<List<Monitor>> parameterLog) {
            this.callLog = callLog;
            this.parameterLog = parameterLog;
        }

        @Override
        public void monitorsRolled(List<Monitor> monitors) {
            callLog.add(this);
            if (parameterLog != null) {
                parameterLog.set(monitors);
            }
        }
    }
}
