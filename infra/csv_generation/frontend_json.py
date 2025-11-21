#!/usr/bin/env python3
"""
Generate a JSON file with key "locations" and value a flattened list:
[A1, A2, ..., AK, B1, B2, ..., BK, ..., Z1, Z2, ..., ZK]

Usage:
    python gen_locations.py 5
    # -> writes locations.json with A1..A5, B1..B5, ..., Z1..Z5

You can also run without args and the script will prompt for K.
"""

import json
import string
import sys
from typing import List

def make_locations(k: int) -> List[str]:
    if k < 0:
        raise ValueError("K must be non-negative")
    letters = list(string.ascii_uppercase)  # A..Z
    locations = []
    for letter in letters:
        for i in range(1, k + 1):
            locations.append(f"{letter}{i}")
    return locations

def main():
    # Parse K from command line or prompt the user
    if len(sys.argv) >= 2:
        try:
            k = int(sys.argv[1])
        except ValueError:
            print("Argument K must be an integer.")
            sys.exit(1)
    else:
        try:
            k = int(input("Enter K (non-negative integer): ").strip())
        except Exception:
            print("Invalid input. Exiting.")
            sys.exit(1)

    if k < 0:
        print("K must be non-negative.")
        sys.exit(1)

    data = {"locations": make_locations(k)}

    out_filename = "locations.json"
    with open(out_filename, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False, indent=2)

    print(f"Wrote {len(data['locations'])} entries to {out_filename}")

if __name__ == "__main__":
    main()