#!/bin/sh
## fill_database.sh -- fill the PTGLtools database from the PDB and DSSP files in this directory
## This script assumes that PTGLgraphComputation.jar and some PDB/DSSP files are in this directory. It also assumes the DB is setup correctly.
## NOTE: Using coils is broken (in various ways).


## settings ###

RUN_GRAPHLETANALYSER="YES"
RUN_GRAPHLETANALYSER_ALSO_FOR_COMPLEXGRAPHS="YES"
RUN_GRAPHLETANALYSER_ALSO_FOR_AMINOACIDGRAPHS="YES"


#PTGLgraphComputation_OPTIONS="-f -u -k -s --complex-graphs --aa-graphs"
PTGLgraphComputation_OPTIONS="--cluster  --compute-graph-metrics --silent"
PTGLgraphComputation_RUNS_IN_SUBDIR_TREE_MODE="YES"
## IMPORTANT: set this to "YES" if PTGLgraphComputation is run with '-k' / '--output-subdir-tree' or other options which include it (e.g., --cluster)

DELETE_CLUSTER_CHAINS_FILE="NO"
SILENT="YES"

### end of settings ###

APPTAG="[FDB]"
NODSSP_LIST="dssp_files_missing.lst"

PTGLgraphComputation_OUTPUT_DIR="."

## cannot run GA under windows/cygwin
if [ "$OSTYPE" = "cygwin" -a "$RUN_GRAPHLETANALYSER" = "YES" ]; then
  echo "$APPTAG WARNING: Cygwin/Windows OS detected, not running graphlet analyser."
  RUN_GRAPHLETANALYSER="NO"
fi

if [ -f $NODSSP_LIST ]; then
   rm $NODSSP_LIST
   touch $NODSSP_LIST
fi

if [ "$SILENT" = "NO" ]; then
  echo "$APPTAG Adding all PDB files in this directory to the PTGLtools database... (assuming they are named '<pdbid>.pdb')"
fi

for PDBFILE in *.pdb;
do
        if [ "$SILENT" = "NO" ]; then
	  echo "$APPTAG Handling PDB file '$PDBFILE'"
	fi
	fullfilename=$(basename "$PDBFILE")
        extension=${fullfilename##*.}
	filename=${fullfilename%.*}

	PDBID="$filename"
	PDB_STRING_LENGTH=${#PDBID}
	if [ $PDB_STRING_LENGTH -ne 4 ]; then
	  echo "$APPTAG: ERROR: Expected PDB ID of length 4 but found '$filename', the input file may not be named after the pattern '<pdbid>.pdb'. Skipping file '$fullfilename'."
	  continue
	fi
	
	DSSPFILE="$filename.dssp"
	if [ ! -r $DSSPFILE ]; then
	   echo "$filename" >> $NODSSP_LIST
	else
	    java -Xmx4096M -jar PTGLgraphComputation.jar $filename $PTGLgraphComputation_OPTIONS	    
	fi
	
	if [ "$RUN_GRAPHLETANALYSER" = "YES" ]; then
	
	        if [ "$SILENT" = "NO" ]; then
                  echo "$APPTAG Running Graphletanalyser to compute graphlets for all chains of the PDB file."
                fi
                
                ## extract the subdir from the pdbid (it is defined by the 2nd and 3rd letter of the id, e.g., for the pdbid '3kmf', it is 'km')
		#echo "ID=$PDBID"
                #MID2PDBCHARS=${PDBID:1:2}
		MID2PDBCHARS=$(echo $PDBID | cut -c 2-3)
                
		CHAINS_FILE="${PTGLgraphComputation_OUTPUT_DIR}/${PDBID}.chains"
		
		if [ ! -f "$CHAINS_FILE" ]; then
		       echo "$APPTAG ##### ERROR: No chains file found at '$CHAINS_FILE', is '--cluster' set as PTGLgraphComputation command line option?"
	        else
	                if [ "$SILENT" = "NO" ]; then
	                  echo "$APPTAG Chains file found at '$CHAINS_FILE'."
	                fi
	                
	                ## run GA for the protein graphs of all chains
			for CHAIN in $(cat ${CHAINS_FILE});
			do
			        if [ "$PTGLgraphComputation_RUNS_IN_SUBDIR_TREE_MODE" = "YES" ]; then			            
			            ALBE_GML_GRAPHFILE="${PTGLgraphComputation_OUTPUT_DIR}/${MID2PDBCHARS}/${PDBID}/${CHAIN}/${PDBID}_${CHAIN}_albe_PG.gml"
			        else
			            ALBE_GML_GRAPHFILE="${PTGLgraphComputation_OUTPUT_DIR}/${PDBID}_${CHAIN}_albe_PG.gml"
			        fi

				if [ -f "$ALBE_GML_GRAPHFILE" ]; then
				        if [ "$SILENT" = "NO" ]; then
				          echo "$APPTAG Running Graphletanalyser on file '$ALBE_GML_GRAPHFILE' for albe protein graph of $PDBID chain '$CHAIN'."
				        fi
					./graphletanalyser --silent --useDatabase --sse_graph $ALBE_GML_GRAPHFILE
				else
					echo "$APPTAG ##### ERROR: The albe GML protein graph file was not found at '$ALBE_GML_GRAPHFILE', cannot run graphletanalyser on it."
				fi
			done

			## now also compute the graphlet counts for CGs
			if [ "$RUN_GRAPHLETANALYSER_ALSO_FOR_COMPLEXGRAPHS" = "YES" ]; then
			    if [ "$PTGLgraphComputation_RUNS_IN_SUBDIR_TREE_MODE" = "YES" ]; then
				ALBELIG_GML_COMPLEXGRAPHFILE="${PTGLgraphComputation_OUTPUT_DIR}/${MID2PDBCHARS}/${PDBID}/ALL/${PDBID}_complex_sses_albelig_CG.gml"
			    else
				ALBELIG_GML_COMPLEXGRAPHFILE="${PTGLgraphComputation_OUTPUT_DIR}/${PDBID}_aagraph.gml"
			    fi
			
			    if [ -f "$ALBELIG_GML_COMPLEXGRAPHFILE" ]; then
				    if [ "$SILENT" = "NO" ]; then
				      echo "$APPTAG Running Graphletanalyser on file '$ALBELIG_GML_COMPLEXGRAPHFILE' for albelig complex graph of PDB $PDBID."
				    fi
				    ./graphletanalyser --silent --useDatabase --complex_graph $ALBELIG_GML_COMPLEXGRAPHFILE
			    else
				    echo "$APPTAG ##### ERROR: The albelig GML complex graph file was not found at '$ALBELIG_GML_COMPLEXGRAPHFILE', cannot run graphletanalyser on it."
			    fi
			fi
			
			## now also compute the graphlet counts for AAGs
			if [ "$RUN_GRAPHLETANALYSER_ALSO_FOR_AMINOACIDGRAPHS" = "YES" ]; then
			    if [ "$PTGLgraphComputation_RUNS_IN_SUBDIR_TREE_MODE" = "YES" ]; then
				ALBE_GML_AMINOACIDGRAPHFILE="${PTGLgraphComputation_OUTPUT_DIR}/${MID2PDBCHARS}/${PDBID}/ALL/${PDBID}_aagraph.gml"
			    else
				ALBE_GML_AMINOACIDGRAPHFILE="${PTGLgraphComputation_OUTPUT_DIR}/${PDBID}_aagraph.gml"
			    fi
			
			    if [ -f "$ALBE_GML_AMINOACIDGRAPHFILE" ]; then
				    if [ "$SILENT" = "NO" ]; then
				      echo "$APPTAG Running Graphletanalyser on file '$ALBE_GML_AMINOACIDGRAPHFILE' for albe amino acid graph of PDB $PDBID."
				    fi
				    ./graphletanalyser --silent --useDatabase --aa_graph $ALBE_GML_AMINOACIDGRAPHFILE
			    else
				    echo "$APPTAG ##### ERROR: The albe GML amino acid graph file was not found at '$ALBE_GML_AMINOACIDGRAPHFILE', cannot run graphletanalyser on it."
			    fi
			fi
		      
			

			## delete chains file
			if [ "$DELETE_CLUSTER_CHAINS_FILE" = "YES" ]; then
				rm "$CHAINS_FILE"
			fi
		fi
	    fi
done

if [ "$SILENT" = "NO" ]; then
  echo "$APPTAG All done, exiting."
fi
exit 0

# EOF
