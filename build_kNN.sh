#!/bin/sh

javac *.java
java -Xmx2048m build_kNN $@