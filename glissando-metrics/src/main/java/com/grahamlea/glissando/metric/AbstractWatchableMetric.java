package com.grahamlea.glissando.metric;

import com.grahamlea.glissando.metric.monitor.MonitorWatcher;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWatchableMetric implements WatchableMetric {

    private final List<MonitorWatcher> watchers = new ArrayList<MonitorWatcher>();

    @Override
    public final void addWatcher(MonitorWatcher monitorWatcher) {
        synchronized (watchers) {
            watchers.add(monitorWatcher);
        }
    }

    public final MonitorWatcher[] getWatchers() {
        synchronized (watchers) {
            return watchers.toArray(new MonitorWatcher[watchers.size()]);
        }
    }
}
