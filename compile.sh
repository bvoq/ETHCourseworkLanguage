#!/bin/sh
find . -name "*.class" -type f -delete
javac commandlineInterface/RunProgram.java
javac DrawingApp.java
#Run plotter using java DrawingApp
#Run language using java commandlineInterface/RunProgram
