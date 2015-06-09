#!/bin/sh
NEWLY_COMPILED_FILE="../dist/Debug/GNU-Linux-x86/graphletanalyser"

APPTAG="[RUNSCRIPT]"

GML_FILE="7tim_A_albe_PG.gml"

if [ "$1" = "--compile" ]; then
  MYDIR=$(pwd)
  cd .. && make && cd $MYDIR
  
  if [ -n "$2" ]; then
    GML_FILE="$2"
  fi
elif [ "$1" = "--svn-compile" ]; then
  MYDIR=$(pwd)
  cd .. && svn up && make && cd $MYDIR
  
  if [ -n "$2" ]; then
    GML_FILE="$2"
  fi
else
  if [ -n "$1" ]; then
    GML_FILE="$1"
  fi
fi 

  

if [ -f "$NEWLY_COMPILED_FILE" ]; then
  cp "$NEWLY_COMPILED_FILE" . && echo "$APPTAG Copied newly compiled binary to this dir."
else
  echo "$APPTAG No newly compiled binary found."
fi

GA="./graphletanalyser"
if [ ! -f "$GA" ]; then
  echo "$APPTAG ERROR: Executable '$GA' not found." 
else 
#  $GA --saveGraphletDetails --useDatabase $GML_FILE
  $GA --silent  $GML_FILE
fi
