package com.grahamlea.glissmetrics.alert;

public interface Alert {
    boolean isActive();
    String alertTriggerAsString();
    String alertTextIfActive();
}
