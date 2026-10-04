import csv
from collections import defaultdict
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.ticker import ScalarFormatter


RESULTS = Path(__file__).resolve().parent.parent
SIZES = [100, 1000, 10000, 100000]
COLORS = {
    "DynamicArray": "#2563eb",
    "MyLinkedList": "#dc2626",
    "MinHeap": "#16a34a",
}
STYLES = {"-": "-", "head": "-", "middle": "--"}
MARKERS = {"DynamicArray": "o", "MyLinkedList": "s", "MinHeap": "^"}
TITLES = {
    "W1": "W1 Random Access",
    "W2": "W2 Search",
    "W3": "W3 Insert and Remove",
    "W4": "W4 Priority Processing",
}
FILES = {
    "W1": "w1_random_access.png",
    "W2": "w2_search.png",
    "W3": "w3_insert_remove.png",
    "W4": "w4_priority.png",
}
PANELS = [
    ("time_ms", "Time (ms)"),
    ("steps", "Steps (count)"),
    ("moves", "Moves (count)"),
    ("comparisons", "Comparisons (count)"),
]


def read_results():
    with (RESULTS / "results.csv").open(newline="", encoding="utf-8") as source:
        return list(csv.DictReader(source))


def plot_workload(rows, workload):
    groups = defaultdict(list)
    for row in rows:
        if row["workload"] == workload:
            groups[(row["variant"], row["structure"])].append(row)

    figure, axes = plt.subplots(2, 2, figsize=(12, 8))
    for axis, (metric, label) in zip(axes.flat, PANELS):
        positive = False
        for (variant, structure), values in sorted(groups.items()):
            values.sort(key=lambda row: int(row["n"]))
            x = [int(row["n"]) for row in values]
            y = [float(row[metric]) for row in values]
            positive = positive or any(value > 0 for value in y)
            name = structure if variant == "-" else f"{structure} {variant}"
            style = STYLES[variant] if variant != "-" else (
                "--" if structure == "MyLinkedList" else "-")
            axis.plot(x, y, marker=MARKERS[structure], linewidth=2, markersize=5,
                      color=COLORS[structure], linestyle=style, label=name)

        axis.set_xscale("log")
        axis.set_xticks(SIZES)
        axis.xaxis.set_major_formatter(ScalarFormatter())
        if metric == "time_ms":
            axis.set_yscale("log")
        elif positive:
            axis.set_yscale("symlog", linthresh=1)
            axis.set_ylim(bottom=0)
        else:
            axis.set_ylim(0, 1)
            axis.set_yticks([0, 1])
            axis.text(0.5, 0.5, "All counts are 0", transform=axis.transAxes,
                      ha="center", va="center", color="#666666")
        axis.set_xlabel("n (elements)")
        axis.set_ylabel(label)
        axis.grid(True, alpha=0.3)
        axis.legend(fontsize=8)

    figure.suptitle(TITLES[workload], fontsize=16)
    figure.tight_layout()
    figure.savefig(RESULTS / "plots" / FILES[workload], dpi=180)
    plt.close(figure)


def main():
    rows = read_results()
    for workload in TITLES:
        plot_workload(rows, workload)


if __name__ == "__main__":
    main()
