#!/bin/sh

APPTAG="[GET_PD_WEB]"
PDBID="$1"

if [ -z "$PDBID" ]; then
  echo "$APPTAG USAGE: $0 <PDB_ID>"
  exit 0
fi

echo "$APPTAG Handling PDB and DSSP files for ${PDBID}..."

echo "$APPTAG  -Downloading gzipped PDB file for ${PDBID} to '${PDBID}.pdb.gz'..."
wget -O ${PDBID}.pdb.gz http://www.rcsb.org/pdb/files/${PDBID}.pdb.gz

echo "$APPTAG  -Uncompressing PDB file for ${PDBID} to '${PDBID}.pdb'..."
gunzip --force ${PDBID}.pdb.gz

echo "$APPTAG  -Downloading DSSP file for ${PDBID} to '${PDBID}.dssp'..."
wget -O ${PDBID}.dssp ftp://ftp.cmbi.ru.nl/pub/molbio/data/dssp/${PDBID}.dssp

if [ ! -f "${PDBID}.pdb" ]; then
  echo "$APPTAG  -WARNING: PDB output file does not exist."
fi

if [ ! -f "${PDBID}.dssp" ]; then
  echo "$APPTAG  -WARNING: DSSP output file does not exist."
fi

echo "$APPTAG Done handling PDB and DSSP files for ${PDBID}. (Check output above for errors, I don't care.)"

