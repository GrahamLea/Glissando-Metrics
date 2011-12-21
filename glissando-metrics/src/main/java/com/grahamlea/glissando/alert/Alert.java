package com.grahamlea.glissando.alert;

public interface Alert {
    boolean isActive();
    String alertTriggerAsString();
    String alertTextIfActive();
}
