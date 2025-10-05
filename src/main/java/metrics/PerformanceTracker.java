package metrics;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Tracks performance metrics for heap operations including comparisons,
 * swaps, array accesses, and memory allocations.
 */
public class PerformanceTracker {
    private long comparisons;
    private long swaps;
    private long arrayAccesses;
    private long memoryAllocations;
    private long startTime;
    private long endTime;
    private final List<MetricSnapshot> snapshots;
    private boolean trackingEnabled;

    public PerformanceTracker() {
        this.comparisons = 0;
        this.swaps = 0;
        this.arrayAccesses = 0;
        this.memoryAllocations = 0;
        this.snapshots = new ArrayList<>();
        this.trackingEnabled = true;
    }

    public void reset() {
        this.comparisons = 0;
        this.swaps = 0;
        this.arrayAccesses = 0;
        this.memoryAllocations = 0;
        this.startTime = 0;
        this.endTime = 0;
    }

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public void stopTimer() {
        this.endTime = System.nanoTime();
    }

    public long getElapsedTimeNanos() {
        return endTime - startTime;
    }

    public double getElapsedTimeMillis() {
        return (endTime - startTime) / 1_000_000.0;
    }

    public void incrementComparisons() {
        if (trackingEnabled) comparisons++;
    }

    public void incrementSwaps() {
        if (trackingEnabled) swaps++;
    }

    public void incrementArrayAccesses() {
        if (trackingEnabled) arrayAccesses++;
    }

    public void incrementArrayAccesses(int count) {
        if (trackingEnabled) arrayAccesses += count;
    }

    public void incrementMemoryAllocations() {
        if (trackingEnabled) memoryAllocations++;
    }

    public void setTrackingEnabled(boolean enabled) {
        this.trackingEnabled = enabled;
    }

    public void takeSnapshot(String operation, int heapSize) {
        snapshots.add(new MetricSnapshot(
                operation, heapSize, comparisons, swaps,
                arrayAccesses, getElapsedTimeNanos()
        ));
    }

    public void exportToCSV(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Operation,HeapSize,Comparisons,Swaps,ArrayAccesses,TimeNanos");
            for (MetricSnapshot snapshot : snapshots) {
                writer.printf("%s,%d,%d,%d,%d,%d%n",
                        snapshot.operation, snapshot.heapSize,
                        snapshot.comparisons, snapshot.swaps,
                        snapshot.arrayAccesses, snapshot.timeNanos);
            }
        }
    }

    public String getSummary() {
        return String.format(
                "Performance Metrics:%n" +
                        "  Comparisons: %,d%n" +
                        "  Swaps: %,d%n" +
                        "  Array Accesses: %,d%n" +
                        "  Memory Allocations: %,d%n" +
                        "  Time: %.3f ms%n",
                comparisons, swaps, arrayAccesses,
                memoryAllocations, getElapsedTimeMillis()
        );
    }

    // Getters
    public long getComparisons() { return comparisons; }
    public long getSwaps() { return swaps; }
    public long getArrayAccesses() { return arrayAccesses; }
    public long getMemoryAllocations() { return memoryAllocations; }

    private static class MetricSnapshot {
        String operation;
        int heapSize;
        long comparisons;
        long swaps;
        long arrayAccesses;
        long timeNanos;

        MetricSnapshot(String operation, int heapSize, long comparisons,
                       long swaps, long arrayAccesses, long timeNanos) {
            this.operation = operation;
            this.heapSize = heapSize;
            this.comparisons = comparisons;
            this.swaps = swaps;
            this.arrayAccesses = arrayAccesses;
            this.timeNanos = timeNanos;
        }
    }
}
