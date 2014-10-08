#!/bin/sh
## fill_database.sh -- fill the VPLG database from the PDB and DSSP files in this directory
## This script assumes that plcc.jar and some PDB/DSSP files are in this directory. It also assumes the DB is setup correctly.
## NOTE: Using coils is broken (in various ways).


## settings ###

RUN_GRAPHLETANALYSER="YES"
PLCC_OPTIONS="-u -f --cluster"
DELETE_CLUSTER_CHAINS_FILE="NO"

### end of settings ###

APPTAG="[FDB]"
NODSSP_LIST="dssp_files_missing.lst"

PLCC_OUTPUT_DIR="."

if [ -f $NODSSP_LIST ]; then
   rm $NODSSP_LIST
   touch $NODSSP_LIST
fi

echo "$APPTAG Adding all PDB files in this directory to the VPLG database... (assuming they are named '<pdbid>.pdb')"

for PDBFILE in ls *.pdb;
do
	echo "$APPTAG Handling PDB file '$PDBFILE'"
	fullfilename=$(basename "$PDBFILE")
        extension=${fullfilename##*.}
	filename=${fullfilename%.*}

	PDBID="$filename"
	PDB_STRING_LENGTH=${#PDBID}
	if [ PDB_STRING_LENGTH -ne 4 ]; then
	  echo "$APPTAG: ERROR: Expected PDB ID of length 4 but found '$filename', the input file may not be named after the pattern '<pdbid>.pdb'. Skipping file '$fullfilename'."
	  continue
	fi
	
	DSSPFILE="$filename.dssp"
	if [ ! -r $DSSPFILE ]; then
	   echo "$filename" >> $NODSSP_LIST
	else
	    ./plcc $filename $PLCC_OPTIONS	    
	fi
	
	if [ "$RUN_GRAPHLETANALYSER" = "YES" ]; then
                echo "$APPTAG Running Graphletanalyser to compute graphlets for all chains of the PDB file."
		CHAINS_FILE="${PLCC_OUTPUT_DIR}/${PDBID}.chains"
		if [ ! -f "$CHAINS_FILE" ]; then
		       echo "$APPTAG ##### ERROR: No chains file found at '$CHAINS_FILE', is '--cluster' set as PLCC command line option?"
	        else
	                echo "$APPTAG Chains file found at '$CHAINS_FILE'."
			for CHAIN in $(cat ${CHAINS_FILE});
			do
				ALBE_GML_GRAPHFILE="${PLCC_OUTPUT_DIR}/${PDBID}_${CHAIN}_albe_PG.gml"
				if [ -f "$ALBE_GML_GRAPHFILE" ]; then
				        echo "$APPTAG Running Graphletanalyser on file '$ALBE_GML_GRAPHFILE' for albe graph of $PDBID chain '$CHAIN'."
					./graphletanalyser --useDatabase $ALBE_GML_GRAPHFILE
				else
					echo "$APPTAG ##### ERROR:The albe GML graph file was not found at '$ALBE_GML_GRAPHFILE', cannot run graphletanalyser on it."
				fi
			done

			if [ "$DELETE_CLUSTER_CHAINS_FILE" = "YES" ]; then
				rm "$CHAINS_FILE"
			fi
		fi
	    fi
done


echo "$APPTAG All done, exiting."
exit 0

# EOF
