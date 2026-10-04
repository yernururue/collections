import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BenchmarkTest {
    @TempDir
    Path tempDir;

    @Test
    void writesAllWorkloadsWithMeasuredCounters() throws Exception {
        Path output = tempDir.resolve("results.csv");
        Benchmark.run(output, new int[]{10}, 1);

        List<String> lines = Files.readAllLines(output);
        assertEquals(10, lines.size());
        assertEquals("workload,variant,structure,n,time_ms,steps,moves,comparisons", lines.get(0));

        int w1 = 0;
        int w2 = 0;
        int w3 = 0;
        int w4 = 0;
        for (int i = 1; i < lines.size(); i++) {
            String[] columns = lines.get(i).split(",");
            assertEquals(8, columns.length);
            assertEquals("10", columns[3]);
            assertTrue(Double.parseDouble(columns[4]) >= 0);
            assertTrue(Long.parseLong(columns[5]) >= 0);
            assertTrue(Long.parseLong(columns[6]) >= 0);
            assertTrue(Long.parseLong(columns[7]) >= 0);

            switch (columns[0]) {
                case "W1" -> w1++;
                case "W2" -> w2++;
                case "W3" -> {
                    assertTrue(columns[1].equals("head") || columns[1].equals("middle"));
                    w3++;
                }
                case "W4" -> w4++;
                default -> fail("Unknown workload: " + columns[0]);
            }
            if (columns[0].equals("W1") && columns[2].equals("DynamicArray")) {
                assertEquals("10000", columns[5]);
                assertEquals("0", columns[6]);
                assertEquals("0", columns[7]);
            }
        }
        assertEquals(2, w1);
        assertEquals(2, w2);
        assertEquals(4, w3);
        assertEquals(1, w4);
    }

    @Test
    void producesTheSameOperationCountsForTheSameSeed() throws Exception {
        Path first = tempDir.resolve("first.csv");
        Path second = tempDir.resolve("second.csv");
        Benchmark.run(first, new int[]{100}, 3);
        Benchmark.run(second, new int[]{100}, 3);

        List<String> firstLines = Files.readAllLines(first);
        List<String> secondLines = Files.readAllLines(second);
        assertEquals(firstLines.size(), secondLines.size());
        for (int i = 1; i < firstLines.size(); i++) {
            String[] a = firstLines.get(i).split(",");
            String[] b = secondLines.get(i).split(",");
            for (int column : new int[]{0, 1, 2, 3, 5, 6, 7}) {
                assertEquals(a[column], b[column], "row " + i + ", column " + column);
            }
        }
    }
}
