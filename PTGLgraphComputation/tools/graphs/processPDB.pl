#!/usr/bin/perl  -I/home/ptgl/lib/perllib

use dssp_lib;
use pdb;

$dsspToStride="/home/ptgl/GAPOMGAP/scripts/dssptostride.pl";

#$dir="/srv/www/PDB/data/structures/divided/pdb/";
$dir="/srv/www/PDB/";


opendir(VERZ,$dir) or die ("Couldn't open dir $dir $!");
my @liste = readdir(VERZ);
closedir(VERZ);

@not_insert=();

print "Test\n";

foreach $subdir (@liste){

    next if $subdir eq "." or $subdir eq "..";
    $pdbdir=$dir.$subdir;
    chdir($pdbdir);

    print "$pdbdir\n";

    opendir(VERZ,$pdbdir) or die ("Couldn't open dir $pdbdir $!");
    my @files = readdir(VERZ);
    closedir(VERZ);

    foreach $file (@files){

	if ($file=~/^(....)\.pdb/){
		
	    print "$file\n";
	    $protein=$1;
	    $proteinfile=$protein.".pdb";
	    $nmrinsert=0;

            # ergebnis des resolution check
	    my $rescheck = resolution_check($protein);

	    my %protein=();

	    my ($header,$title,$remark);
	    if ($rescheck == 0 or $rescheck == 1){
		print "resolution ok\n" if $rescheck==0;
		print "NMR structure theoretical model\n" if $rescheck==1;

		open(PDB,$proteinfile) or die $!;
		while($str=<PDB>){
		    chomp($str);
		    next if length($str)==0;

		    $header=$str."\n" if $str=~/^HEADER/;
		    $title.=$str."\n" if $str=~/TITLE/;
		    if ($str=~/^ATOM/){
			my $chain  = substr($str,21,1);
			if ($chain =~/ /){
				$chain="_";
			}
			#print "$str\t$protein$chain\n";
			push @{$protein{$chain}},$str."\n";
		    }elsif($str=~/^TER/){
			my $chain  = substr($str,21,1);
			if ($chain =~/ /){
				$chain="_";
			    }
			push @{$protein{$chain}},$str."\n";
		    }
		}
		close(PDB);
	    }elsif ($rescheck == -1 ){
		print "Resolution too bad\n";
	    }elsif ($rescheck == 2){
		print "NMR structure with multiple models\n";	
		# process only model 1
		@model=();
		$model=0;
		open(NMR,$proteinfile) or die "Can't open $proteinfile\n";
		while($str=<NMR>){
		    last if $model>1;
		    $header=$str if $str=~/^HEADER/;
		    $title.=$str if $str=~/TITLE/;
		    $remark=$str if $str=~/^REMARK/ and $str=~/2/ and $str=~/RESOLUTION/;
		
		    if($model == 1 and $str =~ /(^ATOM|^TER)/){
			my $chain  = substr($str,21,1);
			if ($chain =~/ /){
				$chain="_";
			}
        	    	# kopieren der Koordinaten fuer ein Modell
            		push @{$protein{$chain}},$str;
		    }elsif($str=~/^MODEL +([0-9]+) /) {
			# Neues Model beginnt	
			$model++			
			}
		}
	
		close(NMR); 
	    }else{
		print "Failure in resolution_check\n";
	    }
            #exit(0);
	    print "..make chain files\n";

	    foreach $chain (keys(%protein)){
		$proteinfile=$protein.$chain.".pdb";
		$dsspfile   =$protein.$chain.".dssp";
		$stridefile =$protein.$chain.".stride";
		$logfile    =$protein.$chain.".dssplog";

		open(PDB,">$proteinfile") or die $!;
		print PDB $header.$title;
		print PDB $remark if length($remark)>0;

		foreach $line (@{$protein{$chain}}){
		    print PDB $line;
		}
		close(PDB);


		print "dssp $proteinfile $dsspfile 2>$logfile\n";

		system("dssp $proteinfile $dsspfile 2> $logfile");
		if (check_dssp_here($logfile)){
		    #print("$id.dssp\n");
		    system ("rm -f $dsspfile");
		    #system ("rm -f $logfile");
		    system ("rm -f $pdbfile");
		    @not_insert = (@not_insert,$id);

		    print " $protein$chain not ok\n";
		}else{
		    print " $protein$chain ok\n";
		    system("$dsspToStride $dsspfile $stridefile");
		    #exit(0);
		}
		system ("rm -f $logfile");
	    }
	    print "$protein finished\n";	    
	}	
    }
}


sub check_dssp_here {

        my $file = $_[0];

        open (DATEI,$file) or die "$file not found\n";

        my @zeilen = <DATEI>;
        close(DATEI);

        foreach (@zeilen) {

                if (($_ =~ /No residue with complete/)
                        or ($_ =~ /No residue with complete backbone/))
                {
                        return 1;
                }#if
        }#foreach

        return 0;
}#check_dssp
