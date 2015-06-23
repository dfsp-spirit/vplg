#!/bin/sh
## visualize graph similartiy
FIRST_GRAPH="$1"
SECOND_GRAPH="$2"


./bk_protsim "$FIRST_GRAPH" "$SECOND_GRAPH" -l -f
java -jar plcc.jar NONE --draw-gml-graph "$FIRST_GRAPH" results_0_first.txt
java -jar plcc.jar NONE --draw-gml-graph "$SECOND_GRAPH" results_0_second.txt

echo "Done. Check files '$FIRST_GRAPH.png' and '$SECOND_GRAPH.png'."