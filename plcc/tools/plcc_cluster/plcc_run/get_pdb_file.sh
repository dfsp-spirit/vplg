#!/bin/bash
## get_pdb_file.sh -- get and extract a PDB file from the local PDB mirror directory
##
## Written by ts_2011

APPTAG="[GET_PDB]"
CFG_FILE="../settings_statistics.cfg"
ERRORLOG="/dev/stderr"		# may get reset later when PDB ID is known

## get settings

source $CFG_FILE
## make sure the settings have been read
if [ -z "$SETTINGSREAD" ]; then
    echo "$APPTAG ##### ERROR: Could not load settings from file '$CFG_FILE'. #####"
    echo "$APPTAG ##### ERROR: Could not load settings from file '$CFG_FILE'. #####" >> $ERRORLOG
    exit 1
fi

if [ -z "$1" ]; then
    echo "$APPTAG USAGE: $0 <PDBID> [<OUTPUT_DIR>]"
    echo "$APPTAG USAGE: $0 <PDBID> [<OUTPUT_DIR>]" >> $ERRORLOG
    echo "$APPTAG example: $0 3kmf /tmp"
    exit 1
fi

PDBID="$1"

## debug
ERRORLOG="/develop/tmp/get_pdb_${PDBID}.log"

## extract the subdir from the pdbid (it is defined by the 2nd and 3rd letter of the id, e.g., for the pdbid '3kmf', it is 'km')
PDB_SUBDIR=${PDBID:1:2}

## e.g. '/srv/www/PDB/data/structures/divided/pdb/km'
PDBPATH="${LOCAL_MIRRORDIR}/${PDB_SUBDIR}"

## e.g. 'pdb3kmf.ent.gz'
PDBFILENAME="${PDBFILE_DL_PREFIX}${PDBID}${PDBFILE_DL_SUFFIX}"

PDBFILEPATH="${PDBPATH}/${PDBFILENAME}"

cp $PDBFILEPATH .
if [ $? -ne 0 ]; then
    echo "$APPTAG ERROR: Could not copy file '${PDBFILEPATH}' to the current directory '`pwd`'."
    echo "$APPTAG ERROR: Could not copy file '${PDBFILEPATH}' to the current directory '`pwd`'." >> $ERRORLOG
    exit 1
fi

## check it
if [ ! -f $PDBFILENAME ]; then
    echo "$APPTAG ERROR: Expected packed raw PDB file '${PDBFILENAME}' does not exist after copying to local dir '`pwd`'." >> $ERRORLOG
    exit 1
fi


## now that we have the file, extract it. This produces 'pdb3kmf.ent' from 'pdb3kmf.ent.gz'.
PDBFILE_EXTRACTED="pdb${PDBID}.ent"

## delete ent file if it already exists
if [ -f $PDBFILE_EXTRACTED ]; then
    echo "$APPTAG NOTE: Extracted PDB file '${PDBFILE_EXTRACTED}' already exists, deleting old version."
    rm -f $PDBFILE_EXTRACTED
fi

gunzip $PDBFILENAME
if [ $? -ne 0 ]; then
    echo "$APPTAG ERROR: Could not extract file '${PDBFILENAME}' using gunzip."
    echo "$APPTAG ERROR: Could not extract file '${PDBFILENAME}' using gunzip." >> $ERRORLOG
    rm -f $PDBFILENAME
    exit 1
fi

## rename the file

PDBFILE_FINAL="${PDBID}.pdb"

if [ ! -f $PDBFILE_EXTRACTED ]; then
    echo "$APPTAG ERROR: Expected extracted PDB file '${PDBFILE_EXTRACTED}' does not exist." >> $ERRORLOG
    exit 1
fi

mv $PDBFILE_EXTRACTED $PDBFILE_FINAL
if [ $? -ne 0 ]; then
    echo "$APPTAG ERROR: Could not rename file '${PDBFILE_EXTRACTED}' to '${PDBFILE_FINAL}'."
    echo "$APPTAG ERROR: Could not rename file '${PDBFILE_EXTRACTED}' to '${PDBFILE_FINAL}'." >> $ERRORLOG
    rm -f $PDBFILE_EXTRACTED
    exit 1
fi

if [ ! -f $PDBFILE_FINAL ]; then
    echo "$APPTAG ERROR: Expected final PDB file '${PDBFILE_FINAL}' does not exist." >> $ERRORLOG
    rm -f $PDBFILE_EXTRACTED
    exit 1
fi

echo "$APPTAG PDB file retrieved, extracted and written to '${PDBFILE_FINAL}'."

## we may need to move the file
if [ -n "$2" ]; then
    TARGET_DIR="$2"
    mv $PDBFILE_FINAL $TARGET_DIR
    if [ $? -ne 0 ]; then
        echo "$APPTAG ERROR: Could not move to '${PDBFILE_FINAL}' to '${TARGET_DIR}'."
	echo "$APPTAG ERROR: Could not move to '${PDBFILE_FINAL}' to '${TARGET_DIR}'." >> $ERRORLOG
        exit 1
    else
        echo "$APPTAG Moved '${PDBFILE_FINAL}' to '${TARGET_DIR}'."
    fi
fi

echo "$APPTAG All done, exiting."
exit 0

