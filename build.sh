#!/bin/bash

if [[ $# -lt 1 ]]; then
	echo "No options specified, aborting"
	exit 1
fi

if [ -d "./bin/" ] ; then
	rm ./bin/ -r
fi

if [ -d "./run/" ] ; then
	rm ./run/ -r
fi


mkdir bin

javac -g -d ./bin $( find ./src -type f | grep .java )


