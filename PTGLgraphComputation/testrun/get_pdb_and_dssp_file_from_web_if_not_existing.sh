#!/bin/sh

# written ts in 2016
# changed by jnw in 2018: add check if files already exist.
#
# Expects a PDB-ID as argument and downloads PDB- and DSSP-File into the current directory if they do not exist already.

APPTAG="[GET_PD_WEB]"
PDBID="$1"

if [ -z "$PDBID" ]; then
  echo "$APPTAG USAGE: $0 <PDB_ID>"
  exit 0
fi

echo "$APPTAG Handling PDB and DSSP files for ${PDBID}..."

if [ -f ${PDBID}.pdb ]; then
    echo "$APPTAG Skipping PDB-File as it already exists!"
else
    echo "$APPTAG  -Downloading gzipped PDB file for ${PDBID} to '${PDBID}.pdb.gz'..."
    wget -O ${PDBID}.pdb.gz http://www.rcsb.org/pdb/files/${PDBID}.pdb.gz
    
    echo "$APPTAG  -Uncompressing PDB file for ${PDBID} to '${PDBID}.pdb'..."
    gunzip --force ${PDBID}.pdb.gz
fi


if [ -f ${PDBID}.dssp ]; then
    echo "$APPTAG Skipping DSSP-File as it already exists!"
else
    echo "$APPTAG  -Downloading DSSP file for ${PDBID} to '${PDBID}.dssp'..."
    wget -O ${PDBID}.dssp ftp://ftp.cmbi.ru.nl/pub/molbio/data/dssp/${PDBID}.dssp
fi

if [ ! -f "${PDBID}.pdb" ]; then
  echo "$APPTAG [WARNING]: PDB output file does not exist."
fi

if [ ! -f "${PDBID}.dssp" ]; then
  echo "$APPTAG [WARNING]: DSSP output file does not exist."
fi

echo "$APPTAG Done handling PDB and DSSP files for ${PDBID}. (Check output above for errors, I don't care.)"

