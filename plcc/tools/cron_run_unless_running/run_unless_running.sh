#!/bin/sh
### run_unless_running.sh -- Sync the contents of two directories via rsync unless the old cron rsync process still runs ###
### by Tim Schaefer
### This script is to be run by cron while the PTGL update is in progress. ###
### Add a cronjob by running 'crontab -e', then edit the file. An example is given below. Be sure to adapt the time to your command. ###
### Example crontab line to run this every 3 hours: 
###
###      '0 */3 * * * /path/to/run_unless_running.sh'
###
### Make sure to edit the settings below to fill in your command. And make sure that cron is running, of course. ;) That's it.
###
### With the current command setup, this should be run on the PTGL3 server to pull the output of the PTGL3 update from the cluster.
### Note that you will have to configure password-less authentication via ssh (using SSH-keys) for the rsync command to work.


# some commands we need
KILL="/bin/kill"
ECHO="/bin/echo"
RM="/bin/rm"
DATE="/bin/date"
CAT="/bin/cat"

DATESTR=$($DATE)
APPTAG="[run_unless_running]"
$ECHO "$APPTAG Checking whether to run commands. Current time is $DATESTR."

########## settings -- edit these ##############

# The process ID file. You only need to change this if you run this script in parallel for different commands on the same server.
PIDFILE=/var/run/rur_vplg-copy-results.pid

# The command that should be executed (unless it still runs). This most likely takes some time.
#COMMAND="/usr/bin/rsync -rh /tmp/results/ /srv/www/htdocs/myapp/data/results/"

### Note that you will have to configure password-less authentication via ssh (using SSH-keys) for the rsync command to work.
COMMAND="/usr/bin/rsync -rhe ssh ts@odysseus.bioinformatik.uni-frankfurt.de:/shares/modshare/vplg_all_nodes_output/ /srv/www/htdocs/ptgl3"


########## end of settings -- no need to edit below this line ##########

DATESTR=$($DATE)

if [ -e "$PIDFILE" ] ; then
    # our pidfile exists, let's make sure the process is still running though
    PID=`$CAT "$PIDFILE"`
    if $KILL -0 "$PID" > /dev/null 2>&1 ; then
        $ECHO "$APPTAG The command is still running, skipping this run at $DATESTR."
        exit 0
    fi
 fi

# create or update the pidfile
$ECHO "$$" > $PIDFILE

# run the command we came for
$ECHO "$APPTAG The command is not running anymore, starting new instance at $DATESTR."
$($COMMAND)

# remove PID file
$RM -f "$PIDFILE"

$ECHO "$APPTAG Exiting."