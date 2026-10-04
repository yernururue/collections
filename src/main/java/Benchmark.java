import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

public class Benchmark {
    private static final int ACCESS_COUNT = 10_000;
    private static final int SEARCH_COUNT = 1_000;
    private static final int CHANGE_COUNT = 1_000;
    private static final int WARMUP_RUNS = 2;
    private static final int JVM_WARMUP_RUNS = 20;
    private static volatile long sink;

    public static void main(String[] args) throws IOException {
        Path output = Path.of("results", "results.csv");
        run(output, new int[]{100, 1_000, 10_000, 100_000}, 5);
        System.out.println("Saved " + output);
    }

    static void run(Path output, int[] sizes, int runs) throws IOException {
        if (runs < 1 || runs % 2 == 0) {
            throw new IllegalArgumentException("runs must be a positive odd number");
        }
        for (int n : sizes) {
            if (n < 1) {
                throw new IllegalArgumentException("n must be positive");
            }
        }

        warmUpJvm();
        Files.createDirectories(output.toAbsolutePath().getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8)) {
            writer.write("workload,variant,structure,n,time_ms,steps,moves,comparisons");
            writer.newLine();

            for (int n : sizes) {
                int[] data = makeData(n);
                int[] indexes = makeIndexes(n);
                int[] queries = makeQueries(data);

                write(writer, "W1", "-", "DynamicArray", n,
                        measure(runs, () -> accessArray(data, indexes)));
                write(writer, "W1", "-", "MyLinkedList", n,
                        measure(runs, () -> accessList(data, indexes)));
                write(writer, "W2", "-", "DynamicArray", n,
                        measure(runs, () -> searchArray(data, queries)));
                write(writer, "W2", "-", "MyLinkedList", n,
                        measure(runs, () -> searchList(data, queries)));
                write(writer, "W3", "head", "DynamicArray", n,
                        measure(runs, () -> changeArray(data, 0)));
                write(writer, "W3", "head", "MyLinkedList", n,
                        measure(runs, () -> changeList(data, 0)));
                write(writer, "W3", "middle", "DynamicArray", n,
                        measure(runs, () -> changeArray(data, n / 2)));
                write(writer, "W3", "middle", "MyLinkedList", n,
                        measure(runs, () -> changeList(data, n / 2)));
                write(writer, "W4", "-", "MinHeap", n,
                        measure(runs, () -> processHeap(data)));
            }
        }
    }

    private static int[] makeData(int n) {
        Random random = new Random(42);
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = random.nextInt(1_000_000);
        }
        return data;
    }

    private static void warmUpJvm() {
        int[] data = makeData(1_000);
        int[] indexes = makeIndexes(data.length);
        int[] queries = makeQueries(data);
        for (int i = 0; i < JVM_WARMUP_RUNS; i++) {
            accessArray(data, indexes);
            accessList(data, indexes);
            searchArray(data, queries);
            searchList(data, queries);
            changeArray(data, 0);
            changeList(data, 0);
            changeArray(data, data.length / 2);
            changeList(data, data.length / 2);
            processHeap(data);
        }
    }

    private static int[] makeIndexes(int n) {
        Random random = new Random(42);
        int[] indexes = new int[ACCESS_COUNT];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = random.nextInt(n);
        }
        return indexes;
    }

    private static int[] makeQueries(int[] data) {
        Random random = new Random(42);
        int[] queries = new int[SEARCH_COUNT];
        for (int i = 0; i < queries.length; i++) {
            queries[i] = i % 2 == 0 ? data[random.nextInt(data.length)] : -i;
        }
        return queries;
    }

    private static Metrics measure(int runs, Supplier<Metrics> operation) {
        for (int i = 0; i < WARMUP_RUNS; i++) {
            operation.get();
        }
        Metrics[] samples = new Metrics[runs];
        for (int i = 0; i < runs; i++) {
            samples[i] = operation.get();
        }
        for (int i = 1; i < samples.length; i++) {
            Metrics value = samples[i];
            int j = i - 1;
            while (j >= 0 && samples[j].timeNs() > value.timeNs()) {
                samples[j + 1] = samples[j];
                j--;
            }
            samples[j + 1] = value;
        }
        return samples[runs / 2];
    }

    private static void write(BufferedWriter writer, String workload, String variant,
                              String structure, int n, Metrics sample) throws IOException {
        String timeMs = String.format(Locale.US, "%.6f", sample.timeNs() / 1_000_000.0);
        writer.write(workload + "," + variant + "," + structure + "," + n + ","
                + timeMs + "," + sample.steps() + "," + sample.moves() + ","
                + sample.comparisons());
        writer.newLine();
    }

    private static Metrics accessArray(int[] data, int[] indexes) {
        DynamicArray array = new DynamicArray();
        for (int value : data) {
            array.add(value);
        }
        array.resetMetrics();
        long sum = 0;
        long start = System.nanoTime();
        for (int index : indexes) {
            sum += array.get(index);
        }
        long elapsed = System.nanoTime() - start;
        sink = sum;
        return new Metrics(elapsed, array.getSteps(), array.getMoves(), array.getComparisons());
    }

    private static Metrics accessList(int[] data, int[] indexes) {
        MyLinkedList list = new MyLinkedList();
        for (int value : data) {
            list.add(value);
        }
        list.resetMetrics();
        long sum = 0;
        long start = System.nanoTime();
        for (int index : indexes) {
            sum += list.get(index);
        }
        long elapsed = System.nanoTime() - start;
        sink = sum;
        return new Metrics(elapsed, list.getSteps(), list.getMoves(), list.getComparisons());
    }

    private static Metrics searchArray(int[] data, int[] queries) {
        DynamicArray array = new DynamicArray();
        for (int value : data) {
            array.add(value);
        }
        array.resetMetrics();
        int found = 0;
        long start = System.nanoTime();
        for (int value : queries) {
            if (array.contains(value)) {
                found++;
            }
        }
        long elapsed = System.nanoTime() - start;
        sink = found;
        if (found != SEARCH_COUNT / 2) {
            throw new IllegalStateException("Search data is not balanced");
        }
        return new Metrics(elapsed, array.getSteps(), array.getMoves(), array.getComparisons());
    }

    private static Metrics searchList(int[] data, int[] queries) {
        MyLinkedList list = new MyLinkedList();
        for (int value : data) {
            list.add(value);
        }
        list.resetMetrics();
        int found = 0;
        long start = System.nanoTime();
        for (int value : queries) {
            if (list.contains(value)) {
                found++;
            }
        }
        long elapsed = System.nanoTime() - start;
        sink = found;
        if (found != SEARCH_COUNT / 2) {
            throw new IllegalStateException("Search data is not balanced");
        }
        return new Metrics(elapsed, list.getSteps(), list.getMoves(), list.getComparisons());
    }

    private static Metrics changeArray(int[] data, int index) {
        DynamicArray array = new DynamicArray();
        for (int value : data) {
            array.add(value);
        }
        array.resetMetrics();
        long sum = 0;
        long start = System.nanoTime();
        for (int i = 0; i < CHANGE_COUNT; i++) {
            array.add(index, -i - 1);
        }
        for (int i = 0; i < CHANGE_COUNT; i++) {
            sum += array.remove(index);
        }
        long elapsed = System.nanoTime() - start;
        sink = sum;
        return new Metrics(elapsed, array.getSteps(), array.getMoves(), array.getComparisons());
    }

    private static Metrics changeList(int[] data, int index) {
        MyLinkedList list = new MyLinkedList();
        for (int value : data) {
            list.add(value);
        }
        list.resetMetrics();
        long sum = 0;
        long start = System.nanoTime();
        for (int i = 0; i < CHANGE_COUNT; i++) {
            list.add(index, -i - 1);
        }
        for (int i = 0; i < CHANGE_COUNT; i++) {
            sum += list.remove(index);
        }
        long elapsed = System.nanoTime() - start;
        sink = sum;
        return new Metrics(elapsed, list.getSteps(), list.getMoves(), list.getComparisons());
    }

    private static Metrics processHeap(int[] data) {
        MinHeap heap = new MinHeap();
        int[] output = new int[data.length];
        long start = System.nanoTime();
        for (int value : data) {
            heap.insert(value);
        }
        for (int i = 0; i < output.length; i++) {
            output[i] = heap.extractMin();
        }
        long elapsed = System.nanoTime() - start;
        for (int i = 1; i < output.length; i++) {
            if (output[i] < output[i - 1]) {
                throw new IllegalStateException("Heap output is not sorted");
            }
        }
        sink = output[output.length - 1];
        return new Metrics(elapsed, heap.getSteps(), heap.getMoves(), heap.getComparisons());
    }
}
