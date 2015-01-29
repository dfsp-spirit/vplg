#!/bin/sh

echo "Running all query scripts, this may take a sec depending on database size..."

for SCRIPT in q_overview.sh q_sselengths.sh q_functions.sh q_contacts.sh q_contact_details.sh q_organisms.sh q_graphtypes.sh q_motifs.sh
do
	echo " *Handling script '$SCRIPT'..."
	SCRIPT_NO_EXT=${SCRIPT%.*}
	RESULT_FILE="./results/result_${SCRIPT_NO_EXT}.txt"
	./$SCRIPT > $RESULT_FILE
	echo "  Results of script '$SCRIPT' written to '$RESULT_FILE'."
done


echo "All done."
exit 0

