#!/bin/sh

# written by jnw in 2018

# Expects a path to a file with PDB ids (one per line) as argument and downloads the mmcif type DSSP file via rsync.

if [ ! -f "$1" ]
    then
    echo "[Error] Could not find file list. Exiting now."
    exit 1
fi

while IFS= read aline
do
    if [ ! -f "$aline".dssp ]
    then 
        rsync -avz rsync://rsync.cmbi.ru.nl/dssp-from-mmcif/"$aline".dssp ./
        if [ ! -f "$aline".dssp ]
        then
            echo "[Error] Rsync seems to have failed for $aline as file is not existing."
        fi
    else
	echo "$aline" already existing. Skipping that.
    fi
done < $1
