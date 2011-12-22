package com.grahamlea.glissando.metric.monitor;

import com.grahamlea.glissando.MonitoringSampleFrequency;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class SingleThreadMonitorSequencer extends AbstractMonitorSequencer {

    private static final ThreadFactory THREAD_FACTORY = new SequencerThreadFactory();

    public SingleThreadMonitorSequencer(MonitoringSampleFrequency sampleFrequency) {
        this(sampleFrequency, DEFAULT_ERROR_LOGGER);
    }

    public SingleThreadMonitorSequencer(MonitoringSampleFrequency sampleFrequency, ErrorLogger errorLogger) {
        super(sampleFrequency, errorLogger);
        Executors.newSingleThreadScheduledExecutor(THREAD_FACTORY)
            .scheduleAtFixedRate(new MonitorRollerAndWatcherNotifier(), sampleFrequency.asMillis(), sampleFrequency.asMillis(), TimeUnit.MILLISECONDS);
    }

    private static class SequencerThreadFactory implements ThreadFactory {
        private final ThreadFactory DEFAULT_THREAD_FACTORY = Executors.defaultThreadFactory();
        private volatile int threadNumber = 1;

        public Thread newThread(Runnable r) {
            Thread thread = DEFAULT_THREAD_FACTORY.newThread(r);
            thread.setName("SingleThreadMonitorSequencer Executor Thread #" + threadNumber++);
            thread.setDaemon(true);
            return thread;
        }
    }

}
