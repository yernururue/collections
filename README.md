# Assignment 2: Data Structures

This project implements `DynamicArray`, `MyLinkedList`, and `MinHeap` with `int` values. It also runs the four workloads from the assignment and saves operation counts and timings.

Use Java 25 and Maven from the project root.

```bash
mvn test
mvn package
```

Run the full benchmark with one command:

```bash
./run_benchmark.sh
```

It writes `results/results.csv`. The run uses `Random(42)`, the four required sizes, two warm-up runs per case, and the median of five measured runs. W1 and W2 compare the array with the list, W3 tests head and middle updates, and W4 inserts and extracts heap values.

To regenerate the four PNG graphs, install Matplotlib in a virtual environment and run the plotting script:

```bash
python3 -m venv .venv
.venv/bin/pip install -r requirements-plots.txt
.venv/bin/python results/plots/make_plots.py
```

The analysis and loop invariant proofs are in `REPORT.md`.
