#!/bin/sh

PDBID="$1"

echo "Downloading PDB file for ${PDBID}..."
wget http://www.rcsb.org/pdb/files/${PDBID}.pdb.gz
tar xzf ${PDBID}.pdb.gz

echo "Downloading DSSP file for ${PDBID}..."
wget ftp://ftp.cmbi.ru.nl/pub/molbio/data/dssp/ ${PDBID}.dssp