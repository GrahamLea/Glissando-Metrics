package com.grahamlea.glissando.metric.monitor;

import com.grahamlea.glissando.MonitoringSampleFrequency;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AbstractMonitorSequencer implements MonitorSequencer {

    static final ErrorLogger DEFAULT_ERROR_LOGGER = new DefaultErrorLogger();
    private final MonitoringSampleFrequency sampleFrequency;
    
    private final Collection<Monitor> monitors = new ArrayList<Monitor>();
    private final ErrorLogger errorLogger;

    public AbstractMonitorSequencer(MonitoringSampleFrequency sampleFrequency, ErrorLogger errorLogger) {
        this.errorLogger = errorLogger;
        this.sampleFrequency = sampleFrequency;
    }

    public void add(Monitor monitor) {
        synchronized (monitors) {
            if (!monitors.contains(monitor)) {
                monitors.add(monitor);
            }
        }
    }

    @Override
    public MonitoringSampleFrequency getSampleFrequency() {
        return sampleFrequency;
    }

    private static class DefaultErrorLogger implements ErrorLogger {

        private Logger logger = Logger.getLogger(SingleThreadMonitorSequencer.class.getName());

        @Override
        public void errorRolling(Monitor monitor, Throwable error) {
            logger.log(Level.WARNING, "Error caught while rolling Monitor " + monitor.getName(), error);
        }

        @Override
        public void errorNotifyingWatcher(MonitorWatcher monitorWatcher, Throwable error) {
            logger.log(Level.WARNING, "Error caught while notifying MonitorWatcher " + monitorWatcher, error);
        }
    }

    protected final class MonitorRollerAndWatcherNotifier implements Runnable {
        @Override
        public void run() {
            Monitor[] allMonitors = copyOfMonitorsList();
            roll(allMonitors);
            notifyWatchersOf(allMonitors);
        }

        private Monitor[] copyOfMonitorsList() {
            synchronized (monitors) {
                return monitors.toArray(new Monitor[monitors.size()]);
            }
        }

        private void roll(Monitor[] monitorList) {
            for (Monitor monitor : monitorList) {
                try {
                    monitor.rollSamples();
                } catch (Throwable e) {
                    errorLogger.errorRolling(monitor, e);
                }
            }
        }

        private void notifyWatchersOf(Monitor[] monitorList) {
            Map<MonitorWatcher, List<Monitor>> monitorsForWatchers =
                    new IdentityHashMap<MonitorWatcher, List<Monitor>>(monitorList.length * 2);

            for (Monitor monitor : monitorList) {
                for (MonitorWatcher watcher : monitor.getWatchers()) {
                    List<Monitor> monitorsForThisWatcher = monitorsForWatchers.get(watcher);
                    if (monitorsForThisWatcher == null) {
                        monitorsForWatchers.put(watcher, monitorsForThisWatcher = new ArrayList<Monitor>());
                    }
                    monitorsForThisWatcher.add(monitor);
                }
            }

            for (Map.Entry<MonitorWatcher, List<Monitor>> watcherAndMonitors : monitorsForWatchers.entrySet()) {
                MonitorWatcher watcher = watcherAndMonitors.getKey();
                try {
                    watcher.monitorsRolled(watcherAndMonitors.getValue());
                } catch (Throwable e) {
                    errorLogger.errorNotifyingWatcher(watcher, e);
                }
            }
        }
    }

    public interface ErrorLogger {
        public void errorRolling(Monitor monitor, Throwable error);
        public void errorNotifyingWatcher(MonitorWatcher monitorWatcher, Throwable error);
    }
}
