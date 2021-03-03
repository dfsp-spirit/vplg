#!/bin/sh
## svn_up.sh -- update a subversion repository and show the log messages of all commits which have been added since the last svn up command
## Note: This only works if 'svn up' will run without user input (i.e., you have the password stored) and it does no error checking
## written by ts

APPTAG="[SVNUP]"

echo "$APPTAG Updating repo in current directory and checking for logs..."

LASTREV=$(svn info | grep 'Revision' | awk '{print $2}')

svn up

CURREV=$(svn info | grep 'Revision' | awk '{print $2}')

echo "$APPTAG Last revision was $LASTREV, now at $CURREV."

if [ $LASTREV -eq $CURREV ]; then
    echo "$APPTAG No changes, exiting."
    exit 1
else
    echo "$APPTAG Showing logs:"
fi

svn log -r $LASTREV:$CURREV | grep -B 1 '^----' | grep -v '^--'


echo "$APPTAG Done, exiting."
exit 1

# EOF


