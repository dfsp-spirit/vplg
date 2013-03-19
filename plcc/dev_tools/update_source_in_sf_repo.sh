#!/bin/sh
# update source in sf repo -- updates the VPLG source code in the public souceforge repo from the code in this private repo
# written by TS, AUG 2012

## OS stuff: some fixes for cygwin

APPTAG="[UR]"

UHOME="$HOME"
if [ "$OS" = "Windows_NT" ]; then
   echo "$APPTAG OS Cygwin/Windows detected, overriding userhome."
   UHOME="/cygdrive/C/Users/spirit"
   echo "$APPTAG OS Cygwin/Windows detected, overriding userhome to $UHOME."
fi

## Settings
TMP_EXPORT_DIR="$UHOME/tmp_svn_export"
SF_REPO_DIR="$UHOME/develop/vplg-sf"
REPO_DIR_PLCC="$UHOME/develop/vplg/plcc"
REPO_DIR_SPLITPDB="$UHOME/develop/vplg/splitpdb"
REPO_DIR_VPG="$UHOME/develop/vplg/vpg"


if [ "$OS" = "Windows_NT" ]; then
   TMP_EXPORT_DIR="C:\Users\spirit\tmp_svn_export"
   echo "$APPTAG OS Cygwin/Windows detected, overriding TMP_EXPORT_DIR to $TMP_EXPORT_DIR."
fi

echo "$APPTAG === Updating VPLG source in public sourceforge repo, using target directory '$SF_REPO_DIR'. ==="
echo "$APPTAG Assuming the PLCC SVN repo is at '$REPO_DIR_PLCC'"
echo "$APPTAG Assuming the SPLITPDB SVN repo is at '$REPO_DIR_SPLITPDB'"
echo "$APPTAG Assuming the VPG SVN repo is at '$REPO_DIR_VPG'"
echo "$APPTAG Assuming the temporary SVN export dir should be created at '$TMP_EXPORT_DIR'"

## Check and prepare stuff
if [ -d $TMP_EXPORT_DIR ]; then
	echo "$APPTAG ERROR: temporary SVN export dir exists, won't overwrite it. Exiting.";
	exit 1;
fi

mkdir -p $TMP_EXPORT_DIR

CUR_DIR=`pwd`


## copy scripts
echo "$APPTAG Copying scripts..."
cp $REPO_DIR_PLCC/tools/rpg.pl $SF_REPO_DIR/plcc/tools/

## copy documentation
echo "$APPTAG Copying documentation..."
cp $REPO_DIR_PLCC/doc/README_plcc $SF_REPO_DIR/plcc/doc/
cp $REPO_DIR_PLCC/doc/README_rpg $SF_REPO_DIR/plcc/doc/
cp $REPO_DIR_PLCC/doc/HOWTO_BUILD_FROM_SOURCE $SF_REPO_DIR/plcc/doc/
cp $REPO_DIR_PLCC/doc/HELP_AND_FAQ $SF_REPO_DIR/plcc/doc/
cp $REPO_DIR_PLCC/doc/README_vplg $SF_REPO_DIR/plcc/doc/
cp $REPO_DIR_PLCC/doc/LICENSE $SF_REPO_DIR/plcc/doc/
cp $REPO_DIR_PLCC/doc/INSTALL $SF_REPO_DIR/plcc/doc/
cp $REPO_DIR_PLCC/doc/RELEASE_LOG $SF_REPO_DIR/plcc/doc/
cp $REPO_DIR_PLCC/doc/plcc_settings.txt $SF_REPO_DIR/plcc/doc
cp $REPO_DIR_PLCC/doc/vplg_explanation_short.pdf $SF_REPO_DIR/plcc/doc
cp $REPO_DIR_SPLITPDB/doc/README_splitpdb $SF_REPO_DIR/splitpdb/doc/
cp $REPO_DIR_VPG/doc/README_vpg $SF_REPO_DIR/vpg/doc/




## export source code from svn repo
echo "$APPTAG Exporting and copying source code..."

echo "$APPTAG PLCC:"
cd $REPO_DIR_PLCC && svn export src/ $TMP_EXPORT_DIR/plcc-src/ && cd $TMP_EXPORT_DIR/plcc-src/ && cp -r * $SF_REPO_DIR/plcc/src/ && cd $CUR_DIR

echo "$APPTAG SplitPDB:"
cd $REPO_DIR_SPLITPDB && svn export src/ $TMP_EXPORT_DIR/splitpdb-src/ && cd $TMP_EXPORT_DIR/splitpdb-src/ && cp -r * $SF_REPO_DIR/splitpdb/src/ && cd $CUR_DIR

echo "$APPTAG VPG:"
cd $REPO_DIR_VPG && svn export src/ $TMP_EXPORT_DIR/vpg-src/ && cd $TMP_EXPORT_DIR/vpg-src/ && cp -r * $SF_REPO_DIR/vpg/src/ && cd $CUR_DIR


## Done
echo "$APPTAG Done. Check output above for errors, I don't care about them."
echo "$APPTAG Don't forget to remove '$TMP_EXPORT_DIR' or the next run will fail because it exists."
echo "$APPTAG The public sourceforge repo at '$SF_REPO_DIR' should be updated, you can now 'svn commit' in there."
exit 0







