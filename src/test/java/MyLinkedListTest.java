import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class MyLinkedListTest {
    @Test
    void appendsAndReadsValues() {
        MyLinkedList list = new MyLinkedList();
        list.add(10);
        list.add(20);
        list.add(30);

        assertEquals(3, list.size());
        assertEquals(10, list.get(0));
        assertEquals(20, list.get(1));
        assertEquals(30, list.get(2));
    }

    @Test
    void insertsAtHeadMiddleAndEnd() {
        MyLinkedList list = new MyLinkedList();
        list.add(20);
        list.add(0, 10);
        list.add(1, 15);
        list.add(3, 30);

        assertEquals(4, list.size());
        assertEquals(10, list.get(0));
        assertEquals(15, list.get(1));
        assertEquals(20, list.get(2));
        assertEquals(30, list.get(3));
    }

    @Test
    void removesHeadMiddleAndTailAndCanAppendAgain() {
        MyLinkedList list = new MyLinkedList();
        list.add(10);
        list.add(20);
        list.add(30);
        list.add(40);

        assertEquals(10, list.remove(0));
        assertEquals(30, list.remove(1));
        assertEquals(40, list.remove(1));
        list.add(50);

        assertEquals(2, list.size());
        assertEquals(20, list.get(0));
        assertEquals(50, list.get(1));
    }

    @Test
    void containsHandlesDuplicatesAndMissingValues() {
        MyLinkedList list = new MyLinkedList();
        assertFalse(list.contains(7));
        list.add(7);
        list.add(7);
        list.add(9);

        assertTrue(list.contains(7));
        assertTrue(list.contains(9));
        assertFalse(list.contains(8));
    }

    @Test
    void rejectsInvalidIndexesAndPreservesContents() {
        MyLinkedList list = new MyLinkedList();
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 10));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(1, 10));

        list.add(5);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.add(2, 10));
        assertEquals(1, list.size());
        assertEquals(5, list.get(0));
    }

    @Test
    void countsTraversalsComparisonsAndLinkUpdates() {
        MyLinkedList list = new MyLinkedList();
        list.add(10);
        list.add(20);
        list.add(30);
        list.resetMetrics();

        assertEquals(30, list.get(2));
        assertTrue(list.contains(20));
        assertEquals(3, list.getSteps());
        assertEquals(2, list.getComparisons());

        list.resetMetrics();
        list.add(0, 5);
        assertTrue(list.getMoves() > 0);
    }

    @Test
    void matchesArrayListAcrossRandomOperations() {
        MyLinkedList list = new MyLinkedList();
        ArrayList<Integer> expected = new ArrayList<>();
        Random random = new Random(42);

        for (int i = 0; i < 300; i++) {
            int index = random.nextInt(expected.size() + 1);
            int value = random.nextInt(21) - 10;
            list.add(index, value);
            expected.add(index, value);
        }
        for (int i = 0; i < 200; i++) {
            int index = random.nextInt(expected.size());
            assertEquals(expected.remove(index).intValue(), list.remove(index));
        }
        assertEquals(expected.size(), list.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).intValue(), list.get(i));
        }
    }
}
