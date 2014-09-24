#!/bin/sh

APPTAG="[CLEANUP]"

WHAT="$1"

if [ "$WHAT" = "" ]; then
  echo "$APPTAG Usage: './cleanup.sh <what>', where <what> is one of: all | images | graphs | metadata"
  exit 0
fi

CLEAN_IMAGES="NO"
CLEAN_GRAPHS="NO"
CLEAN_METADATA="NO"

if [ "$WHAT" = "all" ]; then
  CLEAN_IMAGES="YES"
  CLEAN_GRAPHS="YES"
  CLEAN_METADATA="YES"
fi

if [ "$WHAT" = "images" ]; then
  CLEAN_IMAGES="YES"
fi

if [ "$WHAT" = "graphs" ]; then
  CLEAN_GRAPHS="YES"
fi

if [ "$WHAT" = "metadata" ]; then
  CLEAN_METADATA="YES"
fi



## images
if [ "$CLEAN_IMAGES" = "YES" ]; then
  echo "$APPTAG Cleaning images..."
  rm *.png *.svg *.pdf
fi

## graphs
if [ "$CLEAN_GRAPHS" = "YES" ]; then
  echo "$APPTAG Cleaning graphs..."
  rm *.tgf *.gv *.gml *.plg *.fg *.kavosh *.el *.el_ntl *.json *.el_edges *.graph
fi

## meta data
if [ "$CLEAN_METADATA" = "YES" ]; then
  echo "$APPTAG Cleaning metadata..."
  rm *.chains *.geo *.geolig *.contactstats *.dssplig *.models  *.pymol  *.ligands *.resmap *.jmol *.ptgllinnot
fi

echo "$APPTAG Done, exiting."

