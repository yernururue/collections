public class DynamicArray {
    private int[] data = new int[5];
    private int size;

    private long steps;
    private long moves;
    private long comparisons;

    public void add(int value) {
        growIfFull();
        data[size] = value;
        size++;
    }

    public void add(int index, int value) {
        checkPositionIndex(index);
        growIfFull();

        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
            steps++;
            moves++;
        }
        data[index] = value;
        size++;
    }

    public int remove(int index) {
        checkElementIndex(index);
        int removed = data[index];
        steps++;

        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
            steps++;
            moves++;
        }
        size--;
        return removed;
    }

    public int get(int index) {
        checkElementIndex(index);
        steps++;
        return data[index];
    }

    public boolean contains(int value) {
        for (int i = 0; i < size; i++) {
            int current = data[i];
            steps++;
            comparisons++;
            if (current == value) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }

    public long getSteps() {
        return steps;
    }

    public long getMoves() {
        return moves;
    }

    public long getComparisons() {
        return comparisons;
    }

    public void resetMetrics() {
        steps = 0;
        moves = 0;
        comparisons = 0;
    }

    private void growIfFull() {
        if (size < data.length) {
            return;
        }
        int[] larger = new int[data.length * 2];
        for (int i = 0; i < size; i++) {
            larger[i] = data[i];
            steps++;
            moves++;
        }
        data = larger;
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
    }

    private void checkPositionIndex(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("index: " + index + ", size: " + size);
        }
    }
}
