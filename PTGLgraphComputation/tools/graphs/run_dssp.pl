#!/usr/bin/perl

$dir="/srv/www/PDB/";

opendir(VERZ,$dir) or die ("Couldn't open dir $dir $!");
my @liste = readdir(VERZ);
closedir(VERZ);

@not_insert=();

foreach $subdir (@liste){

    $pdbdir=$dir.$subdir;
    chdir($pdbdir);

    opendir(VERZ,$pdbdir) or die ("Couldn't open dir $pdbdir $!");
    my @files = readdir(VERZ);
    closedir(VERZ);

    foreach $file (@files){
	if ($file=~/^pdb(\S+)\.ent\.gz$/){

	    $id=$1;
	    $pdbfile=$id.".pdb";
	    $oldpdbfile="pdb$id".".ent";

	    print "$id: ";
	    $dsspfile=$id.".dssp";
	    $logfile=dssp.".".$id.".log";

	    system("gunzip $file");
	    system("mv $oldpdbfile $pdbfile");
	    system("dssp $pdbfile $dsspfile 2>$logfile");

	    if (check_dssp($logfile)){
		 #print("$id.dssp\n");
		 system ("rm -f $dsspfile");
		 #system ("rm -f $logfile");
		 system ("rm -f $pdbfile");
		 @not_insert = (@not_insert,$id);

		 print " not ok\n";
	    }else{
		 print " ok\n";
	    }
            system ("rm -f $logfile");
	    #exit(0);
	}
    }
}


foreach $id (@not_insert){
    print "$id\n";
}

sub check_dssp {

        my $file = $_[0];

        open (DATEI,"<$file") or die "Datei nicht gefunden\n";

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

