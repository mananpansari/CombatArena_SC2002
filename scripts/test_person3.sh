#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "$0")/.." && pwd)"
out_dir="$repo_root/out/person3-tests"

mkdir -p "$out_dir"
find "$out_dir" -mindepth 1 -delete

cd "$repo_root"

javac -d "$out_dir" $(find src tests -name '*.java' | sort)
java -cp "$out_dir" Person3RegressionTest
