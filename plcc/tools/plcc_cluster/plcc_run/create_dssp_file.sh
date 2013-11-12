#!/bin/bash
## create_dssp_file.sh -- create the dssp file for a pdb file (or its 1st model if the file has multiple models).
##
## Written by ts_2011

APPTAG="[CREATE_DSSP]"
CFG_FILE="../settings_statistics.cfg"

ERRORLOG="/dev/stderr"

## get settings

source $CFG_FILE
## make sure the settings have been read
if [ -z "$SETTINGSREAD" ]; then
    echo "$APPTAG ##### ERROR: Could not load settings from file '$CFG_FILE'. #####"
    echo "$APPTAG ##### ERROR: Could not load settings from file '$CFG_FILE'. #####" >> $ERRORLOG
    exit 1
fi

if [ -z "$1" ]; then
    echo "$APPTAG USAGE: $0 <PDB_FILE> [<OUTPUT_PATH>]" >> $ERRORLOG
    echo "$APPTAG USAGE: $0 <PDB_FILE> [<OUTPUT_PATH>]"
    echo "$APPTAG example: $0 3kmf.pdb /tmp"
    exit 1
fi

PDBFILE="$1"
PDBID=${PDBFILE:0:4}
PDBID_CENTER=${PDBFILE:1:2}
echo "$APPTAG Assuming PDB id '$PDBID'."

DSSPFILE="${PDBID}.dssp"

######################## Simply copy the file if the server has a DSSP database #####################


if [ "$SERVER_HAS_LOCAL_DSSP_DATABASE" = "true" ]; then
    echo "$APPTAG Server has local DSSP database at '$LOCAL_DSSP_DATA_DIR', copying files from there."
    
    if [ "$LOCAL_DSSP_DATA_DIR_USES_SUBDIRS" = "true" ]; then 
        DSSP_SOURCE_FILE="${LOCAL_DSSP_DATA_DIR}${PDBID_CENTER}/${PDBID}.dssp"        
        echo "$APPTAG Local DSSP database uses subdirectory structure."
    else 
        DSSP_SOURCE_FILE="${LOCAL_DSSP_DATA_DIR}${PDBID}.dssp"
        echo "$APPTAG Local DSSP database does not use subdirectory structure."        
    fi
    
    if [ -f $DSSP_SOURCE_FILE ]; then
        cp $DSSP_SOURCE_FILE $DSSPFILE
       
        if [ -f $DSSP_FILE ]; then
            echo "$APPTAG DSSP file copied from local DSSP database."

	    ## We may need to move it though.
	    if [ -n "$2" ]; then
		DSSPOUT="$2"
		mv $DSSPFILE $DSSPOUT
		if [ $? -ne 0 ]; then
		    echo "$APPTAG ERROR: Could not move DSSP file '$DSSPFILE' to '$DSSPOUT'."
		    echo "$APPTAG ERROR: Could not move DSSP file '$DSSPFILE' to '$DSSPOUT'." >> $ERRORLOG
		    exit 1
		else
		    echo "$APPTAG Moved DSSP file to '$DSSPOUT'."
                    exit 0
		fi
            else
              ## we are done already
              exit 0
	    fi            
            
        else
          echo "$APPTAG Could not copy DSSP file from local DB, trying to create it..."
        fi
    else
        echo "$APPTAG DSSP source file '$DSSP_SOURCE_FILE' does not exist in local DSSP database, trying to create it..."
    fi

    
fi


########################## we need to create it if we move after this line #########################

## check whether the PDB file exists and is readable
if [ ! -f "$PDBFILE" -a -r "$PDBFILE" ]; then
    echo "$APPTAG ERROR: Could not read PDB file '$PDBFILE'."
    echo "$APPTAG ERROR: Could not read PDB file '$PDBFILE'." >> $ERRORLOG
    exit 1
fi


## we now copy dsspcmbi to the plcc_run/ directory, so it should be right here (path from settings is ignored)
DSSP="./dsspcmbi"

## check for dssp binary
if [ "$OS" = "Windows_NT" ]; then
    DSSP="${DSSP}.exe"
    echo "$APPTAG Windows OS detected, assuming dssp binary '$DSSP'."
fi

if [ ! -f "$DSSP" -a -x "$DSSP" ]; then
    echo "$APPTAG ERROR: Could not find executable DSSP binary file at '$DSSP'. Check path and permissions."
    echo "$APPTAG ERROR: Could not find executable DSSP binary file at '$DSSP'. Check path and permissions." >> $ERRORLOG
    exit 1
fi


## check and run splitpdb
SPLITPDB="./splitpdb"
if [ ! -f "$SPLITPDB" -a -x "$SPLITPDB" ]; then
    echo "$APPTAG ERROR: Could not find splitpdb executable at '$SPLITPDB'. Check path and permissions."
    echo "$APPTAG ERROR: Could not find splitpdb executable at '$SPLITPDB'. Check path and permissions." >> $ERRORLOG
    exit 1
fi

SPLITPDBFILE="${PDBFILE}.split"
$SPLITPDB $PDBFILE -o $SPLITPDBFILE $SPLITPDB_OPTIONS
if [ $? -eq 1 ]; then
    echo "$APPTAG ERROR: splitpdb failed (return value 1)."
    echo "$APPTAG ERROR: splitpdb failed (return value 1)." >> $ERRORLOG
    exit 1
fi

rm $PDBFILE && mv $SPLITPDBFILE $PDBFILE
if [ $? -ne 0 ]; then
    echo "$APPTAG ERROR: Could not move split PDB file '$SPLITPDBFILE' to '$PDBFILE'."
    echo "$APPTAG ERROR: Could not move split PDB file '$SPLITPDBFILE' to '$PDBFILE'." >> $ERRORLOG
    exit 1
else
    echo "$APPTAG Deleted original PDB file and moved '$SPLITPDBFILE' to '$PDBFILE'. Running dsspcmbi..."
fi

## ok, run dssp
DSSPFILE="${PDBID}.dssp"
$DSSP $PDBFILE > $DSSPFILE 2>/dev/null
if [ $? -ne 0 ]; then
    echo "$APPTAG ERROR: Running dssp binary '$DSSP' for input file '$PDBFILE' failed."
    echo "$APPTAG ERROR: Running dssp binary '$DSSP' for input file '$PDBFILE' failed." >> $ERRORLOG
    exit 1
fi

if [ ! -r "$DSSPFILE" ]; then
    echo "$APPTAG ERROR: No DSSP file found at '$DSSPFILE' after DSSP run."
    echo "$APPTAG ERROR: No DSSP file found at '$DSSPFILE' after DSSP run." >> $ERRORLOG
    exit 1
fi

## check for broken DSSP files (no data part, i.e., 28 lines of less)
NUM_DSSP_LINES=$(wc -l $DSSPFILE 2>/dev/null | awk '{print $1}')
if [ $? -ne 0 ]; then
    echo "$APPTAG ERROR: Could not determine length of DSSP file."
    echo "$APPTAG ERROR: Could not determine length of DSSP file." >> $ERRORLOG
    exit 1
fi

if [ $NUM_DSSP_LINES -le 28 ]; then
    # The data in DSSP files only starts in line 29, everything before that is the header.
    # If the file has no data section, the PDB file most likely contained no valid resides (e.g., DNA/RNA only).
    echo "$APPTAG ERROR: No data part in DSSP file '$DSSPFILE'."
    echo "$APPTAG ERROR: No data part in DSSP file '$DSSPFILE'." >> $ERRORLOG
fi

## OK, the DSSP file should be ok.
echo "$APPTAG DSSP file created at '$DSSPFILE'."

## We may need to move it though.
if [ -n "$2" ]; then
    DSSPOUT="$2"
    mv $DSSPFILE $DSSPOUT
    if [ $? -ne 0 ]; then
        echo "$APPTAG ERROR: Could not move DSSP file '$DSSPFILE' to '$DSSPOUT'."
	echo "$APPTAG ERROR: Could not move DSSP file '$DSSPFILE' to '$DSSPOUT'." >> $ERRORLOG
        exit 1
    else
	echo "$APPTAG Moved DSSP file to '$DSSPOUT'."
    fi

fi

echo "$APPTAG All done, exiting."
exit 0
















