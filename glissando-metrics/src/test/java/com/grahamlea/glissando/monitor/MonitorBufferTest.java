package com.grahamlea.glissando.monitor;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MonitorBufferTest {

    @Test
    public void shouldRollIncrementsOntoAndOffTheTotalWhenRollIsCalled() throws Exception {
        MonitorBuffer buffer = new MonitorBuffer(2);
        assertThat(buffer.getTotal(), is(0L));
        assertThat(buffer.getActiveSegmentCount(), is(0));
        assertThat(buffer.isFull(), is(false));

        buffer.increment();
        buffer.increment();
        assertThat(buffer.getTotal(), is(0L));
        assertThat(buffer.getActiveSegmentCount(), is(0));
        assertThat(buffer.isFull(), is(false));
        buffer.roll();
        assertThat(buffer.getTotal(), is(2L));
        assertThat(buffer.getActiveSegmentCount(), is(1));
        assertThat(buffer.isFull(), is(false));

        buffer.increment();
        assertThat(buffer.getTotal(), is(2L));
        buffer.roll();
        assertThat(buffer.getTotal(), is(3L));
        assertThat(buffer.getActiveSegmentCount(), is(2));
        assertThat(buffer.isFull(), is(true));

        buffer.increment();
        buffer.increment();
        buffer.increment();
        buffer.increment();
        assertThat(buffer.getTotal(), is(3L));
        buffer.roll();
        assertThat(buffer.getTotal(), is(5L)); // 1 + 4
        assertThat(buffer.getActiveSegmentCount(), is(2));
        assertThat(buffer.isFull(), is(true));

        buffer.increment();
        buffer.increment();
        assertThat(buffer.getTotal(), is(5L));
        buffer.roll();
        assertThat(buffer.getTotal(), is(6L)); // 4 + 2
        assertThat(buffer.getActiveSegmentCount(), is(2));
        assertThat(buffer.isFull(), is(true));

        buffer.increment();
        buffer.increment();
        buffer.increment();
        buffer.increment();
        buffer.increment();
        assertThat(buffer.getTotal(), is(6L));
        buffer.roll();
        assertThat(buffer.getTotal(), is(7L)); // 2 + 5
        assertThat(buffer.getActiveSegmentCount(), is(2));
        assertThat(buffer.isFull(), is(true));

        buffer.roll();
        assertThat(buffer.getTotal(), is(5L));
        buffer.roll();
        assertThat(buffer.getTotal(), is(0L));
        assertThat(buffer.getActiveSegmentCount(), is(2));
        assertThat(buffer.isFull(), is(true));
    }

    @Test
    public void shouldWorkFineWithASingleActiveSegment() throws Exception {
        MonitorBuffer buffer = new MonitorBuffer(1);
        assertThat(buffer.getTotal(), is(0L));

        buffer.increment();
        buffer.increment();
        assertThat(buffer.getTotal(), is(0L));
        buffer.roll();
        assertThat(buffer.getTotal(), is(2L));

        buffer.increment();
        assertThat(buffer.getTotal(), is(2L));
        buffer.roll();
        assertThat(buffer.getTotal(), is(1L));
        buffer.roll();
        assertThat(buffer.getTotal(), is(0L));
    }

    @Test
    public void shouldMaintainAccuracyDespiteMssiveConcurrency() throws Exception {
        ThreadFactory threadFactory = new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = Executors.defaultThreadFactory().newThread(r);
                thread.setDaemon(true);
                return thread;
            }
        };

        long start = System.currentTimeMillis();
        int numberOfThreads = 1000;
        ExecutorService threadPool = Executors.newFixedThreadPool(numberOfThreads, threadFactory);

        int bufferSegments = 120;
        MonitorBuffer buffer = new MonitorBuffer(bufferSegments);
        buffer.roll();
        buffer.roll();
        buffer.roll();
        List<Incrementer> incrementers = new ArrayList<Incrementer>(numberOfThreads);
        List<Future<Long>> futures = new ArrayList<Future<Long>>(numberOfThreads);

        System.out.println("Creating actors...");
        for (int i = 0; i < numberOfThreads; i++)
            incrementers.add(new Incrementer(buffer));

        System.out.println("Starting threads...");
        for (int i = 0; i < numberOfThreads; i++)
            futures.add(threadPool.submit(incrementers.get(i)));

        System.out.println("Rolling...");
        for (int i = 0; i < bufferSegments - 1; i++) {
            Thread.sleep(500);
            long rollStart = System.currentTimeMillis();
            buffer.roll();
            long rollTime = System.currentTimeMillis() - rollStart;
//            System.out.println("rollTime = " + rollTime);
        }

        System.out.println("Stopping...");
        for (Incrementer incrementer : incrementers) {
            incrementer.running = false;
        }

        System.out.println("Waiting and summing...");
        long incrementersTotal = 0;
        for (Future<Long> future : futures) {
            incrementersTotal += future.get();
        }
        long end = System.currentTimeMillis();
        double timeInSeconds = (end - start) / 1000.0;

        buffer.roll();

        long bufferTotal = buffer.getTotal();
        System.out.println("buffer.getTotal() = " + bufferTotal);
        System.out.println("timeInSeconds = " + timeInSeconds);
        System.out.println("Calls per thread per second = " + (((double)bufferTotal / numberOfThreads) / timeInSeconds));
        assertThat(bufferTotal, is(incrementersTotal));
    }

    private final class Incrementer implements Callable<Long> {

        private final MonitorBuffer buffer;
        private volatile boolean running = true;
        private long count = 0;

        private Incrementer(MonitorBuffer buffer) {
            this.buffer = buffer;
        }

        public Long call() {
            while (running) {
                buffer.increment();
                count++;
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    // Ignored
                }
            }
            return count;
        }
    }
}
