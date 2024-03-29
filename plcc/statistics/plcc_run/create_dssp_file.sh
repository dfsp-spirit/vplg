#!/bin/bash
## create_dssp_file.sh -- create the dssp file for a pdb file (or its 1st model if the file has multiple models).
##
## Written by ts_2011

APPTAG="[CREATE_DSSP]"
CFG_FILE="../settings_statistics.cfg"

## get settings

source $CFG_FILE
## make sure the settings have been read
if [ -z "$SETTINGSREAD" ]; then
    echo "$APPTAG ##### ERROR: Could not load settings from file '$CFG_FILE'. #####"
    exit 1
fi

if [ -z "$1" ]; then
    echo "$APPTAG USAGE: $0 <PDB_FILE> [<OUTPUT_PATH>]"
    echo "$APPTAG example: $0 3kmf.pdb /tmp"
    exit 1
fi

PDBFILE="$1"
PDBID=${PDBFILE:0:4}
echo "$APPTAG Assuming PDB id '$PDBID'."

## check whether the PDB file exists and is readable
if [ ! -f "$PDBFILE" -a -r "$PDBFILE" ]; then
    echo "$APPTAG ERROR: Could not read PDB file '$PDBFILE'."
    exit 1
fi

## check for dssp binary
if [ "$OS" = "Windows_NT" ]; then
    DSSP="${DSSP}.exe"
    echo "$APPTAG Windows OS detected, assuming dssp binary '$DSSP'."
fi

if [ ! -f "$DSSP" -a -x "$DSSP" ]; then
    echo "$APPTAG ERROR: Could not find executable DSSP binary file at '$DSSP'. Check path and permissions."
    exit 1
fi


## check and run splitpdb
SPLITPDB="./splitpdb"
if [ ! -f "$SPLITPDB" -a -x "$SPLITPDB" ]; then
    echo "$APPTAG ERROR: Could not find splitpdb executable at '$SPLITPDB'. Check path and permissions."
    exit 1
fi

SPLITPDBFILE="${PDBFILE}.split"
$SPLITPDB $PDBFILE -o $SPLITPDBFILE $SPLITPDB_OPTIONS
if [ $? -eq 1 ]; then
    echo "$APPTAG ERROR: splitpdb failed (return value 1)."
    exit 1
fi

rm $PDBFILE && mv $SPLITPDBFILE $PDBFILE
if [ $? -ne 0 ]; then
    echo "$APPTAG ERROR: Could not move split PDB file '$SPLITPDBFILE' to '$PDBFILE'."
    exit 1
else
    echo "$APPTAG Deleted original PDB file and moved '$SPLITPDBFILE' to '$PDBFILE'. Running dsspcmbi..."
fi

## ok, run dssp
DSSPFILE="${PDBID}.dssp"
$DSSP $PDBFILE > $DSSPFILE 2>/dev/null
if [ $? -ne 0 ]; then
    echo "$APPTAG ERROR: Running dssp binary '$DSSP' for input file '$PDBFILE' failed."
    exit 1
fi

if [ ! -r "$DSSPFILE" ]; then
    echo "$APPTAG ERROR: No DSSP file found at '$DSSPFILE'."
    exit 1
fi

## check for broken DSSP files (no data part, i.e., 28 lines of less)
NUM_DSSP_LINES=$(wc -l $DSSPFILE 2>/dev/null | awk '{print $1}')
if [ $? -ne 0 ]; then
    echo "$APPTAG ERROR: Could not determine length of DSSP file."
    exit 1
fi

if [ $NUM_DSSP_LINES -le 28 ]; then
    # The data in DSSP files only starts in line 29, everything before that is the header.
    # If the file has no data section, the PDB file most likely contained no valid resides (e.g., DNA/RNA only).
    echo "$APPTAG ERROR: No data part in DSSP file '$DSSPFILE'."
fi

## OK, the DSSP file should be ok.
echo "$APPTAG DSSP file created at '$DSSPFILE'."

## We may need to move it though.
if [ -n "$2" ]; then
    DSSPOUT="$2"
    mv $DSSPFILE $DSSPOUT
    if [ $? -ne 0 ]; then
        echo "$APPTAG ERROR: Could not move DSSP file '$DSSPFILE' to '$DSSPOUT'."
        exit 1
    else
	echo "$APPTAG Moved DSSP file to '$DSSPOUT'."
    fi

fi

echo "$APPTAG All done, exiting."
exit 0
















