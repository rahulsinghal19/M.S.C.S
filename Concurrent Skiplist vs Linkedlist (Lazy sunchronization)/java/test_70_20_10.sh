#!/bin/sh

tests="ListTest"
threads="1 2 4 8 16 32"
ops="100000"
keySpace="100 1000 10000"
numTrials=10
numTests=180
count=0

echo 'Test,Threads,Ops,Trials,KeySpace,Search,Insert,Delete,ops/ms' > test_70_20_10.csv

for s in $tests;
do
    for t in $threads;
    do
	for o in $ops;
	do
	    for k in $keySpace;
	    do
		sum=0.0
		for ((n=1; n<=$numTrials; n++));
		do
		    time=`java $s $t $o $k 70 20 10`
		    sum=`echo "$sum + $time" | bc`
		    count=`expr $count + 1`
		    echo $count '/' $numTests
		done
		echo $s','$t','$o','$numTrials','$k',70,20,10,'`echo "$sum / $numTrials" | bc` >> test_70_20_10.csv
	    done
	done
    done
done

