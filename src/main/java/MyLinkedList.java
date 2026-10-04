public class MyLinkedList {
    private static class Node {
        int value;
        Node next;

        Node(int value) {
            this.value = value;
        }
    }

    private Node head;
    private Node tail;
    private int size;

    private long steps;
    private long moves;
    private long comparisons;

    public void add(int value) {
        Node newNode = new Node(value);
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        moves += 2;
        size++;
    }

    public void add(int index, int value) {
        checkPositionIndex(index);

        if (index == size) {
            add(value);
            return;
        }

        Node newNode = new Node(value);
        if (index == 0) {
            newNode.next = head;
            head = newNode;
        } else {
            Node previous = nodeAt(index - 1);
            newNode.next = previous.next;
            previous.next = newNode;
        }
        moves += 2;
        size++;
    }

    public int remove(int index) {
        checkElementIndex(index);

        Node removed;
        if (index == 0) {
            removed = head;
            head = head.next;
            moves++;
            if (size == 1) {
                tail = null;
                moves++;
            }
        } else {
            Node previous = nodeAt(index - 1);
            removed = previous.next;
            previous.next = removed.next;
            moves++;
            if (removed == tail) {
                tail = previous;
                moves++;
            }
        }
        size--;
        return removed.value;
    }

    public int get(int index) {
        checkElementIndex(index);
        return nodeAt(index).value;
    }

    public boolean contains(int value) {
        Node current = head;
        while (current != null) {
            comparisons++;
            if (current.value == value) {
                return true;
            }
            if (current.next != null) {
                steps++;
            }
            current = current.next;
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

    private Node nodeAt(int index) {
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
            steps++;
        }
        return current;
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
