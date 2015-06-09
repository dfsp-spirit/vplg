#!/bin/sh
for FILE in countsMatlabFormat.m countsNovaFormat.csv counts.plain graphsMatlabFormat.m graphsStatistics.csv graphsStatisticsMatlabFormat.m; 
do
    if [ -f $FILE ]; then
      rm $FILE;
    fi
done
