import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MinHeapTest {
    @Test
    void emptyHeapRejectsPeekAndExtract() {
        MinHeap heap = new MinHeap();

        assertEquals(0, heap.size());
        assertThrows(IllegalStateException.class, heap::peekMin);
        assertThrows(IllegalStateException.class, heap::extractMin);
    }

    @Test
    void insertsAndExtractsInSortedOrderIncludingDuplicates() throws Exception {
        MinHeap heap = new MinHeap();
        int[] values = {7, -2, 7, 0, -9, 4, -2};

        for (int value : values) {
            heap.insert(value);
            assertHeapProperty(heap);
        }

        assertEquals(-9, heap.peekMin());
        assertEquals(7, heap.size());
        int[] expected = {-9, -2, -2, 0, 4, 7, 7};
        for (int value : expected) {
            assertEquals(value, heap.extractMin());
            assertHeapProperty(heap);
        }
        assertEquals(0, heap.size());
    }

    @Test
    void growsAndMatchesPriorityQueueAcrossRandomOperations() throws Exception {
        MinHeap heap = new MinHeap();
        PriorityQueue<Integer> expected = new PriorityQueue<>();
        Random random = new Random(42);

        for (int i = 0; i < 300; i++) {
            int value = random.nextInt(201) - 100;
            heap.insert(value);
            expected.add(value);
            assertEquals(expected.peek().intValue(), heap.peekMin());
            assertHeapProperty(heap);
        }
        while (!expected.isEmpty()) {
            assertEquals(expected.remove().intValue(), heap.extractMin());
            assertHeapProperty(heap);
        }
    }

    @Test
    void recordsArrayReadsMovesAndComparisons() {
        MinHeap heap = new MinHeap();
        heap.insert(10);
        heap.insert(5);
        heap.insert(1);
        heap.peekMin();
        heap.extractMin();

        assertTrue(heap.getSteps() > 0);
        assertTrue(heap.getMoves() > 0);
        assertTrue(heap.getComparisons() > 0);

        heap.resetMetrics();
        assertEquals(0, heap.getSteps());
        assertEquals(0, heap.getMoves());
        assertEquals(0, heap.getComparisons());
    }

    private static void assertHeapProperty(MinHeap heap) throws Exception {
        Field arrayField = MinHeap.class.getDeclaredField("values");
        arrayField.setAccessible(true);
        int[] values = (int[]) arrayField.get(heap);

        for (int child = 1; child < heap.size(); child++) {
            int parent = (child - 1) / 2;
            assertTrue(values[parent] <= values[child],
                    "parent " + parent + " exceeds child " + child);
        }
    }
}
