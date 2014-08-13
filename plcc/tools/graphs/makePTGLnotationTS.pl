#!/usr/bin/perl

use Graph::Undirected;
use Graph;
use my_array;

use strict;
#use warnings;

my $graphfile=$ARGV[0];
my $graphtype="albe";
$graphtype=$ARGV[1] if $ARGV[1]=~/(beta|alpha)/;


my $verbose=0;
$verbose=1 if $ARGV[1] eq 1 or $ARGV[2]==1;
$verbose=1;


print "graphtype: $graphtype\n";

my $g = Graph::Undirected->new;
my $foldids="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

my ($str,$protein,$chain,$nvertices,$fold,$seqNotation,$redNotation,$adjNotation,$keyNotation,$start,$keystart,$seqstart,$adjstart,$redstart,$i,$hasCycle,$kl1,$kl2, $v,$next, $topo,$edgeType, $vertexType);
my $ct=0;

my %vertices=();
my %edges=();
my $nedges=0;
my %notations=();
my %degrees=();
my $isBifurcated=1;
my %totalPos=();
my $tct=0;

open(GRAPH,$graphfile) or die $!;
while($str=<GRAPH>){
	chomp($str);

	next if length($str)==0;
	if ($str=~/# protein: (....)(.)$/){
		$protein=$1;
		$chain=$2;
		if ( $g->is_compat02 ) {
		  $g->set_attribute('name',$protein.$chain);
		}
		else {
		  $g->set_graph_attribute('name',$protein.$chain);
		}
	}elsif($str=~/# SSEs: (\d+)$/){
		$nvertices=$1 if $graphtype=~/albe/;
	}elsif($str=~/^([HE]).+(\d+).+(\d+).+(\d+)$/){
		$tct++;
		next if (($1 eq "H" and $graphtype eq "beta") or($1 eq "E" and $graphtype eq "alpha")); 
		$ct++;
		$vertices{$ct}{type} = $1;
		$vertices{$ct}{len}  = $2;
		$vertices{$ct}{start}= $3;
		$vertices{$ct}{stop} = $4;
		$g->add_vertex( $ct );
		if ( $g->is_compat02 ) {
		  $g->set_attribute('type',$ct,lc($1));
		  $g->set_attribute('len',$ct,$2);
		  $g->set_attribute('start',$ct,$3);
		  $g->set_attribute('stop',$ct,$4);
		} else {
		  #$g->set_vertex_attribute('type',$ct,lc($1));
		  #$g->set_vertex_attribute('len',$ct,$2);
		  #$g->set_vertex_attribute('start',$ct,$3);
		  #$g->set_vertex_attribute('stop',$ct,$4);		
		  $g->set_vertex_attribute($ct,'type',lc($1));
		  $g->set_vertex_attribute($ct,'len',$2);
		  $g->set_vertex_attribute($ct,'start',$3);
		  $g->set_vertex_attribute($ct,'stop',$4);		
		}
		print "g = $g\n" if $verbose;

		$totalPos{$tct}=$ct;
		print "$str\n" if $verbose;
	}elsif($str=~/^(\d+) (\d+) ([APX]) (EE|HH|HE)$/){
		
		next if (($4 ne "EE" and $graphtype eq "beta") or ($4 ne "HH" and $graphtype eq "alpha"));
		$edges{$totalPos{$1+1}}{$totalPos{$2+1}}{'type'}=$4;
		$edges{$totalPos{$1+1}}{$totalPos{$2+1}}{'topo'}=$3;
		$edges{$totalPos{$1+1}}{$totalPos{$2+1}}{'status'}=0;
		$g->add_edge( $totalPos{$1+1},$totalPos{$2+1} );

		my $topo=$3;
		$topo='m' if $3 eq "X";
		if ( $g->is_compat02 ) {
		  $g->set_attribute('type',$totalPos{$1+1},$totalPos{$2+1},lc($4));
		  $g->set_attribute('topo',$totalPos{$1+1},$totalPos{$2+1},lc($topo));
		  $g->set_attribute('status',$totalPos{$1+1},$totalPos{$2+1},0);
		} else {
		  #$g->set_edge_attribute('type',$totalPos{$1+1},$totalPos{$2+1},lc($4));
		  #$g->set_edge_attribute('topo',$totalPos{$1+1},$totalPos{$2+1},lc($topo));
		  #$g->set_edge_attribute('status',$totalPos{$1+1},$totalPos{$2+1},0);		
		  $g->set_edge_attribute($totalPos{$1+1},$totalPos{$2+1},'type',lc($4));
		  $g->set_edge_attribute($totalPos{$1+1},$totalPos{$2+1},'topo',lc($topo));
		  $g->set_edge_attribute($totalPos{$1+1},$totalPos{$2+1},'status',0);		
		}
		$nedges++;
		print "$str\n" if $verbose;
		print "g = $g\n" if $verbose;

	}
}
close(GRAPH);

print "protein: $protein chain: $chain\n" ;
print "number of edges: $nedges\n"  if $verbose ;
print "number of vertices: $nvertices $ct\n" if $verbose;
print "g = $g\n" if $verbose;

print "number of edges: ",scalar($g->edges),"\n" if $verbose;
print "number of vertices: ",scalar($g->vertices),"\n" if $verbose;

# get the connected components as list of vertex lists
my @S = $g->connected_components;
my %S=();
print "number of connected components: ",scalar(@S),"\n";

foreach my $s (@S){
	$fold=substr($foldids,$ct++,1);
	my @vs = sort {$a <=> $b} @{$s};
	$S{$vs[0]}=[ @vs ];
}

# check degrees
for($i=1;$i<=scalar($g->vertices);$i++){
	$degrees{$i}=$g->degree($i);
}

print "\n\n------------------------------\n" if $verbose;
$ct=0;
foreach my $s (sort{$a<=>$b} keys(%S)){
	$fold=substr($foldids,$ct++,1);
	my @vs = sort {$a <=> $b} @{$S{$s}};
	my $hasCycle=0;

	print "connected component $ct $fold: ";print_array(@vs);print "\n";

	$kl1="[";
	$kl2="]";

	# checkBifurcation
	my $isBifurcated=isBifurcated(@vs);

	if (scalar(@vs)==1){
		if($graphtype=~/albe/){
		        if ( $g->is_compat02 ) {
			  $seqNotation=$kl1.$g->get_attribute('type',$vs[0]).$kl2;
			  $keyNotation=$kl1.$g->get_attribute('type',$vs[0]).$kl2;
			  $adjNotation=$kl1.$g->get_attribute('type',$vs[0]).$kl2;
			  $redNotation=$kl1.$g->get_attribute('type',$vs[0]).$kl2;
			}
			else {
			  $seqNotation=$kl1.$g->get_vertex_attribute('type',$vs[0]).$kl2;
			  $keyNotation=$kl1.$g->get_vertex_attribute('type',$vs[0]).$kl2;
			  $adjNotation=$kl1.$g->get_vertex_attribute('type',$vs[0]).$kl2;
			  $redNotation=$kl1.$g->get_vertex_attribute('type',$vs[0]).$kl2;
			}
		}else{
			$seqNotation=$kl1.$kl2;
			$keyNotation=$kl1.$kl2;
			$adjNotation=$kl1.$kl2;
			$redNotation=$kl1.$kl2;
		}
		$adjstart   = $vs[0];
		$redstart   = $vs[0];
		$keystart   = $vs[0];
		$seqstart   = $vs[0];
	}else{
		if(!$isBifurcated) {
			$kl1="{";
			$kl2="}";
			print "$fold is not bifurcated\n" if $verbose; 
		}elsif(hasConnectedComponentCycle(@vs)) {
			$kl1="(";
			$kl2=")";
			print "$fold has a cycle\n" if $verbose;
			$hasCycle=1
		}else{
			print "$fold is bifurcated\n" if $verbose;
		}
		
		# write the opening bracket and for albe the type of the starting SSE

		$adjNotation=$kl1;
		$redNotation=$kl1;
		$keyNotation=$kl1;
		$seqNotation=$kl1;
		
		if ( $g->is_compat02 ) {
		  $seqNotation.=$g->get_attribute("type",$vs[0]) if $graphtype eq "albe";
		}
		else {
		   $seqNotation.=$g->get_vertex_attribute("type",$vs[0]) if $graphtype eq "albe";
		}

		$adjstart = $vs[0];
		$redstart = $vs[0];
		$keystart = $vs[0];
		$seqstart = $vs[0];

		my %order=();	# hash for ordering

		#################################################
		# first check where to begin
		# Ina: ist die start position in key die nummer der SSE oder die Nummer 
		# innerhalb der Connected Component
		my %pos=($vs[0] => 1);
		my $cur=$vs[0];

		my $bool=0;
		for($i=0;$i<scalar(@vs);$i++){
			print "$i $vs[$i] has degree ".$degrees{$vs[$i]}."\n" if $verbose;
			if(!$bool and $degrees{$vs[$i]}==1){
				$cur=$vs[$i];
				$bool=1;
			}
			$pos{$vs[$i]}=$i+1;
		}


		#################################################
		# ADJ Notation

		print "\nADJ notation:\n" if $verbose;
		my $adjcur=$cur;
		my %adjvisited=();
		
		if ( $g->is_compat02 ) {
		  $adjNotation.=$g->get_attribute("type",$adjcur) if $graphtype eq "albe";
		} else {
		  $adjNotation.=$g->get_vertex_attribute("type",$adjcur) if $graphtype eq "albe";
		}
		
		$adjstart=$cur;
		$adjvisited{$cur}++;
		my %zvertices=();
		my @zstack=();
		my %adjdegrees=%degrees;

		my @tvertex=($adjcur);
		my %tvertex=();
	
		my $found=0;

		$order{$adjcur}='+';

		my $hC=$hasCycle;
		my $no=0;

		my $adjct=0;
		$tvertex{$adjcur}=$adjct+1;
		#print "tvertex: tvertex[$adjcur] (".$tvertex{$adjcur}.")=".$adjct+1."\n";
		
		while ( !isFinished(\%adjdegrees,\@vs) or ($hC and scalar(keys(%adjvisited))<=scalar(@vs)) ) {
			#$no++;
		
			my @adjneighbors=sort {$a<=>$b}($g->neighbors($adjcur));
			$found=0;
			$next=-1;

			if ($verbose){
				print "\n$adjcur has neighbours: ";print_array(@adjneighbors);print "\n";
			}

			foreach my $adjv (@adjneighbors){
				
				# close the cycle
				if($hC and scalar(keys(%adjvisited)) == scalar(@vs)){
					$found=1;
					$next=$adjv;
					$hC=0;
				}
				
				my $left=$adjcur;
				my $right=$adjv;
				if($left > $right){$left=$adjv; $right=$adjcur;}

				#next if exists($adjvisited{$adjv}) and $g->get_attribute('status',$left,$right)==1;
				if ( $g->is_compat02 ) {
				  next if $g->get_attribute('status',$left,$right)==1;
				}
				else {
				  next if $g->get_edge_attribute('status',$left,$right)==1;
				}
				
				next if $adjdegrees{$adjv}==0;

				if (!$found){
					$next=$adjv;
					$found=1;

					last;
				}
			}
			
			print " next found $found: $next\n" if $verbose;	

			if($found){

				my $left=$adjcur;
				my $right=$next;
				if($left > $right){$left=$next; $right=$adjcur;}
				
				if ( $g->is_compat02 ) {
				  $edgeType=$g->get_attribute('topo',$left,$right);
				} else {
				  $edgeType=$g->get_edge_attribute('topo',$left,$right);
				}
				$adjNotation.="," if (($graphtype eq "albe") or ($graphtype ne "albe" and scalar(keys(%adjvisited))>1));
				$adjNotation.=($next - $adjcur).lc($edgeType);

				if ( $g->is_compat02 ) {
				  $g->set_attribute('status',$left,$right,1);
				}
				else {
				  $g->set_edge_attribute('status',$left,$right,1);
				}
			
				$adjdegrees{$adjcur}--;
				$adjdegrees{$next}--;	
			}else{
				# end of path
				if(getVertexDegree1(\%adjdegrees,\@vs)){
					$next=getVertexDegree1(\%adjdegrees,\@vs);
					print "now the z-vertices: $next\n" if $verbose;
					# first go back to the last vertex
					$adjNotation.="," if (($graphtype eq "albe") or ($graphtype ne "albe" and scalar(keys(%adjvisited))>1));
					$adjNotation.=($next - $adjcur)."z";
				}elsif(getVertexDegreeGreater1(\%adjdegrees,\@vs)){
				        $next=getVertexDegreeGreater1(\%adjdegrees,\@vs);
					print "now the z-vertices: $next\n" if $verbose;
					# first go back to the last vertex
					$adjNotation.="," if (($graphtype eq "albe") or ($graphtype ne "albe" and scalar(keys(%adjvisited))>1));
				}else{    
					die "ERROR: $adjcur\n";
				}
			}

			
			if ( $g->is_compat02 ) {
			  $adjNotation.=$g->get_attribute("type",$next) if $graphtype eq "albe";	
			} else {
			  $adjNotation.=$g->get_vertex_attribute("type",$next) if $graphtype eq "albe";	
			}
			
			# ordering

			if($edgeType eq 'p' or $edgeType eq 'm'){
				$order{$next}=$order{$adjcur};
			}elsif($order{$adjcur} eq '-'){
				$order{$next}='+';
			}else{
				$order{$next}='-';
			}

			$adjcur=$next;

			if(scalar(@tvertex)<scalar(@vs)){
				$adjct++;
				push @tvertex, $adjcur;
				$tvertex{$adjcur}=$adjct+1;
			}

			$adjvisited{$next}++;
			if($verbose){
				print "notation: $adjNotation\n" if $verbose;
				#print "degrees: \n";print_hash(\%adjdegrees); print "\n";
				print "visited: ";print_array(sort{$a<=>$b} keys(%adjvisited));print "\n" if $verbose;
			}			
		}	

		# reset the attrbutes
		resetEdgeStatus(@vs);

		#################################################
		# RED Notation
		print "\nRED notation:\n" if $verbose;
		my $redcur=$cur;
		my %redvisited=();
		
		if ( $g->is_compat02 ) {
		  $redNotation.=$g->get_attribute("type",$redcur) if $graphtype eq "albe";
		} else {
		  $redNotation.=$g->get_vertex_attribute("type",$redcur) if $graphtype eq "albe";
		}
		
		$redstart=$pos{$cur};
		$redvisited{$cur}++;
		
		my $found=0;
		my %reddegrees=%degrees;

		my $hC=$hasCycle;
		my $no=0;
		while ( !isFinished(\%reddegrees,\@vs) or ($hC and scalar(keys(%redvisited))<=scalar(@vs)) ) {
			#$no++;
		
			my @redneighbors=sort {$a<=>$b}($g->neighbors($redcur));
			$found=0;
			$next=-1;

			if ($verbose){
				print "$redcur has neighbours: ";print_array(@redneighbors);print "\n";
			}

			foreach my $redv (@redneighbors){

				my $left=$redcur;
				my $right=$redv;
				if($left > $right){$left=$redv; $right=$redcur;}

				if ( $g->is_compat02 ) {
				  print "neighbour: $redv status: ".$g->get_attribute('status',$left,$right)."\n" if $verbose;
				} else {
				  print "neighbour: $redv status: ".$g->get_edge_attribute('status',$left,$right)."\n" if $verbose;
				}
				
				# close the cycle
				if($hC and scalar(keys(%redvisited)) == scalar(@vs)){
					$found=1;
					$next=$redv;
					$hC=0;
				}

				
				if ( $g->is_compat02 ) {
				  next if $g->get_attribute('status',$left,$right)==1;
				}
				else {
				  next if $g->get_edge_attribute('status',$left,$right)==1;
				}
				
				
				
				next if $reddegrees{$redv}==0;

				if (!$found){
					$next=$redv;
					$found=1;

					last;
				}
			}
			
			print " next found $found: $next\n" if $verbose;	

			if($found){

				my $left=$redcur;
				my $right=$next;
				if($left > $right){$left=$next; $right=$redcur;}

				if ( $g->is_compat02 ) {
				  $edgeType=$g->get_attribute('topo',$left,$right);
				} else {
				  $edgeType=$g->get_edge_attribute('topo',$left,$right);				
				}

				$redNotation.="," if (($graphtype eq "albe") or ($graphtype ne "albe" and scalar(keys(%redvisited))>1));
				$redNotation.=($pos{$next} - $pos{$redcur}).lc($edgeType);

				
				if ( $g->is_compat02 ) {
				  $g->set_attribute('status',$left,$right,1);
				} else {
				  $g->set_edge_attribute('status',$left,$right,1);
				}

				$reddegrees{$redcur}--;
				$reddegrees{$next}--;	
				
			}else{
				# end of path

				if(getVertexDegree1(\%reddegrees,\@vs)){
					$next=getVertexDegree1(\%reddegrees,\@vs);
					print "now the z-vertices: $next\n" if $verbose;
					# first go back to the last vertex
					$redNotation.="," if (($graphtype eq "albe") or ($graphtype ne "albe" and scalar(keys(%adjvisited))>1));
					$redNotation.=($pos{$next} - $pos{$redcur})."z";
				}elsif(getVertexDegreeGreater1(\%reddegrees,\@vs)){
					$next=getVertexDegreeGreater1(\%reddegrees,\@vs);
					print "now the z-vertices: $next\n" if $verbose;
					# first go back to the last vertex
					$redNotation.="," if (($graphtype eq "albe") or ($graphtype ne "albe" and scalar(keys(%adjvisited))>1));
					$redNotation.=($pos{$next} - $pos{$redcur})."z";
				}else{
					die "ERROR: $adjcur\n";
				}
			}

			
			if ( $g->is_compat02 ) {
			  $redNotation.=$g->get_attribute("type",$next) if $graphtype eq "albe";	
			} else {
			  $redNotation.=$g->get_vertex_attribute("type",$next) if $graphtype eq "albe";	
			}
			
			$redcur=$next;
			$redvisited{$next}++;
			print "notation: $redNotation\n" if $verbose;
			if($verbose){
				#print "degrees: \n";print_hash(\%reddegrees); print "\n";
				print "notation: $redNotation\n" if $verbose;
				print "visited: ";print_array(sort{$a<=>$b} keys(%redvisited));print "\n";
			}
		}		

		# reset the attrbutes
		resetEdgeStatus(@vs);

		#################################################
		# KEY Notation

		if ($isBifurcated){
			my $keycur=$cur;
			my %keyvisited=();

			if ($verbose){
				print "\nKEY notation\n";
			
				print "order    : \n";print_hash(\%order);print "\n";
				print "pos      : \n";print_hash(\%pos);print "\n";
				print "tvertex  : \n";print_hash(\%tvertex);print "\n";
			}

			my @order=sort{$a<=>$b} keys(%tvertex);
			my @pos=sort {$a<=>$b} keys(%pos);

			$keystart=$tvertex{$pos[0]};	
			
			if ( $g->is_compat02 ) {
			  $keyNotation.=$g->get_attribute("type",$pos[0]) if $graphtype eq "albe";
			} else {
			  $keyNotation.=$g->get_vertex_attribute("type",$pos[0]) if $graphtype eq "albe";
			}
			
			for($i=1;$i<=scalar(@pos)-1;$i++){
				
				my $dist=$tvertex{$pos[$i]}-$tvertex{$pos[$i-1]};
				
				if ($verbose){
					print "i  =".$i." pos[$i]=".$pos[$i]." ".$tvertex{$pos[$i]}."\n";
					print "i-1=".($i-1)." pos[$i]=".$pos[$i-1]." ". $tvertex{$pos[$i-1]}."\n"; 
					print "dist=$dist\n";
				}

				$keyNotation.="," if (($i==1 and $graphtype eq "albe") or ($i>1));
				$keyNotation.=$dist;
				$keyNotation.="x" if ($order{$pos[$i-1]} eq $order{$pos[$i]});
				
				if ( $g->is_compat02 ) {
				  $keyNotation.=$g->get_attribute('type',$vs[$i]) if $graphtype eq "albe";
				} else {
				  $keyNotation.=$g->get_vertex_attribute('type',$vs[$i]) if $graphtype eq "albe";
				}
			}

			print "keynotation: $keyNotation\n" if $verbose;
			
		}


		####################################################################
		# SEQ Notation			
		for($i=1;$i<scalar(@vs);$i++){
			$seqNotation.="," if (($graphtype eq "albe") or (($graphtype ne "albe") and ($i > 1))) ;
			$seqNotation.=abs($vs[$i]-$vs[$i-1]);
			
			if ( $g->is_compat02 ) {
			  $seqNotation.=$g->get_attribute('type',$vs[$i]) if $graphtype eq "albe";
			}else {
			  $seqNotation.=$g->get_vertex_attribute('type',$vs[$i]) if $graphtype eq "albe";
			}
		}

		# write the closing bracket

		$adjNotation.=$kl2;
		$redNotation.=$kl2;
		$keyNotation.=$kl2;
		$seqNotation.=$kl2;
		print "\n" if $verbose;
	}

	$notations{'ADJ'}{$fold}{'notation'}=$adjNotation;
	$notations{'ADJ'}{$fold}{'start'}=$adjstart;
	$notations{'ADJ'}{$fold}{'size'}=scalar(@vs);

	$notations{'RED'}{$fold}{'notation'}=$redNotation;
	$notations{'RED'}{$fold}{'start'}=$redstart;
	$notations{'RED'}{$fold}{'size'}=scalar(@vs);

	$notations{'KEY'}{$fold}{'notation'}=$keyNotation;
	$notations{'KEY'}{$fold}{'start'}=$keystart;
	$notations{'KEY'}{$fold}{'size'}=scalar(@vs);

	$notations{'SEQ'}{$fold}{'notation'}=$seqNotation;
	$notations{'SEQ'}{$fold}{'start'}=$seqstart;
	$notations{'SEQ'}{$fold}{'size'}=scalar(@vs);

	print "----------------\n" if $verbose;
}

foreach my $notation (sort keys(%notations)){

	print "Graph-type: $graphtype, Notation: $notation\n";
	print "FOLD\t#SSE\tStart\tGraph\n";
	foreach $fold (sort keys(%{$notations{$notation}})){
		print $fold."\t".$notations{$notation}{$fold}{'size'}."\t".$notations{$notation}{$fold}{'start'}."\t".$notations{$notation}{$fold}{'notation'}."\n"; 
	}
	print "#--------------------------------\n";
}

######################################################################
##################### subroutines ####################################
######################################################################

############################################
# check the degrees of a set of vertices

sub isBifurcated{

	my @v=@_;

	foreach (@v){
		return 0 if $g->degree($_) > 2; 
	}
	return 1;
}

# check if a list of vertices in a connected component has a cycle
sub hasConnectedComponentCycle{
	my @v=@_;

	my %visited=();
	my @stack=();
	my $cur;

	push @stack,$v[0];	
	if (!exists($visited{$i})){
		while(scalar(@stack)>0){
			$cur=$stack[0];
			shift(@stack);
			if(!exists($visited{$cur})){
				$visited{$cur}++;
				my @n=$g->neighbors($cur);
				my $start=0;
				my $end=scalar(@n)-1;
				while($start < $end){
					if($visited{$n[$start]}){
						return 1;
					}else{
						push @stack, $n[$start];
					}
					$start++;
				}	
			}	
		}
	}
	return 0;
}

# sets the edge_attribute 'status' to zero
sub resetEdgeStatus{
	my @v=@_;


	foreach my $vertex (@v){
		my @n=$g->neighbors($vertex);

		foreach my $next (@n){
			if($g->has_edge($vertex,$next)){
				my $left=$vertex;
				my $right=$next;
				if($left > $right){$left=$next; $right=$vertex;}

				if ( $g->is_compat02 ) {
				    $g->set_attribute('status',$left,$right,0);
				  } else {
				    $g->set_edge_attribute('status',$left,$right,0);
				  }
			}
		}
	}
}

# sub check edge status
sub checkEdgeStatus{

	my $vertex=$_[0];
	my @n=$g->neighbors($vertex);

	foreach my $next (@n){

		my ($left,$right);
		if($next < $vertex){$left=$next;$right=$vertex;}	
		else{$left=$vertex;$right=$next;}

		if($g->has_edge($left,$right)){
			print "$vertex is not completed yet\n";
			return 0 if ($g->get_attribute('status',$left,$right) == 0);
		}
	}
	print "$vertex is completely processed\n";
	return 1;
}

# print z vertices
sub print_zvertices{

	my %zvertices=%{$_[0]};

	foreach my $key (sort {$a<=>$b}(keys(%zvertices))){
		print "$key => ";
		foreach (@{$zvertices{$key}}){
			print "$_ ";
		}
		print "\n";
	}

}

# get vertex degree 1 next to N-terminus
sub getVertexDegree1{
	my %degree=%{$_[0]};
	my @vertices=@{$_[1]};

	foreach my $vertex (sort {$a<=>$b} (@vertices)){
		return $vertex if $degree{$vertex}==1;
	}

	return 0;
}

# get vertex with degree >1 next to N-terminus
sub getVertexDegreeGreater1{
	my %degree=%{$_[0]};
	my @vertices=@{$_[1]};

	foreach my $vertex (sort {$a<=>$b} (@vertices)){
		return $vertex if $degree{$vertex}>1;
	}

	return 0;
}


# isFinished
sub isFinished{
	my %degree=%{$_[0]};
	my @vertices=@{$_[1]};

	foreach my $vertex (sort {$a<=>$b} @vertices){
		return 0 if $degree{$vertex}>0;
	}

	return 1;
}
