#!/usr/bin/perl

#$dir="/data2/bzcmaypa/pdb/divided/pdb/";
$dir="/srv/www/PDB/";

$ptglNotations="/home/ptgl/bin/graphs/makePTGLnotation.pl";

opendir(VERZ,$dir) or die ("Couldn't open dir $dir $!");
my @liste = readdir(VERZ);
closedir(VERZ);

@not_insert=();

$bool=0;

foreach $subdir (@liste){

    $pdbdir=$dir.$subdir;
    chdir($pdbdir);

    print "$pdbdir\n";

    #$bool=1 if $subdir=~/qz/;

    #next if !$bool;

    opendir(VERZ,$pdbdir) or die ("Couldn't open dir $pdbdir $!");
    my @files = readdir(VERZ);
    closedir(VERZ);

    foreach $file (@files){
	print "hallo $file\n";
	if ($file=~/^(\S+)\.ptgl.graph$/){
	    $id=$1;
	    print "$id\n";

	    $id=$1;
	    $ptglfile=$id.".ptgl.graph.notations.txt";


	    print "$ptglNotations $file > $ptglfile\n";	   
	    system("$ptglNotations $file > $ptglfile");

	    print "$ptglNotations $file alpha >> $ptglfile\n";
	    system("$ptglNotations $file alpha >> $ptglfile");

	    print "$ptglNotations $file beta >> $ptglfile\n";
	    system("$ptglNotations $file beta >> $ptglfile");

	    print "..$id done\n";

	    #exit(0);
	}
    }
    #exit(0) if $bool;
}


