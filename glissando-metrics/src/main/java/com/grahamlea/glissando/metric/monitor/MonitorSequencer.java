package com.grahamlea.glissando.metric.monitor;

import com.grahamlea.glissando.MonitoringSampleFrequency;

public interface MonitorSequencer {
    MonitoringSampleFrequency getSampleFrequency();
    void add(Monitor monitor);
}
