package algorithms;

import metrics.PerformanceTracker;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Min-Heap implementation with decrease-key and merge operations.
 *
 * Time Complexity:
 * - Insert: O(log n)
 * - Extract-Min: O(log n)
 * - Decrease-Key: O(log n)
 * - Merge: O(n + m) where n and m are heap sizes
 * - Build-Heap: O(n)
 *
 * Space Complexity: O(n) for storing n elements plus O(n) for position map
 */
public class MinHeap {
    private static final int DEFAULT_CAPACITY = 16;
    private static final double GROWTH_FACTOR = 2.0;

    private int[] heap;
    private int size;
    private int capacity;
    private final PerformanceTracker tracker;

    // Maps element values to their indices for O(log n) decrease-key
    private final Map<Integer, Integer> positionMap;

    /**
     * Constructs an empty min-heap with default capacity.
     */
    public MinHeap() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Constructs an empty min-heap with specified initial capacity.
     *
     * @param initialCapacity the initial capacity
     */
    public MinHeap(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = initialCapacity;
        this.heap = new int[capacity];
        this.size = 0;
        this.tracker = new PerformanceTracker();
        this.positionMap = new HashMap<>();
        tracker.incrementMemoryAllocations();
    }

    /**
     * Constructs a min-heap from an array using bottom-up heapify.
     * Time Complexity: O(n)
     *
     * @param array the input array
     */
    public MinHeap(int[] array) {
        this(array.length);
        System.arraycopy(array, 0, heap, 0, array.length);
        this.size = array.length;
        tracker.incrementArrayAccesses(array.length);

        // Build position map
        for (int i = 0; i < size; i++) {
            positionMap.put(heap[i], i);
            tracker.incrementArrayAccesses();
        }

        // Bottom-up heapify - O(n) complexity
        buildHeap();
    }

    /**
     * Builds a valid min-heap from an unordered array.
     * Uses bottom-up approach starting from last non-leaf node.
     * Time Complexity: O(n)
     */
    private void buildHeap() {
        // Start from last non-leaf node and heapify down
        for (int i = (size / 2) - 1; i >= 0; i--) {
            heapifyDown(i);
        }
    }

    /**
     * Inserts an element into the heap.
     * Time Complexity: O(log n)
     *
     * @param value the value to insert
     */
    public void insert(int value) {
        // Check for duplicates
        if (positionMap.containsKey(value)) {
            throw new IllegalArgumentException(
                    "Duplicate values not allowed: " + value);
        }

        // Resize if necessary
        if (size == capacity) {
            resize();
        }

        // Insert at end and bubble up
        heap[size] = value;
        positionMap.put(value, size);
        tracker.incrementArrayAccesses();
        heapifyUp(size);
        size++;
    }

    /**
     * Extracts and returns the minimum element.
     * Time Complexity: O(log n)
     *
     * @return the minimum element
     * @throws NoSuchElementException if heap is empty
     */
    public int extractMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }

        int min = heap[0];
        tracker.incrementArrayAccesses();
        positionMap.remove(min);

        // Move last element to root and heapify down
        heap[0] = heap[size - 1];
        tracker.incrementArrayAccesses(2);
        size--;

        if (size > 0) {
            positionMap.put(heap[0], 0);
            heapifyDown(0);
        }

        return min;
    }

    /**
     * Returns the minimum element without removing it.
     * Time Complexity: O(1)
     *
     * @return the minimum element
     * @throws NoSuchElementException if heap is empty
     */
    public int peekMin() {
        if (isEmpty()) {
            throw new NoSuchElementException("Heap is empty");
        }
        tracker.incrementArrayAccesses();
        return heap[0];
    }

    /**
     * Decreases the value of a key in the heap.
     * Time Complexity: O(log n)
     *
     * @param oldValue the current value to decrease
     * @param newValue the new smaller value
     * @throws IllegalArgumentException if oldValue not in heap or newValue >= oldValue
     */
    public void decreaseKey(int oldValue, int newValue) {
        if (!positionMap.containsKey(oldValue)) {
            throw new IllegalArgumentException(
                    "Value not in heap: " + oldValue);
        }

        if (newValue >= oldValue) {
            throw new IllegalArgumentException(
                    "New value must be smaller than old value");
        }

        if (positionMap.containsKey(newValue)) {
            throw new IllegalArgumentException(
                    "New value already exists in heap: " + newValue);
        }

        int index = positionMap.get(oldValue);
        tracker.incrementArrayAccesses();

        // Update value and position map
        heap[index] = newValue;
        tracker.incrementArrayAccesses();
        positionMap.remove(oldValue);
        positionMap.put(newValue, index);

        // Bubble up since value decreased
        heapifyUp(index);
    }

    /**
     * Restores heap property by moving element up.
     * Time Complexity: O(log n)
     *
     * @param index starting index
     */
    private void heapifyUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            tracker.incrementArrayAccesses(2);
            tracker.incrementComparisons();

            if (heap[index] < heap[parentIndex]) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    /**
     * Restores heap property by moving element down.
     * Time Complexity: O(log n)
     *
     * @param index starting index
     */
    private void heapifyDown(int index) {
        while (true) {
            int smallest = index;
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;

            // Find smallest among node and its children
            if (leftChild < size) {
                tracker.incrementArrayAccesses(2);
                tracker.incrementComparisons();
                if (heap[leftChild] < heap[smallest]) {
                    smallest = leftChild;
                }
            }

            if (rightChild < size) {
                tracker.incrementArrayAccesses(2);
                tracker.incrementComparisons();
                if (heap[rightChild] < heap[smallest]) {
                    smallest = rightChild;
                }
            }

            // If smallest is not current node, swap and continue
            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    /**
     * Swaps two elements in the heap and updates position map.
     *
     * @param i first index
     * @param j second index
     */
    private void swap(int i, int j) {
        int temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;

        // Update position map
        positionMap.put(heap[i], i);
        positionMap.put(heap[j], j);

        tracker.incrementSwaps();
        tracker.incrementArrayAccesses(4);
    }

    /**
     * Resizes the internal array when capacity is reached.
     */
    private void resize() {
        capacity = (int) (capacity * GROWTH_FACTOR);
        heap = Arrays.copyOf(heap, capacity);
        tracker.incrementMemoryAllocations();
        tracker.incrementArrayAccesses(size);
    }

    /**
     * Creates a copy of this heap.
     *
     * @return a new heap with the same elements
     */
    private MinHeap copy() {
        MinHeap newHeap = new MinHeap(this.capacity);
        System.arraycopy(this.heap, 0, newHeap.heap, 0, this.size);
        newHeap.size = this.size;
        newHeap.positionMap.putAll(this.positionMap);
        return newHeap;
    }

    // Utility methods
    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public PerformanceTracker getTracker() {
        return tracker;
    }

    public boolean contains(int value) {
        return positionMap.containsKey(value);
    }

    /**
     * Returns array representation of heap.
     *
     * @return copy of heap array
     */
    public int[] toArray() {
        return Arrays.copyOf(heap, size);
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(heap[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
