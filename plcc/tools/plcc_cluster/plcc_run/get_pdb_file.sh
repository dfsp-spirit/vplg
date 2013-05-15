#!/bin/bash
## get_pdb_file.sh -- get and extract a PDB file from the local PDB mirror directory
##
## Written by ts_2011

APPTAG="[GET_PDB]"
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
    echo "$APPTAG USAGE: $0 <PDBID> [<OUTPUT_DIR>]"
    echo "$APPTAG USAGE: $0 <PDBID> [<OUTPUT_DIR>]" >> $ERRORLOG
    echo "$APPTAG example: $0 3kmf /tmp"
    exit 1
fi

PDBID="$1"

## extract the subdir from the pdbid (it is defined by the 2nd and 3rd letter of the id, e.g., for the pdbid '3kmf', it is 'km')
PDB_SUBDIR=${PDBID:1:2}

## e.g. '/srv/www/PDB/data/structures/divided/pdb/km'
PDBPATH="${LOCAL_MIRRORDIR}/${PDB_SUBDIR}"

## e.g. 'pdb3kmf.ent.gz'
PDBFILENAME="${PDBFILE_DL_PREFIX}${PDBID}${PDBFILE_DL_SUFFIX}"

PDBFILEPATH="${PDBPATH}/${PDBFILENAME}"

cp $PDBFILEPATH .
if [ $? -ne 0 ]; then
    echo "$APPTAG ERROR: Could not copy file '${PDBFILEPATH}' to the current directory."
    echo "$APPTAG ERROR: Could not copy file '${PDBFILEPATH}' to the current directory." >> $ERRORLOG
    exit 1
fi

## now that we have the file, extract it. This produces 'pdb3kmf.ent' from 'pdb3kmf.ent.gz'.
PDBFILE_EXTRACTED="pdb${PDBID}.ent"
## delete ent file if it already exists
rm $PDBFILE_EXTRACTED 2>/dev/null
gunzip $PDBFILENAME
if [ $? -ne 0 ]; then
    echo "$APPTAG ERROR: Could not extract file '${PDBFILENAME}' using gunzip."
    echo "$APPTAG ERROR: Could not extract file '${PDBFILENAME}' using gunzip." >> $ERRORLOG
    exit 1
fi

## rename the file

PDBFILE_FINAL="${PDBID}.pdb"
mv $PDBFILE_EXTRACTED $PDBFILE_FINAL
if [ $? -ne 0 ]; then
    echo "$APPTAG ERROR: Could not rename file '${PDBFILE_EXTRACTED}' to '${PDBFILE_FINAL}'."
    echo "$APPTAG ERROR: Could not rename file '${PDBFILE_EXTRACTED}' to '${PDBFILE_FINAL}'." >> $ERRORLOG
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

