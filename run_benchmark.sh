#!/bin/sh
set -eu
mvn -q -DskipTests package
java -cp target/classes Benchmark
