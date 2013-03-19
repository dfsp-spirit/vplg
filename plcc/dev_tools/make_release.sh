#!/bin/sh
# make-release -- prepares a VPLG release from the files in the SVN repo
# written by TS, JAN 2012

## OS stuff: some fixes for cygwin

APPTAG="[MR]"
UHOME="$HOME"
if [ "$OS" = "Windows_NT" ]; then
   echo "$APPTAG OS Cygwin/Windows detected, overriding userhome."
   UHOME="/cygdrive/C/Users/spirit"
   echo "$APPTAG OS Cygwin/Windows detected, overriding userhome to $UHOME."
fi

## Settings
RELEASE_DIR="$UHOME/vplg_release"
TMP_EXPORT_DIR="$UHOME/tmp_svn_export"
REPO_DIR_PLCC="$UHOME/develop/vplg/plcc"
REPO_DIR_SPLITPDB="$UHOME/develop/vplg/splitpdb"
REPO_DIR_VPG="$UHOME/develop/vplg/vpg"


if [ "$OS" = "Windows_NT" ]; then
   TMP_EXPORT_DIR="C:\Users\spirit\tmp_svn_export"
   echo "$APPTAG OS Cygwin/Windows detected, overriding TMP_EXPORT_DIR to $TMP_EXPORT_DIR."
fi

echo "$APPTAG === Preparing VPLG release, using target directory '$RELEASE_DIR'. ==="
echo "$APPTAG Assuming the PLCC SVN repo is at '$REPO_DIR_PLCC'"
echo "$APPTAG Assuming the SPLITPDB SVN repo is at '$REPO_DIR_SPLITPDB'"
echo "$APPTAG Assuming the VPG SVN repo is at '$REPO_DIR_VPG'"
echo "$APPTAG Assuming the temporary SVN export dir should be created at '$TMP_EXPORT_DIR'"

## Check and prepare stuff
if [ -d $RELEASE_DIR ]; then
	echo "$APPTAG ERROR: Release dir exists, won't overwrite it. Exiting.";
	exit 1;
fi

mkdir -p $RELEASE_DIR/vplg/
mkdir -p $RELEASE_DIR/vplg/lib/
mkdir -p $RELEASE_DIR/vplg/doc/
mkdir -p $RELEASE_DIR/vplg/src/
mkdir -p $RELEASE_DIR/vplg/example_data/input
mkdir -p $RELEASE_DIR/vplg/example_data/plccopt
mkdir -p $TMP_EXPORT_DIR

CUR_DIR=`pwd`


## Copy the files 1: binaries
echo "$APPTAG Copying binaries and scripts..."
cp $REPO_DIR_PLCC/dist/plcc.jar $RELEASE_DIR/vplg/
cp $REPO_DIR_PLCC/testrun/plcc $RELEASE_DIR/vplg/

cp $REPO_DIR_SPLITPDB/dist/splitpdb.jar $RELEASE_DIR/vplg/
cp $REPO_DIR_SPLITPDB/testrun/splitpdb $RELEASE_DIR/vplg/

cp $REPO_DIR_VPG/dist/vpg.jar $RELEASE_DIR/vplg/
cp $REPO_DIR_VPG/testrun/vpg $RELEASE_DIR/vplg/

cp $REPO_DIR_PLCC/tools/rpg.pl $RELEASE_DIR/vplg/

## 2: libraries
echo "$APPTAG Copying libraries..."
cp $REPO_DIR_PLCC/lib/*.jar $RELEASE_DIR/vplg/lib/
#cp $REPO_DIR_SPLITPDB/lib/*.jar $RELEASE_DIR/vplg/lib/
#cp $REPO_DIR_VPG/lib/*.jar $RELEASE_DIR/vplg/lib/

## 3: documentation
echo "$APPTAG Copying documentation..."
cp $REPO_DIR_PLCC/doc/README_plcc $RELEASE_DIR/vplg/doc/
cp $REPO_DIR_PLCC/doc/README_rpg $RELEASE_DIR/vplg/doc/
cp $REPO_DIR_PLCC/doc/HOWTO_BUILD_FROM_SOURCE $RELEASE_DIR/vplg/doc/
cp $REPO_DIR_PLCC/doc/HELP_AND_FAQ $RELEASE_DIR/vplg/doc/
cp $REPO_DIR_PLCC/doc/README_vplg $RELEASE_DIR/vplg/doc/
cp $REPO_DIR_PLCC/doc/LICENSE $RELEASE_DIR/vplg/doc/
cp $REPO_DIR_PLCC/doc/INSTALL $RELEASE_DIR/vplg/doc/
cp $REPO_DIR_PLCC/doc/RELEASE_LOG $RELEASE_DIR/vplg/doc/
cp $REPO_DIR_PLCC/doc/plcc_settings.txt $RELEASE_DIR/vplg/doc/
cp $REPO_DIR_PLCC/doc/vplg_explanation_short.pdf $RELEASE_DIR/vplg/doc/
cp $REPO_DIR_SPLITPDB/doc/README_splitpdb $RELEASE_DIR/vplg/doc/
cp $REPO_DIR_VPG/doc/README_vpg $RELEASE_DIR/vplg/doc/

## 4: source code
echo "$APPTAG Copying source code..."
cd $REPO_DIR_PLCC && svn export src/ $TMP_EXPORT_DIR/plcc-src/ && cd $TMP_EXPORT_DIR && zip -r plcc-src.zip plcc-src/ && mv plcc-src.zip $RELEASE_DIR/vplg/src/ && cd $CUR_DIR
cd $REPO_DIR_SPLITPDB && svn export src/ $TMP_EXPORT_DIR/splitpdb-src/ && cd $TMP_EXPORT_DIR && zip -r splitpdb-src.zip splitpdb-src/ && mv splitpdb-src.zip $RELEASE_DIR/vplg/src/ && cd $CUR_DIR
cd $REPO_DIR_VPG && svn export src/ $TMP_EXPORT_DIR/vpg-src/ && cd $TMP_EXPORT_DIR && zip -r vpg-src.zip vpg-src/ && mv vpg-src.zip $RELEASE_DIR/vplg/src/ && cd $CUR_DIR

## 5: example data
echo "$APPTAG Copying example data..."
cp $REPO_DIR_PLCC/data/input/*.dssp $RELEASE_DIR/vplg/example_data/input/
cp $REPO_DIR_PLCC/data/input/*.pdb $RELEASE_DIR/vplg/example_data/input/
cp $REPO_DIR_PLCC/data/input/test* $RELEASE_DIR/vplg/example_data/input/
cp $REPO_DIR_PLCC/data/plccopt/*.plccopt $RELEASE_DIR/vplg/example_data/plccopt/

## Done
echo "$APPTAG Done. Check output above for errors, I don't care about them."
echo "$APPTAG Don't forget to remove '$RELEASE_DIR' and '$TMP_EXPORT_DIR' or the next run will fail because they exist."
echo "$APPTAG Your release directory is at '$RELEASE_DIR'."
exit 0







