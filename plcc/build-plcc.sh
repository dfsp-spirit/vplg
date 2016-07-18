#!/bin/sh
APPTAG="[BPLCC]"
echo "$APPTAG Remember that you need JUnit for this to work. Running 'ant ci'..."
ant ci
echo "$APPTAG If no error showed up, the binary should be in 'store/plcc.jar'."

