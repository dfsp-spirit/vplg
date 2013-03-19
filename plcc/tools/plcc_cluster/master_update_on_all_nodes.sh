#!/bin/bash
## master_update_on_all_nodes.sh -- this script starts or stops the update processes on all worker nodes
##
## Run it only after you updated the PDB and create the host file lists!
##
## If you use this script, DO NOT ssh to each node and DO NOT run the 'run_update_on_this_node_in_screen.sh' on any node
##
## Note that this script assumes you have SSH pub key authentication configured properly for the nodes, i.e., you can ssh to them without
## being askes for a password.


APPTAG="[MASTER] "

NODE_FILE="settings_node_hostnames.cfg"
CFG_FILE="./settings_statistics.cfg"

if [ ! -r "$CFG_FILE" ]; then
  echo "$APPTAG ERROR: Could not read config file at '$CFG_FILE'."
  exit 1
fi

source $CFG_FILE

SSH_USER_FOR_NODES=$(whoami)
echo "$APPTAG Using username '$SSH_USER_FOR_NODES' to connect to nodes via SSH public key authentication."

PLCC_CLUSTER_PATH_ON_NODES="/home/${SSH_USER_FOR_NODES}/software/plcc_cluster"

################# here we go #####################

COMMAND="$1"

if [ "$COMMAND" = "start" -o "$COMMAND" = "cancel" -o "$COMMAND" = "status" ]; then
    echo "$APPTAG Command '$COMMAND' accepted..."
else
    echo "$APPTAG SYNTAX ERROR. USAGE: $0 <COMMAND>"
    echo "$APPTAG Valid commands are:"
    echo "$APPTAG   start           : starts the update in a screen on each node listed in the host file"
    echo "$APPTAG   cancel          : cancels the update, i.e., stop the screen sessions on all nodes"
    exit 1
fi


RUNCOMMAND="ls"


NUM_NODES=$(cat $NODE_FILE | wc -l)

echo "$APPTAG Handling $NUM_NODES nodes..."

for (( BIN_NUM = 1; BIN_NUM <= $NUM_NODES ; BIN_NUM++ ))
do
   HOSTNAME=$(cat $NODE_FILE | awk "NR==$BIN_NUM {print}")

   if [ -z "$HOSTNAME" ]; then
     echo "$APPTAG FATAL: Hostname empty, something went really wrong. Exiting."
     exit 1
   fi
   
   echo "$APPTAG Connecting to host '$HOSTNAME'..."
   SCREEN_SESSION_NAME="plcc_update_${HOSTNAME}"
   
   if [ "$COMMAND" = "start" ]; then
    echo "$APPTAG Starting update on node $HOSTNAME."
    RUNCOMMAND="cd $PLCC_CLUSTER_PATH_ON_NODES && ./run_update_on_this_node_in_screen.sh"
   fi

   if [ "$COMMAND" = "cancel" ]; then
    echo "$APPTAG Canceling update on node $HOSTNAME, killing screen session '$SCREEN_SESSION_NAME'."
    RUNCOMMAND="screen -S $SCREEN_SESSION_NAME -X quit"
   fi

   if [ "$COMMAND" = "status" ]; then
    echo "$APPTAG Getting status of node $HOSTNAME."
    RUNCOMMAND="screen -list"
   fi

   
   ## run commands on host via SSH
   ssh ${SSH_USER_FOR_NODES}@${HOSTNAME} "$RUNCOMMAND"
   #ssh ${SSH_USER_FOR_NODES}@${HOSTNAME} "$RUNCOMMAND" &>master_log.${HOSTNAME}
   #ssh ${SSH_USER_FOR_NODES}@${HOSTNAME} <<'ENDSSH'
   #$RUNCOMMAND
   #ENDSSH
done

echo "$APPTAG Done for all hosts, exiting."
exit 0

