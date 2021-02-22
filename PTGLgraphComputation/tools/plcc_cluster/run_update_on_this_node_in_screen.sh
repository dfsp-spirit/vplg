#!/bin/bash
## Run this script on every node once when the host filelists are ready. (Just type './run_update_on_this_node_in_screen.sh').

APPTAG="[SCREEN_UPD_NODE]"

HOSTNAME=$(hostname)
FILELISTPATH=$(cat filelist.${HOSTNAME})

if [ ! -f $FILELISTPATH ]; then
  echo "$APPTAG ERROR: No filelist found at '$FILELISTPATH', exiting."
  exit 1
fi

SCREEN_SESSION_NAME="plcc_update_${HOSTNAME}"

echo "$APPTAG Starting update on node '$HOSTNAME' in screen session '$SCREEN_SESSION_NAME', using filelist at '$FILELISTPATH'..."

screen -dmS "$SCREEN_SESSION_NAME" ./update_db_from_hostfilelist_on_this_node.sh $FILELISTPATH all

echo "$APPTAG Screen session started. Check it using 'screen -list'. You can now logout."


