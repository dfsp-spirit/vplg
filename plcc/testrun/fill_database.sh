#!/bin/sh
## fill_database.sh -- fill the VPLG database from the PDB and DSSP files in this directory
## This script assumes that plcc.jar and some PDB/DSSP files are in this directory. It also assumes the DB is setup correctly.
## NOTE: Using coils is broken (in various ways).

APPTAG="[FDB]"
NODSSP_LIST="dssp_files_missing.lst"
USE_COILS="NO"

if [ -f $NODSSP_LIST ]; then
   rm $NODSSP_LIST
   touch $NODSSP_LIST
fi

echo "$APPTAG Adding all PDB files in this directory to the VPLG database..."

for PDBFILE in ls *.pdb;
do
	echo "$APPTAG Handling PDB file '$PDBFILE'"
	fullfilename=$(basename "$PDBFILE")
        extension=${fullfilename##*.}
	filename=${fullfilename%.*}

	DSSPFILE="$filename.dssp"
	if [ ! -r $DSSPFILE ]; then
	   echo "$filename" >> $NODSSP_LIST
	else
	    ./plcc $filename -u -E
	    if [ "$USE_COILS" = "YES" ]; then
	        ./plcc $filename -a
            fi
	fi
done


echo "$APPTAG All done, exiting."
exit 0

# EOF
