package com.grahamlea.glissando.metric.monitor;

import java.util.List;

public interface MonitorWatcher {
    void monitorsRolled(List<Monitor> monitors);
}
