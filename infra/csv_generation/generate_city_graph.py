"""
Generate a complete undirected graph CSV (one row per unordered pair).
For N nodes, rows = N*(N-1)//2.

Columns: node1,node2,distance

Warning: this can produce very large files. For k=100 (N=2600) rows = 3,378,700.
Consider gzip output to save disk space.
"""

import csv
import string
import random
import gzip
from tqdm import tqdm

# -----------------------
k = 10                 # nodes per letter -> N = 26 * k
max_distance = 20        # distance sampled uniformly 1..max_distance
random_seed = 42
output_file = "complete_undirected_distances.csv.gz"  # gzipped output
use_gzip = True
# -----------------------

random.seed(random_seed)

letters = list(string.ascii_lowercase)
nodes = [f"{L}{i}" for L in letters for i in range(1, k + 1)]
N = len(nodes)

def sample_distance():
    return random.randint(1, max_distance)

# Choose writer (gzip or plain)
if use_gzip:
    f = gzip.open(output_file, "wt", newline="")
else:
    f = open(output_file, "w", newline="")

with f:
    writer = csv.writer(f)
    writer.writerow(["node1", "node2", "distance"])

    # Iterate over all unordered pairs (i < j) — streaming
    total_pairs = N * (N - 1) // 2
    pbar = tqdm(total=total_pairs, desc="Writing pairs")
    for i in range(N):
        ni = nodes[i]
        # j runs from i+1 .. N-1
        for j in range(i + 1, N):
            nj = nodes[j]
            writer.writerow([ni, nj, sample_distance()])
            pbar.update(1)
    pbar.close()

print(f"✅ Complete undirected graph written to '{output_file}'")
print(f"Nodes: {N}, edges (rows): {total_pairs}")
