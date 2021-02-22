#!/usr/bin/perl

$dir="/data2/bzcmaypa/pdb/divided/pdb/";

$dsspToStride="/home/bzcmaypa/theseus/GAPOMGAP/scripts/dssptostride.pl";

opendir(VERZ,$dir) or die ("Couldn't open dir $dir $!");
my @liste = readdir(VERZ);
closedir(VERZ);

@not_insert=();

foreach $subdir (@liste){

    $pdbdir=$dir.$subdir;
    chdir($pdbdir);

    print "$pdbdir\n";

    opendir(VERZ,$pdbdir) or die ("Couldn't open dir $pdbdir $!");
    my @files = readdir(VERZ);
    closedir(VERZ);

    foreach $file (@files){
	if ($file=~/^(\S+)\.dssp$/){
	    $id=$1;
	    print "$id\n";

	    $id=$1;
	    $dsspfile=$id.".dssp";
	    $stridefile=$id.".stride";
	   
	    system("$dsspToStride $dsspfile $stridefile");

	    #exit(0);
	}
    }
}


