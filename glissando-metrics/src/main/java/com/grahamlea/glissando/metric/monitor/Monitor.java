package com.grahamlea.glissando.metric.monitor;

public interface Monitor {
    String getName();
    void rollSamples(); // TODO: Move this into its own interface?
    MonitorWatcher[] getWatchers();
}
