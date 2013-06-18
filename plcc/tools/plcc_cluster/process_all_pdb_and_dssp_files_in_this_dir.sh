#!/bin/sh
## process_all_pdb_and_dssp_files_in_this_dir.sh
## 
## This script runs PLCC for all PDB files in the current directory. It assumes they are already 
## extracted and split, and that the dssp file is available in this dir as well.

APPTAG="[PROC_ALL_HERE]"


echo "$APPTAG Processing all prepared PDB files in this directory which have valid DSSP files available."

NUM_PDB=0
NUM_DSSP_AVAILABLE=0
NUM_PLCC_FAIL=0

for PDBFILE in *.pdb; do   
  PDBID=${PDBFILE%%.*};
  let NUM_PDB++
  DSSPFILE="$PDBID.dssp";
  if [ -f "$DSSPFILE" ]; then
    echo "$APPTAG Processing PDB file $PDBFILE with PDBID $PDBID, using DSSP file $DSSPFILE.";
    let NUM_DSSP_AVAILABLE++
    ./plcc $PDBID --use-database; 

    if [ $? -ne 0 ]; then
      let NUM_PLCC_FAIL++
      echo "$APPTAG ERROR: Running PLCC failed for protein '$PDBID', skipping."
    fi
  else
    echo "$APPTAG ERROR: No DSSP file found at $DSSPFILE for file $PDBFILE with PDBID: $PDBID, skipping.";
  fi
done

echo "$APPTAG Found $NUM_PDB PDB files, $NUM_DSSP_AVAILABLE of them had DSSP files available."
echo "$APPTAG Ran PLCC for $NUM_DSSP_AVAILABLE PDB files, failed for $NUM_PLCC_FAIL of them."
echo "$APPTAG Done, exiting."