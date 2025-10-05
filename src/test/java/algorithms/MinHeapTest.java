package algorithms;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Comprehensive test suite for MinHeap implementation.
 * Tests correctness, edge cases, and heap property maintenance.
 */
@DisplayName("MinHeap Tests")
class MinHeapTest {

    private MinHeap heap;

    @BeforeEach
    void setUp() {
        heap = new MinHeap();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor creates empty heap")
        void testDefaultConstructor() {
            assertTrue(heap.isEmpty());
            assertEquals(0, heap.size());
        }

        @Test
        @DisplayName("Array constructor builds valid heap")
        void testArrayConstructor() {
            int[] array = {5, 3, 8, 1, 9, 2};
            MinHeap h = new MinHeap(array);

            assertEquals(6, h.size());
            assertEquals(1, h.peekMin());

            // Verify heap property
            assertHeapProperty(h);
        }

        @Test
        @DisplayName("Constructor rejects invalid capacity")
        void testInvalidCapacity() {
            assertThrows(IllegalArgumentException.class,
                    () -> new MinHeap(0));
            assertThrows(IllegalArgumentException.class,
                    () -> new MinHeap(-5));
        }

        @Test
        @DisplayName("Empty array constructor creates empty heap")
        void testEmptyArrayConstructor() {
            MinHeap h = new MinHeap(new int[0]);
            assertTrue(h.isEmpty());
        }

        @Test
        @DisplayName("Single element array constructor")
        void testSingleElementConstructor() {
            MinHeap h = new MinHeap(new int[]{42});
            assertEquals(1, h.size());
            assertEquals(42, h.peekMin());
        }
    }

    @Nested
    @DisplayName("Insert Tests")
    class InsertTests {

        @Test
        @DisplayName("Insert single element")
        void testInsertSingle() {
            heap.insert(5);
            assertEquals(1, heap.size());
            assertEquals(5, heap.peekMin());
        }

        @Test
        @DisplayName("Insert maintains heap property")
        void testInsertMaintainsHeapProperty() {
            int[] values = {15, 10, 20, 8, 25, 5, 30};
            for (int v : values) {
                heap.insert(v);
            }
            assertHeapProperty(heap);
        }

        @Test
        @DisplayName("Insert in ascending order")
        void testInsertAscending() {
            for (int i = 1; i <= 10; i++) {
                heap.insert(i);
            }
            assertEquals(1, heap.peekMin());
            assertHeapProperty(heap);
        }

        @Test
        @DisplayName("Insert in descending order")
        void testInsertDescending() {
            for (int i = 10; i >= 1; i--) {
                heap.insert(i);
            }
            assertEquals(1, heap.peekMin());
            assertHeapProperty(heap);
        }

        @Test
        @DisplayName("Insert duplicate values throws exception")
        void testInsertDuplicate() {
            heap.insert(5);
            assertThrows(IllegalArgumentException.class,
                    () -> heap.insert(5));
        }

        @Test
        @DisplayName("Insert triggers resize")
        void testInsertResize() {
            MinHeap smallHeap = new MinHeap(2);
            smallHeap.insert(1);
            smallHeap.insert(2);
            smallHeap.insert(3); // Should trigger resize

            assertEquals(3, smallHeap.size());
            assertHeapProperty(smallHeap);
        }
    }

    @Nested
    @DisplayName("Extract-Min Tests")
    class ExtractMinTests {

        @Test
        @DisplayName("Extract from empty heap throws exception")
        void testExtractFromEmpty() {
            assertThrows(NoSuchElementException.class,
                    () -> heap.extractMin());
        }

        @Test
        @DisplayName("Extract single element")
        void testExtractSingle() {
            heap.insert(42);
            assertEquals(42, heap.extractMin());
            assertTrue(heap.isEmpty());
        }

        @Test
        @DisplayName("Extract returns elements in sorted order")
        void testExtractSortedOrder() {
            int[] values = {15, 10, 20, 8, 25, 5, 30};
            for (int v : values) {
                heap.insert(v);
            }

            int[] expected = {5, 8, 10, 15, 20, 25, 30};
            for (int exp : expected) {
                assertEquals(exp, heap.extractMin());
            }
        }

        @Test
        @DisplayName("Extract maintains heap property")
        void testExtractMaintainsProperty() {
            for (int i = 1; i <= 20; i++) {
                heap.insert(i * 3);
            }

            heap.extractMin();
            heap.extractMin();

            assertHeapProperty(heap);
        }

        @Test
        @DisplayName("Extract all elements empties heap")
        void testExtractAll() {
            int[] values = {5, 3, 8, 1, 9};
            for (int v : values) {
                heap.insert(v);
            }

            while (!heap.isEmpty()) {
                heap.extractMin();
            }

            assertTrue(heap.isEmpty());
            assertEquals(0, heap.size());
        }
    }

    @Nested
    @DisplayName("Decrease-Key Tests")
    class DecreaseKeyTests {

        @Test
        @DisplayName("Decrease key updates value correctly")
        void testDecreaseKey() {
            heap.insert(10);
            heap.insert(20);
            heap.insert(15);

            heap.decreaseKey(20, 5);

            assertEquals(5, heap.peekMin());
            assertHeapProperty(heap);
        }

        @Test
        @DisplayName("Decrease key maintains heap property")
        void testDecreaseKeyHeapProperty() {
            int[] values = {50, 30, 40, 20, 25, 35, 45};
            for (int v : values) {
                heap.insert(v);
            }

            heap.decreaseKey(45, 10);

            assertHeapProperty(heap);
            assertTrue(heap.contains(10));
            assertFalse(heap.contains(45));
        }

        @Test
        @DisplayName("Decrease non-existent key throws exception")
        void testDecreaseNonExistent() {
            heap.insert(10);
            assertThrows(IllegalArgumentException.class,
                    () -> heap.decreaseKey(20, 5));
        }

        @Test
        @DisplayName("Increase key throws exception")
        void testIncreaseKey() {
            heap.insert(10);
            assertThrows(IllegalArgumentException.class,
                    () -> heap.decreaseKey(10, 20));
        }

        @Test
        @DisplayName("Decrease to same value throws exception")
        void testDecreaseSameValue() {
            heap.insert(10);
            assertThrows(IllegalArgumentException.class,
                    () -> heap.decreaseKey(10, 10));
        }

        @Test
        @DisplayName("Decrease to existing value throws exception")
        void testDecreaseToExisting() {
            heap.insert(10);
            heap.insert(20);
            assertThrows(IllegalArgumentException.class,
                    () -> heap.decreaseKey(20, 10));
        }

        @Test
        @DisplayName("Decrease key to become new minimum")
        void testDecreaseToMin() {
            heap.insert(10);
            heap.insert(20);
            heap.insert(15);

            heap.decreaseKey(20, 5);

            assertEquals(5, heap.peekMin());
        }
    }

    @Nested
    @DisplayName("Merge Tests")
    class MergeTests {

        @Test
        @DisplayName("Merge two non-empty heaps")
        void testMergeTwoHeaps() {
            MinHeap heap1 = new MinHeap();
            heap1.insert(5);
            heap1.insert(10);
            heap1.insert(15);

            MinHeap heap2 = new MinHeap();
            heap2.insert(3);
            heap2.insert(12);
            heap2.insert(20);

            MinHeap merged = heap1.merge(heap2);

            assertEquals(6, merged.size());
            assertEquals(3, merged.peekMin());
            assertHeapProperty(merged);
        }

        @Test
        @DisplayName("Merge with empty heap")
        void testMergeWithEmpty() {
            heap.insert(5);
            heap.insert(10);

            MinHeap empty = new MinHeap();
            MinHeap merged = heap.merge(empty);

            assertEquals(2, merged.size());
            assertEquals(5, merged.peekMin());
        }

        @Test
        @DisplayName("Merge empty with non-empty")
        void testMergeEmptyWithNonEmpty() {
            MinHeap empty = new MinHeap();
            MinHeap nonEmpty = new MinHeap();
            nonEmpty.insert(10);
            nonEmpty.insert(5);

            MinHeap merged = empty.merge(nonEmpty);

            assertEquals(2, merged.size());
            assertEquals(5, merged.peekMin());
        }

        @Test
        @DisplayName("Merge with null returns copy")
        void testMergeWithNull() {
            heap.insert(5);
            heap.insert(10);

            MinHeap merged = heap.merge(null);

            assertEquals(2, merged.size());
            assertEquals(5, merged.peekMin());
        }

        @Test
        @DisplayName("Merge maintains heap property")
        void testMergeMaintainsProperty() {
            Random rand = new Random(42);
            MinHeap heap1 = new MinHeap();
            MinHeap heap2 = new MinHeap();

            for (int i = 0; i < 50; i++) {
                heap1.insert(rand.nextInt(1000));
            }
            for (int i = 0; i < 50; i++) {
                heap2.insert(rand.nextInt(1000) + 1000);
            }

            MinHeap merged = heap1.merge(heap2);

            assertEquals(100, merged.size());
            assertHeapProperty(merged);
        }

        @Test
        @DisplayName("Merge does not modify original heaps")
        void testMergeDoesNotModify() {
            heap.insert(5);
            heap.insert(10);

            MinHeap other = new MinHeap();
            other.insert(3);
            other.insert(7);

            int heap1Size = heap.size();
            int heap2Size = other.size();

            heap.merge(other);

            assertEquals(heap1Size, heap.size());
            assertEquals(heap2Size, other.size());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Peek on empty heap throws exception")
        void testPeekEmpty() {
            assertThrows(NoSuchElementException.class,
                    () -> heap.peekMin());
        }

        @Test
        @DisplayName("Single element operations")
        void testSingleElement() {
            heap.insert(42);
            assertEquals(42, heap.peekMin());
            assertEquals(42, heap.extractMin());
            assertTrue(heap.isEmpty());
        }

        @Test
        @DisplayName("Large heap maintains correctness")
        void testLargeHeap() {
            Random rand = new Random(42);
            int[] values = new int[1000];

            for (int i = 0; i < 1000; i++) {
                values[i] = rand.nextInt(10000);
                heap.insert(values[i]);
            }

            Arrays.sort(values);

            for (int i = 0; i < 1000; i++) {
                assertEquals(values[i], heap.extractMin());
            }
        }

        @Test
        @DisplayName("Negative values handled correctly")
        void testNegativeValues() {
            heap.insert(-5);
            heap.insert(10);
            heap.insert(-20);
            heap.insert(0);

            assertEquals(-20, heap.extractMin());
            assertEquals(-5, heap.extractMin());
            assertEquals(0, heap.extractMin());
            assertEquals(10, heap.extractMin());
        }

        @Test
        @DisplayName("Contains method works correctly")
        void testContains() {
            heap.insert(5);
            heap.insert(10);
            heap.insert(15);

            assertTrue(heap.contains(5));
            assertTrue(heap.contains(10));
            assertTrue(heap.contains(15));
            assertFalse(heap.contains(20));
            assertFalse(heap.contains(0));
        }
    }

    // Helper method to verify heap property
    private void assertHeapProperty(MinHeap h) {
        int[] array = h.toArray();
        for (int i = 0; i < h.size(); i++) {
            int leftChild = 2 * i + 1;
            int rightChild = 2 * i + 2;

            if (leftChild < h.size()) {
                assertTrue(array[i] <= array[leftChild],
                        "Heap property violated at index " + i);
            }
            if (rightChild < h.size()) {
                assertTrue(array[i] <= array[rightChild],
                        "Heap property violated at index " + i);
            }
        }
    }
}
