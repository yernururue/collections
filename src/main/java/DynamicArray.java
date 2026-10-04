class DynamicArray {

    private int[] data;
    private int size;


    public DynamicArray() {
        data = new int[5];
        size = 0;
    }

    public void growIfFull() {
        if (size < data.length) {
            return;
        }

        int[] newData = new int[data.length];


        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }

        data = newData;
    }

    public void add(int value) {
        growIfFull();

        data[size] = value;
        size++;
    }

    public void insert(int index, int value) {
        if (index<0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        growIfFull();

        for (int i = size; i > index; i--) {
            data[i] = data[i-1];
        }

        data[index] = value;
        size++;
    }

    public int delete(int index) {
        if (index<0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        int removed = data[index];
        for (int i = index; i<size-1; i++) {
            data[i] = data[i+1];
        }

        size--;
        return removed;
    }

    public int get(int index) {
        if (index<0 || index>size) {
            throw new IndexOutOfBoundsException();
        }
        return data[index];
    }

    public int size() {
        return size;
    }
}