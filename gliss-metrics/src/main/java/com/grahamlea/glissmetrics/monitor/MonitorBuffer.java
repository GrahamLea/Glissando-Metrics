package com.grahamlea.glissmetrics.monitor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A circular buffer used by a {@link RateMonitor} to maintain a series of rolling counters that
 * can be updated very quickly by many concurrent threads and rolled with minimum interruption
 * while maintaining accuracy.
 */
class MonitorBuffer {

    private final long[] completedSegments;
    private final Object rollLock = new Object();

    private final AtomicInteger counter = new AtomicInteger();
    private volatile int nextSegmentIndex = 0;
    private volatile int incompleteSegments;
    private volatile long currentTotal = 0;

    public MonitorBuffer(int segments) {
        if (segments < 1)
            throw new IllegalArgumentException("Must be at least one active segment");

        this.incompleteSegments = segments;
        this.completedSegments = new long[segments];
    }

    public void increment() {
        counter.incrementAndGet();
    }

    public void roll() {
        synchronized (rollLock) {
            segmentCompleted(counter.getAndSet(0));
        }
    }

    private void segmentCompleted(long segmentTotal) {
        long expiredSegmentTotal = completedSegments[nextSegmentIndex];
        completedSegments[nextSegmentIndex] = segmentTotal;
        currentTotal = (currentTotal + segmentTotal - expiredSegmentTotal);
        if (incompleteSegments != 0)
            incompleteSegments--;
        if (++nextSegmentIndex == completedSegments.length)
            nextSegmentIndex = 0;
    }

    public long getTotal() {
        return currentTotal;
    }

    public Integer getActiveSegmentCount() {
        return completedSegments.length - incompleteSegments;
    }

    public boolean isFull() {
        return incompleteSegments == 0;
    }
}
