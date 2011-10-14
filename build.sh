#!/bin/bash

if [ -d "./bin/" ] ; then
	echo "Removing existing binaries"
	rm ./bin/ -r
fi

if [ -d "./run/" ] ; then
	echo "Removing running environment"
	rm ./run/ -r
fi

echo "Creating binaries..."
mkdir bin

javac -g -d ./bin $( find ./src -type f | grep .java )

echo "Done."
