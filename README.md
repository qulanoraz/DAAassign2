# Min-Heap Implementation with Decrease-Key and Merge Operations

A comprehensive Min-Heap data structure implementation in Java with advanced operations including decrease-key and heap merge, complete with performance tracking and benchmarking capabilities.

## Features

- **Core Operations**
    - Insert: O(log n)
    - Extract-Min: O(log n)
    - Peek-Min: O(1)
    - Build-Heap: O(n) using bottom-up approach

- **Advanced Operations**
    - Decrease-Key: O(log n) with position tracking
    - Merge: O(n + m) for merging two heaps

- **Performance Tracking**
    - Comparisons counter
    - Swaps counter
    - Array accesses counter
    - Memory allocations counter
    - Execution time measurement

## Complexity Analysis

### Time Complexity

| Operation | Best Case | Average Case | Worst Case |
|-----------|-----------|--------------|------------|
| Insert | O(1) | O(log n) | O(log n) |
| Extract-Min | O(log n) | O(log n) | O(log n) |
| Peek-Min | O(1) | O(1) | O(1) |
| Decrease-Key | O(1) | O(log n) | O(log n) |
| Merge | O(n + m) | O(n + m) | O(n + m) |
| Build-Heap | O(n) | O(n) | O(n) |

### Space Complexity

- O(n) for heap array storage
- O(n) for position map (enables O(log n) decrease-key)
- Total: O(n)

## Building the Project

### Compile
mvn clean compile

### Run tests
mvn test

### Run benchmarks
mvn exec:java -Dexec.mainClass="cli.BenchmarkRunner"

### Package
mvn package


## Usage Examples
### Basic Operations
// Create empty heap

MinHeap heap = new MinHeap();

// Insert elements

heap.insert(15);

heap.insert(10);

heap.insert(20);

// Peek minimum

int min = heap.peekMin(); // Returns 10

// Extract minimum

int extracted = heap.extractMin(); // Returns 10

### Build from Array

int[] array = {5, 3, 8, 1, 9, 2};

MinHeap heap = new MinHeap(array); // O(n) construction

### Decrease-Key Operation
heap.insert(50);

heap.insert(30);

heap.insert(40);

// Decrease 40 to 5

heap.decreaseKey(40, 5); // Now 5 is the minimum

### Merge Operation
heap.insert(50);

heap.insert(30);

heap.insert(40);

// Decrease 40 to 5

heap.decreaseKey(40, 5); // Now 5 is the minimum

### Performance Tracking
MinHeap heap = new MinHeap();

PerformanceTracker tracker = heap.getTracker();

// Perform operations

for (int i = 0; i < 1000; i++) {

heap.insert(i);

}

// View metrics

System.out.println(tracker.getSummary());

System.out.println("Comparisons: " + tracker.getComparisons());

System.out.println("Swaps: " + tracker.getSwaps());

// Export to CSV

tracker.exportToCSV("metrics.csv");


## Testing
The test suite includes:

Constructor tests (empty, array-based, edge cases)

Insert tests (single, multiple, duplicates, resize)

Extract-Min tests (sorted order, heap property)

Decrease-Key tests (correctness, heap property maintenance)

Merge tests (various combinations, property preservation)

Edge cases (empty heap, single element, negative values)

Parameterized stress tests (10 to 1000 elements)

Property-based testing (heap property verification)

### Run tests with:
mvn test

## Benchmarking
The benchmark suite measures:

Insert Performance: Average time and comparisons per insertion

Extract-Min Performance: Time for complete heap sort

Decrease-Key Performance: Average operation time

Merge Performance: Time to merge heaps of various sizes

Build-Heap Performance: Bottom-up construction efficiency

Scalability Analysis: Performance across sizes 100 to 100,000

### Run benchmarks:
mvn exec:java -Dexec.mainClass="cli.BenchmarkRunner"

Results are exported to benchmark_results.csv for analysis.

## Implementation Details
### Position Map for Decrease-Key
The implementation uses a HashMap to track element positions, enabling O(log n) decrease-key operations. Without this, decrease-key would require O(n) search time.

### Bottom-Up Heap Construction
The array constructor uses the Floyd algorithm for O(n) heap construction, which is more efficient than successive insertions O(n log n).

### Merge Strategy
The merge operation concatenates arrays and rebuilds using bottom-up heapify for O(n + m) complexity, which is optimal for binary heaps.
