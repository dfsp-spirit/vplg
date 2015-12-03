#!/usr/bin/perl
## rpg.pl -- Random Protein Graph -- generate random protein graphs in the plg format of plcc
## 
## This program is part of plcc. Written by Tim Schaefer, http://rcmd.org.
## See the plcc license and README or website for more info.
##

use strict;
use Getopt::Std;

## ----------------------------------------------------------------------
## --------- Settings, feel free to adapt these to your needs -----------
## ----------------------------------------------------------------------

my $num_vertices = 150;
my $edge_probability = 0.1;
my $outfile = "random_graph.plg";
my $format = "plcc";

## ----------------------------------------------------------------------
## ------------ Internal global vars, don't mess with these -------------
## ----------------------------------------------------------------------

my $apptag = "[RPG]";


## ----------------------------------------------------------------------
## ----------------------------- Functions ------------------------------
## ----------------------------------------------------------------------


## prints usage info
sub usage {
    print "$apptag   USAGE   : rpg.pl [options]\n";
    print "$apptag    Valid options are:\n";
    print "$apptag     -h                    : Print this help message and exit.\n";
    print "$apptag     -v <num_vertices>     : Number of vertices the graph should have (positive integer). Default=100.\n";
    print "$apptag     -p <edge_probability> : Probability that an edge is created between a pair of vertices (positive float between 0 and 1). Default=0.1.\n";
    print "$apptag     -o <outfile>          : Path where to write the output file. Default='random_graph.dpg'.\n";
	print "$apptag     -f <format>           : The output graph format. Options include 'gml' and 'plcc'. Default='plcc'.\n";
    print "$apptag   EXAMPLE : rpg.pl -v 100 -p 0.1 -o random_graph_100_01.plg\n";
    exit(1);
}


## Returns a random SSE type. Ligands are less likely than helices and beta strands.
sub get_random_sse_type {
    my $rnd = rand(3);		# random float in range 0..3
    if($rnd >= 0 and $rnd < 1.4) {
	return("H");	    
    }
    elsif($rnd >= 1.4 and $rnd < 2.8) {
	return("E");
    }
    else {
	return("L");
    }    
}


## Returns a random contact type that is valid for the given SSE types
sub get_random_contact_type {
    my $sse1_type = uc $_[0];
    my $sse2_type = uc $_[1];
    
    if($sse1_type eq "L" || $sse2_type eq "L") {
	return("l");
    }
    else {
	my $rnd = rand(3);	# random float in range 0..3
	if($rnd >= 0 and $rnd < 1) {
	    return("p");	    
	}
	elsif($rnd >= 1 and $rnd < 2) {
	    return("a");
	}
	else {
	    return("m");
	}	
    }       
}


## Get a fake PDB residue identifier (<chain>-<residue_number>-<insertion_code>) from a DSSP residue number
sub get_pdb_identifier_from_dssp_residue {
    my $dssp_res = $_[0];
    my $chain_id = $_[1];
    my $icode = " ";
    
    return($chain_id . "-" . $dssp_res . "-" . $icode);
}


## Checks whether we are lucky and an edge should be created. Yes, we like edges! :)
sub we_are_lucky {
    
    my $rnd = rand();		# random float in range 0..1
    
    if($rnd > (1.0 - $edge_probability)) {
	return(1);
    }
    else {
	return(0);
    }
}

sub get_footer_string {
    my $format = $_[0];
	my $pdbid = $_[1];
	my $chain = $_[2];
	my $graphtype = $_[3];
	
	my $footer = "";
	
	if($format == "gml") {
	    $footer .= "]\n";
	}
	elsif ($format == "plcc") {
	    $footer .= "";
	}
	else {
	  die("get_footer_string: invalid output format\n");
	}
	return $footer;
}

sub get_vertex_sting {
    my $format = $_[0];
	my $pdbid = $_[1];
	my $chain = $_[2];
	my $graphtype = $_[3];
	my $sse_id = $_[4];
	my $sse_seq_num = $_[5];
	my $sse_type = $_[6];
	my $dssp_start = $_[7];
	my $dssp_end = $_[8];
	my $pdb_start = $_[9];
	my $pdb_end = $_[10];
	my $sequence = $_[11];
   
    my $vertex = "";
	
	if($format == "gml") {
	    $vertex = "  node [\n";
		$vertex = "    id $sse_id\n";
        $vertex = "    label \"$sse_id-$sse_type\"\n";
        $vertex = "    num_in_chain $sse_seq_num\n";
        $vertex = "    num_residues 5\n";
        $vertex = "    pdb_res_start \"$pdb_start\"\n";
        $vertex = "    pdb_res_end \"$pdb_end\"\n";
        $vertex = "    dssp_res_start $dssp_start\n";
        $vertex = "    dssp_res_end $dssp_end\n";
        $vertex = "    pdb_residues_full \"A-15- ,A-16- ,A-17- \"\n";
        $vertex = "    aa_sequence \"$sequence\"\n";
        $vertex = "    sse_type \"$sse_type\"\n";
		$vertex = "  ]\n";
    }	
	elsif ($format == "plcc") {
	    $vertex .= "| $pdbid | $chain | $graphtype | $sse_id | $sse_seq_num | $sse_type | $dssp_start | $dssp_end | $pdb_start | $pdb_end | $sequence\n";
	} 
	else {
	    die("get_vertex_string: invalid output format\n");
	}
	return $vertex;
}


sub get_edge_string {
    my $format = $_[0];
	my $source = $_[1];
	my $target = $_[2];
	my $spatial = $_[2];
	
	my $edge = "";	
	if($format == "gml") {
	    $edge .= "  edge [\n";
        $edge .= "    source $source\n";
        $edge .= "    target $target\n";
        $edge .= "    label \"$spatial\"\n";
        $edge .= "    spatial \"$spatial\"\n";
	    $edge .= "  ]\n";  
	}
	elsif($format == "plcc") {
	    $edge .= "= $source = $spatial = $target\n";
	}
	else {
	    die("get_edge_string: invalid output format\n");
	}
	return $edge;  
}

sub get_header_string {
    my $format = $_[0];
	my $pdbid = $_[1];
	my $chain = $_[2];
	my $graphtype = $_[3];
	
	my $header = "";
	
	if($format == "gml") {
		$header .= "graph [\n";
		$header .= "  id 1\n";
		$header .= "  label \"VPLG Protein Graph\"\n";
		$header .= "  comment \"pdbid=$pdbid|chainid=$chain|graphtype=$graphtype\"\n";
		$header .= "  directed 0\n";
		$header .= "  isplanar 0\n";
		$header .= "  creator \"RPG\"\n";
		$header .= "  pdb_id \"$pdbid\"\n";
		$header .= "  chain_id \"$chain\"\n";
		$header .= "  graph_type \"$graphtype\"\n";
		$header .= "  is_protein_graph 1\n";
		$header .= "  is_folding_graph 0\n";
		$header .= "  is_SSE_graph 1\n";
		$header .= "  is_AA_graph 0\n";
		$header .= "  is_all_chains_graph 0\n";	
	}
	elsif ($format == "plcc") {
	    $header .= "> format_version > 2\n";
        $header .= "> pdbid > $pdbid\n";
        $header .= "> chainid > $chain\n";
        $header .= "> graphtype > $graphtype\n";
	}
	else {
	  die("get_header_string: invalid output format\n");
	}
	return $header;
}


## ----------------------------------------------------------------------
############################## Main ####################################
## ----------------------------------------------------------------------

print "$apptag rpg.pl -- Random Protein Graph -- generate random protein graphs in the plg format of plcc\n";
print "$apptag This program is part of plcc, written by Tim Schaefer.\n";


## Parse and handle command line arguments
my %options = ();
getopts("hv:p:o:", \%options) or &usage;

&usage if (defined $options{"h"});

$num_vertices = $options{"v"} if(defined $options{"v"});
$edge_probability = $options{"p"} if(defined $options{"p"});
$outfile = $options{"o"} if(defined $options{"o"});
$format = $options{"f"} if(defined $options{"f"});

## Let's go

print "$apptag Generating undirected graph with $num_vertices edges and an edge probability of $edge_probability.\n";

if ($num_vertices < 0 || $edge_probability < 0 || $edge_probability > 1) {
    print "Invalid parameters given for edge probability and/or number of vertices.\n";
    &usage; 
}

if( ! ($format == "plcc" || $format == "gml")) {
    print "Invalid parameter given for format.\n";
    &usage; 
}

## Generate the graph file

open (OUTFILE, ">$outfile") || die "$apptag ERROR: Could not open output file '$outfile': $!\n"; 



## Create vertices
## Example line: '| 3kmf | G | albelig | 5 | H | 489 | 506 | G-658-  | G-675-  | PKVKAHGKKVLGAFSDGL'

my $pdbid = "RAND";
my $graphtype = "albelig";
my $sse_id = 1;
my $sse_seq_num = 1;
my $chain = "A";
my $sse_type;
my $dssp_start = 1;
my $dssp_end = 5;
my $pdb_start;
my $pdb_end;
my $sequence = "DOESNTMATTER";

my %sse_type_dict = ();


## Create header
print OUTFILE "> format_version > 2\n";
print OUTFILE "> pdbid > $pdbid\n";
print OUTFILE "> chainid > $chain\n";
print OUTFILE "> graphtype > $graphtype\n";


## Create vertices
for(my $i = 0; $i < $num_vertices; $i++) {
    
    $sse_type = &get_random_sse_type();
    $pdb_start = &get_pdb_identifier_from_dssp_residue($dssp_start, $chain);
    $pdb_end = &get_pdb_identifier_from_dssp_residue($dssp_end, $chain);
    print OUTFILE "| $pdbid | $chain | $graphtype | $sse_id | $sse_seq_num | $sse_type | $dssp_start | $dssp_end | $pdb_start | $pdb_end | $sequence\n";
    
    $sse_type_dict{ "$sse_id" } = $sse_type;
    
    ## set new values
    $sse_id++;
    $sse_seq_num++;
    $dssp_start += 10;
    $dssp_end += 10;
    
}

## Create edges
## Example line: '= 2 = a = 5'
my($sse1, $sse2);
my $contact_type;
my $num_edges = 0;
my $possible_contacts = 0;

for(my $i = 0; $i < $num_vertices; $i++) {
    for(my $j = $i + 1; $j < $num_vertices; $j++) {
	
	$possible_contacts++;
	
	$sse1 = $i + 1;
	$sse2 = $j + 1;
	
		if(&we_are_lucky()) {
			$contact_type = &get_random_contact_type($sse_type_dict{ "$sse1" }, $sse_type_dict{ "$sse2" });
			print OUTFILE "= $sse1 = $contact_type = $sse2\n";
			$num_edges++;
		}	
    }
}


close (OUTFILE); 

print "$apptag Created random undirected graph with $num_vertices vertices and $num_edges (of max $possible_contacts) edges.\n";
print "$apptag Done, results written to file '$outfile'.\n";











