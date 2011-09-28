#!/bin/bash

if [ $# -lt 1 ] ; then
	echo "Usage: $0 <class-to-run>"
	exit 1
fi

if [ ! -d "./run" ] ; then
	mkdir run
	mkdir run/res
fi

cd run

java -cp ../bin $1


