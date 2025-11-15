#!/usr/bin/env python3
# select_metro_and_map.py
# Usage: python select_metro_and_map.py
import gzip, csv, random
from collections import Counter
import pandas as pd

random.seed(42)

INFILE = "complete_undirected_distances.csv.gz"
OUT_CSV = "location_nearby.csv"
TARGET_PCT = 0.30
MAX_STATIONS_TRY = 30

# --- load edges and nodes ---
edges = []
nodes_set = set()
with gzip.open(INFILE, "rt", newline="") as f:
    reader = csv.DictReader(f)
    for r in reader:
        u = r["node1"].strip()
        v = r["node2"].strip()
        d = float(r["distance"])
        edges.append((u, v, d))
        nodes_set.add(u); nodes_set.add(v)

nodes = sorted(nodes_set)
N = len(nodes)
target_count = int(round(TARGET_PCT * N))

# build distance lookup
dist_map = {n: {} for n in nodes}
for u, v, d in edges:
    dist_map[u][v] = d
    dist_map[v][u] = d

# inspect distances
all_dists = [d for (_,_,d) in edges]
max_dist = int(max(all_dists))

# Try thresholds and station counts to reach ~30%
results = []
for t in range(1, max_dist+1):
    # neighbor sets within threshold t
    neighbor_sets = {n: set(other for other,d in dist_map[n].items() if d <= t) for n in nodes}
    for S in range(1, MAX_STATIONS_TRY+1):
        counts = sorted(((n, len(neighbor_sets[n])) for n in nodes), key=lambda x: x[1], reverse=True)
        topS = [n for n,_ in counts[:S]]
        covered = set()
        for st in topS:
            covered.update(neighbor_sets[st])
            covered.add(st)
        covered_count = len(covered)
        results.append((t, S, covered_count))

res_df = pd.DataFrame(results, columns=["threshold","S","covered"])
res_df["diff"] = (res_df["covered"] - target_count).abs()

# select best candidate (within ±5% if possible, else minimal diff)
tolerance = int(round(0.05 * N))
candidates = res_df[res_df["diff"] <= tolerance].sort_values(["diff","S"])
if candidates.empty:
    candidates = res_df.sort_values(["diff","S"]).head(10)

best = candidates.iloc[0].to_dict()
chosen_t = int(best["threshold"])
chosen_S = int(best["S"])

# Build mapping: choose top S stations and map nodes to nearest chosen station within threshold
neighbor_sets = {n: set(other for other,d in dist_map[n].items() if d <= chosen_t) for n in nodes}
counts_chosen = sorted(((n, len(neighbor_sets[n])) for n in nodes), key=lambda x: x[1], reverse=True)
selected_stations = [n for n,_ in counts_chosen[:chosen_S]]

mapping = {}
for n in nodes:
    cand = []
    for idx, st in enumerate(selected_stations, start=1):
        if n == st:
            cand.append((0.0, idx, st))
        elif st in dist_map[n] and dist_map[n][st] <= chosen_t:
            cand.append((dist_map[n][st], idx, st))
    if cand:
        cand.sort(key=lambda x: (x[0], x[1]))
        _, station_idx, _ = cand[0]
        mapping[n] = f"ME{station_idx}"
    else:
        mapping[n] = ""

# write output csv
with open(OUT_CSV, "w", newline="") as f:
    writer = csv.writer(f)
    writer.writerow(["city_point", "near_metro_dropoff"])
    for n in nodes:
        writer.writerow([n, mapping[n]])

print("Nodes:", N)
print("Target (30%):", target_count)
print("Chosen threshold:", chosen_t)
print("Stations chosen:", chosen_S, selected_stations)
print("Mapped nodes:", sum(1 for v in mapping.values() if v))
print("Mapping CSV written to", OUT_CSV)
