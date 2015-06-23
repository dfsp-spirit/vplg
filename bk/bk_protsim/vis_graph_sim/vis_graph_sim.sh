#!/bin/sh
## visualize graph similartiy
FIRST_GRAPH="$1"
SECOND_GRAPH="$2"


BK="./bk_protsim"
PLCC="plcc.jar"

APPTAG="VGS "

if [ ! -f "$BK" ]; then
  echo "$APPTAG Missing bk_protsim binary"
  exit 1
fi

if [ ! -f "$PLCC" ]; then
  echo "$APPTAG Missing bk_protsim binary"
  exit 1
fi

$BK "$FIRST_GRAPH" "$SECOND_GRAPH" -l -f
java -jar $PLCC NONE --draw-gml-graph "$FIRST_GRAPH" results_0_first.txt
java -jar $PLCC NONE --draw-gml-graph "$SECOND_GRAPH" results_0_second.txt

echo "Done. Check files '$FIRST_GRAPH.png' and '$SECOND_GRAPH.png'."