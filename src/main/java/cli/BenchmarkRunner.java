package cli;

import algorithms.MinHeap;
import metrics.PerformanceTracker;

import java.io.IOException;
import java.util.Random;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Command-line interface for benchmarking MinHeap operations.
 * Provides configurable input sizes and operation types.
 */
public class BenchmarkRunner {

    public static void main(String[] args) {
        System.out.println("=== MinHeap Benchmark Suite ===\n");

        // Run benchmarks with different sizes
        int[] sizes = {100, 1000, 10000, 100000};

        for (int size : sizes) {
            System.out.println("Benchmarking with n = " + size);
            System.out.println("-".repeat(50));

            benchmarkInsert(size);
            benchmarkExtractMin(size);
            benchmarkDecreaseKey(size);
            benchmarkMerge(size);
            benchmarkBuildHeap(size);

            System.out.println();
        }

        // Run scalability analysis
        System.out.println("\n=== Scalability Analysis ===");
        runScalabilityTest();
    }

    private static int[] generateUniqueRandomArray(int size, Random rand) {
        Set<Integer> uniqueValues = new HashSet<>();
        while (uniqueValues.size() < size) {
            uniqueValues.add(rand.nextInt(size * 100));
        }
        return uniqueValues.stream().mapToInt(Integer::intValue).toArray();
    }

    private static void benchmarkInsert(int n) {
        MinHeap heap = new MinHeap(n);
        PerformanceTracker tracker = heap.getTracker();
        Random rand = new Random(42);

        int[] values = generateUniqueRandomArray(n, rand);

        tracker.startTimer();
        for (int i = 0; i < n; i++) {
            heap.insert(values[i]);
        }
        tracker.stopTimer();

        System.out.printf("Insert (%d elements):%n", n);
        System.out.printf("  Time: %.3f ms%n", tracker.getElapsedTimeMillis());
        System.out.printf("  Comparisons: %,d (avg: %.2f per insert)%n",
                tracker.getComparisons(), (double)tracker.getComparisons() / n);
        System.out.printf("  Swaps: %,d (avg: %.2f per insert)%n",
                tracker.getSwaps(), (double)tracker.getSwaps() / n);
    }

    private static void benchmarkExtractMin(int n) {
        // Build heap first
        Random rand = new Random(42);
        int[] array = generateUniqueRandomArray(n, rand);
        MinHeap heap = new MinHeap(array);

        PerformanceTracker tracker = heap.getTracker();
        tracker.reset();
        tracker.startTimer();

        for (int i = 0; i < n; i++) {
            heap.extractMin();
        }
        tracker.stopTimer();

        System.out.printf("Extract-Min (%d elements):%n", n);
        System.out.printf("  Time: %.3f ms%n", tracker.getElapsedTimeMillis());
        System.out.printf("  Comparisons: %,d (avg: %.2f per extract)%n",
                tracker.getComparisons(), (double)tracker.getComparisons() / n);
        System.out.printf("  Swaps: %,d (avg: %.2f per extract)%n",
                tracker.getSwaps(), (double)tracker.getSwaps() / n);
    }

    private static void benchmarkDecreaseKey(int n) {
        Random rand = new Random(42);
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = i * 10; // Ensure unique values
        }
        MinHeap heap = new MinHeap(array);

        PerformanceTracker tracker = heap.getTracker();
        tracker.reset();
        tracker.startTimer();

        // Decrease random keys
        int operations = Math.min(n / 2, 1000);
        for (int i = 0; i < operations; i++) {
            int oldVal = (rand.nextInt(n / 2) + n / 2) * 10;
            int newVal = oldVal - rand.nextInt(oldVal);
            try {
                if (heap.contains(oldVal) && !heap.contains(newVal) && newVal >= 0) {
                    heap.decreaseKey(oldVal, newVal);
                }
            } catch (Exception e) {
                // Skip if value already used
            }
        }
        tracker.stopTimer();

        System.out.printf("Decrease-Key (%d operations):%n", operations);
        System.out.printf("  Time: %.3f ms%n", tracker.getElapsedTimeMillis());
        System.out.printf("  Comparisons: %,d%n", tracker.getComparisons());
        System.out.printf("  Swaps: %,d%n", tracker.getSwaps());
    }

    private static void benchmarkMerge(int n) {
        Random rand = new Random(42);

        // Create two heaps of size n/2
        int[] array1 = generateUniqueRandomArray(n / 2, rand);
        int[] array2 = generateUniqueRandomArray(n / 2, rand);

        MinHeap heap1 = new MinHeap(array1);
        MinHeap heap2 = new MinHeap(array2);

        PerformanceTracker tracker = new PerformanceTracker();
        tracker.startTimer();
        MinHeap merged = heap1.merge(heap2);
        tracker.stopTimer();

        System.out.printf("Merge (2 heaps of size %d):%n", n / 2);
        System.out.printf("  Time: %.3f ms%n", tracker.getElapsedTimeMillis());
        System.out.printf("  Resulting heap size: %d%n", merged.size());
    }

    private static void benchmarkBuildHeap(int n) {
        Random rand = new Random(42);
        int[] array = generateUniqueRandomArray(n, rand);

        long startTime = System.nanoTime();
        MinHeap heap = new MinHeap(array);
        long endTime = System.nanoTime();

        PerformanceTracker tracker = heap.getTracker();

        System.out.printf("Build-Heap (%d elements):%n", n);
        System.out.printf("  Time: %.3f ms%n", (endTime - startTime) / 1_000_000.0);
        System.out.printf("  Comparisons: %,d (%.2f per element)%n",
                tracker.getComparisons(), (double)tracker.getComparisons() / n);
        System.out.printf("  Swaps: %,d (%.2f per element)%n",
                tracker.getSwaps(), (double)tracker.getSwaps() / n);
    }

    private static void runScalabilityTest() {
        System.out.println("\nScalability Test (Insert Operation):");
        System.out.println("Size\t\tTime(ms)\tOps/ms\t\tComp/Op");
        System.out.println("-".repeat(60));

        int[] sizes = {100, 500, 1000, 5000, 10000, 50000, 100000};

        try {
            StringBuilder csv = new StringBuilder();
            csv.append("Size,TimeMs,Comparisons,Swaps,ComparisonsPerOp\n");

            for (int size : sizes) {
                MinHeap heap = new MinHeap(size);
                PerformanceTracker tracker = heap.getTracker();
                Random rand = new Random(42);

                int[] values = generateUniqueRandomArray(size, rand);

                tracker.startTimer();
                for (int i = 0; i < size; i++) {
                    heap.insert(values[i]);
                }
                tracker.stopTimer();

                double timeMs = tracker.getElapsedTimeMillis();
                double opsPerMs = size / timeMs;
                double compPerOp = (double)tracker.getComparisons() / size;

                System.out.printf("%,d\t\t%.2f\t\t%.2f\t\t%.2f%n",
                        size, timeMs, opsPerMs, compPerOp);

                csv.append(String.format("%d,%.3f,%d,%d,%.2f%n",
                        size, timeMs, tracker.getComparisons(),
                        tracker.getSwaps(), compPerOp));
            }

            // Export to CSV
            java.io.PrintWriter writer = new java.io.PrintWriter("C:\\Users\\Ulan\\IdeaProjects\\DAAassign2\\docs\\performance-plots\\benchmark_results.csv");
            writer.print(csv.toString());
            writer.close();

            System.out.println("\nResults exported to benchmark_results.csv");

        } catch (IOException e) {
            System.err.println("Error exporting results: " + e.getMessage());
        }
    }
}