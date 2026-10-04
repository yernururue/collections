public class MinHeap {
    private int[] values = new int[16];
    private int size;

    private long steps;
    private long moves;
    private long comparisons;

    public void insert(int value) {
        if (size == values.length) {
            grow();
        }
        values[size] = value;
        bubbleUp(size);
        size++;
    }

    public int peekMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }
        steps++;
        return values[0];
    }

    public int extractMin() {
        if (size == 0) {
            throw new IllegalStateException("Heap is empty");
        }

        int minimum = values[0];
        steps++;
        size--;
        if (size > 0) {
            int last = values[size];
            steps++;
            values[0] = last;
            moves++;
            bubbleDown(0);
        }
        return minimum;
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

    private void grow() {
        int[] larger = new int[values.length * 2];
        for (int i = 0; i < size; i++) {
            larger[i] = values[i];
            steps++;
            moves++;
        }
        values = larger;
    }

    private void bubbleUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            int parentValue = values[parent];
            int childValue = values[index];
            steps += 2;
            comparisons++;
            if (parentValue <= childValue) {
                break;
            }
            values[parent] = childValue;
            values[index] = parentValue;
            moves += 2;
            index = parent;
        }
    }

    private void bubbleDown(int index) {
        while (index < size / 2) {
            int left = 2 * index + 1;
            int smaller = left;
            int childValue = values[left];
            steps++;

            int right = left + 1;
            if (right < size) {
                int rightValue = values[right];
                steps++;
                comparisons++;
                if (rightValue < childValue) {
                    smaller = right;
                    childValue = rightValue;
                }
            }

            int parentValue = values[index];
            steps++;
            comparisons++;
            if (parentValue <= childValue) {
                break;
            }
            values[index] = childValue;
            values[smaller] = parentValue;
            moves += 2;
            index = smaller;
        }
    }
}
