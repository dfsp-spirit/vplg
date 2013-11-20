#!/bin/sh
echo "Counting protein chains..."
NUMCHAINS=$(find -mindepth 3 -maxdepth 3 -type d | wc -l)
echo "Found $NUMCHAINS protein chains."

