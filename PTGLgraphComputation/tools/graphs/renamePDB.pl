#!/usr/bin/perl  -I/home/ptgl/lib/perllib

use strict;

my $dir="/srv/www/PDB/";

opendir(VERZ,$dir) or die ("Couldn't open dir $dir $!");
my @liste = readdir(VERZ);
closedir(VERZ);

print "dir: $dir\n";
print "dir files: ".scalar(@liste)." Files\n";

my $ct=0;

foreach my $subdir (@liste){

    next if $subdir=~/\./;	
    
    my $pdbdir=$dir.$subdir;
    chdir($pdbdir);

    opendir(VERZ,$pdbdir) or die ("Couldn't open dir $pdbdir $!");
    my @files = readdir(VERZ);
    closedir(VERZ);

    print "subdir: $subdir\n";
    print "subdir files: ".scalar(@files)."\n";

    foreach my $file (@files){

	next if $file eq "." or $file eq "..";
	print "$file\n";
	if ($file=~/^pdb(....)\.ent\.gz/){

		my $protein=$1;
	    	my $newfile=$pdbdir."/".$protein.".pdb.gz";
		my $oldfile=$pdbdir."/".$file;
		my $sys="cp $oldfile $newfile";
		print "$sys\n";
		system($sys);
		$sys="gunzip $newfile";
		print "$sys\n";
		system($sys);
		$ct++;
	}	
    }
}

print "total $ct PDB structures were processed !!\n";
