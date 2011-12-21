package com.grahamlea.glissando;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * A helper class for metrics, monitors and alerts to schedule short-running tasks that need to be
 * executed at specified sample frequencies.
 */
public final class MonitoringThreads {

    private static final ScheduledExecutorService EXECUTOR_SERVICE =
        Executors.newScheduledThreadPool(10, new ThreadFactory() {
            private final ThreadFactory DEFAULT_THREAD_FACTORY = Executors.defaultThreadFactory();
            private volatile int threadNumber = 1;

            public Thread newThread(Runnable r) {
                Thread thread = DEFAULT_THREAD_FACTORY.newThread(r);
                thread.setName("MonitoringThreads Executor Thread #" + threadNumber++);
                thread.setDaemon(true);
                return thread;
            }
        });

    public static void scheduleAtFixedRates(Runnable task, Set<MonitoringSampleFrequency> sampleFrequencies) {
        for (MonitoringSampleFrequency sampleFrequency : sampleFrequencies) {
            scheduleAtFixedRate(task, sampleFrequency);
        }
    }

    public static void scheduleAtFixedRate(Runnable task, MonitoringSampleFrequency sampleFrequency) {
        EXECUTOR_SERVICE.scheduleAtFixedRate(new ExceptionCatchingTask(task), sampleFrequency.asMillis(), sampleFrequency.asMillis(), TimeUnit.MILLISECONDS);
    }

    private static final class ExceptionCatchingTask implements Runnable {
        private final Runnable task;

        public ExceptionCatchingTask(Runnable task) {
            this.task = task;
        }

        public void run() {
            try {
                task.run();
            } catch (Throwable e) {
                // Ignored
            }
        }
    }
}
