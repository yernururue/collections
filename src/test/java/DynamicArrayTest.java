import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {
    @Test
    void growsWhenMoreValuesAreAppended() {
        DynamicArray array = new DynamicArray();
        for (int i = 0; i < 100; i++) {
            array.add(i);
        }

        assertEquals(100, array.size());
        assertEquals(0, array.get(0));
        assertEquals(99, array.get(99));
    }

    @Test
    void insertsAndRemovesAtHeadMiddleAndEnd() {
        DynamicArray array = new DynamicArray();
        array.add(20);
        array.add(0, 10);
        array.add(2, 40);
        array.add(2, 30);

        assertEquals(4, array.size());
        assertEquals(10, array.get(0));
        assertEquals(30, array.get(2));
        assertEquals(40, array.remove(3));
        assertEquals(10, array.remove(0));
        assertEquals(30, array.remove(1));
        assertEquals(20, array.get(0));
    }

    @Test
    void rejectsInvalidIndexesWithoutChangingContents() {
        DynamicArray array = new DynamicArray();
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(-1, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(1, 1));

        array.add(7);
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.remove(1));
        assertThrows(IndexOutOfBoundsException.class, () -> array.add(2, 1));
        assertEquals(1, array.size());
        assertEquals(7, array.get(0));
    }

    @Test
    void containsDuplicatesAndCountsOperations() {
        DynamicArray array = new DynamicArray();
        array.add(7);
        array.add(7);
        array.add(9);
        array.resetMetrics();

        assertTrue(array.contains(7));
        assertTrue(array.contains(9));
        assertFalse(array.contains(8));
        assertEquals(7, array.get(1));
        assertEquals(8, array.getSteps());
        assertEquals(7, array.getComparisons());

        array.resetMetrics();
        array.add(0, 5);
        assertEquals(3, array.getMoves());
    }

    @Test
    void matchesArrayListAcrossRandomOperations() {
        DynamicArray array = new DynamicArray();
        ArrayList<Integer> expected = new ArrayList<>();
        Random random = new Random(42);

        for (int i = 0; i < 300; i++) {
            int index = random.nextInt(expected.size() + 1);
            int value = random.nextInt(21) - 10;
            array.add(index, value);
            expected.add(index, value);
        }
        for (int i = 0; i < 200; i++) {
            int index = random.nextInt(expected.size());
            assertEquals(expected.remove(index).intValue(), array.remove(index));
        }
        assertEquals(expected.size(), array.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i).intValue(), array.get(i));
        }
    }

    @Test
    void countsReadsAndShiftsForIndexedChanges() {
        DynamicArray array = new DynamicArray();
        array.add(10);
        array.add(20);
        array.add(30);
        array.resetMetrics();

        array.add(1, 15);
        assertEquals(2, array.getSteps());
        assertEquals(2, array.getMoves());

        array.resetMetrics();
        assertEquals(15, array.remove(1));
        assertEquals(3, array.getSteps());
        assertEquals(2, array.getMoves());
    }
}
