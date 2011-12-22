package com.grahamlea.glissando.metric;

import com.grahamlea.glissando.metric.monitor.MonitorWatcher;

public interface WatchableMetric extends Metric {
    void addWatcher(MonitorWatcher monitorWatcher);
}