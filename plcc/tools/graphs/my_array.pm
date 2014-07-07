# db_lib.pm DB-Zugriff mit Perl und DBI.pm

package my_array;

#use strict;

use Exporter;

use vars qw(@ISA @EXPORT $VERSION);

@ISA = qw(Exporter);

@EXPORT = qw(max2 min2 print_array get_diff get_a_minus_b get_ident print_array_to_file in del_element get_a_minus_b_2 NumberSort union print_keys get_max_value_in_array get_min_value_in_array sum_array union_of_hashes print_hash isIN );

$VERSION = '1.00';


####################################
# checks if value is in array
####################################

sub isIN{
	my $element=$_[0];
	my @array  =@{$_[1]};

	my %hash=();
	foreach my $i (@array){
		$hash{$array[i]}++;
	}
	return 1 if exists($hash{$element});
	return 0;
}

####################################
# print hash
####################################

sub print_hash{
	my %hash=%{$_[0]};
	foreach my $k (sort(keys(%hash)) ) {
  	  	print "$k => $hash{$k}\n";
	}
}
####################################
# union of hashes
####################################

sub union_of_hashes{
	my %first  = %{$_[0]};
	my %second = %{$_[1]};
	my %union=();
	while (($k,$v) = each %first) { $union{$k} = $v; }
	while (($k,$v) = each %second) { $union{$k} = $v; }
	return %union;
}

####################################
# get sum array
####################################

sub sum_array{

	local($array) = @_;
	local($sum, $len_array, $i);
	
	$sum=0;	
	$len_array = @$array;
	for($i=0; $i<$len_array; $i++){
		$sum = $sum + $$array[$i];	
	}
	
	return $sum;
}

####################################
# get maximal number in array
####################################

sub get_max_value_in_array{
	local($array) = @_;
	local($len_array, $ret, $i);
	
	$len_array = @$array;
	$ret = $$array[0];
	for($i=1; $i<$len_array; $i++){
		if($ret < $$array[$i]){
			$ret = $$array[$i];
		}	
	}
	
	return $ret;
}

####################################
# get minimmal number in array
####################################

sub get_min_value_in_array{
	local($array) = @_;
	local($len_array, $ret, $i);
	
	$len_array = @$array;
	$ret = $$array[0];
	for($i=1; $i<$len_array; $i++){
		if($ret > $$array[$i]){
			$ret = $$array[$i];
		}	
	}
	
	return $ret;
}

####################################
# max of two numbers
####################################

sub max2 {

    my($a, $b) = @_;
    return ($a>$b ? $a : $b);
}

####################################
# min of two numbers
####################################

sub min2  {

    my($a, $b) = @_;
    return ($a<$b ? $a : $b);
}

#########################################################
# print_keys prints the keys of a hash                  #
#########################################################

sub print_keys{

    #print hash

    my $hash=shift;

    my @keys=sort keys %$hash;
    foreach my $key(@keys){
        print "$key\n";
    }
}


######################################################################
# print_array gibt die Elemente eines arrays aus                     #
######################################################################

sub print_array{

        my @array = @_;

        my $count = @array;

        #print "\n";

	my $ct=0;

        foreach (@array) {
		print "\t" if $ct>0;

                print $_;

		$ct++;
        }#foreach

        #print "\nThe array has $count elements\n\n";
}#print_array

########################################################################
# print_array_to_file schreibt die Elemente eines arrays in einen file #
########################################################################

sub print_array_to_file{

        my $file  = $_[0];
	my @array = @{$_[1]};

        open(FILE,">$file");

        foreach (@array) {

	    print FILE $_,"\n";
        }#foreach

	close(FILE);

}#print_array

######################################################################
# get_diff ermittelt aus zwei arrays die Elemente, die nur in einem  #
# vorkommen (ohne Schnittmenge)                                      #
######################################################################

sub get_diff{

        my @a1 = @{$_[0]};
        my @a2 = @{$_[1]};

        my (%hash,@diff);

        foreach (@a1,@a2){

                $hash{$_}++;
        }#foreach

        foreach (keys %hash){

                if ( $hash{$_} == 1 ) { push @diff,$_; }
        }#foreach

        return @diff;
}#get_diff

######################################################################
# get_ident ermittelt aus zwei arrays die Elemente, die in beiden    #
# vorkommen                                                         #
######################################################################

sub get_ident{

        my @a1 = @{$_[0]};
        my @a2 = @{$_[1]};

        my (%hash,@ident);

        foreach (@a1,@a2){

                $hash{$_}++;
        }#foreach

        foreach (keys %hash){

                if ( $hash{$_} == 2 ) { push @ident,$_; }
        }#foreach

        return @ident;
}#get_ident


######################################################################
# get_diff ermittelt aus zwei arrays die Elemente, die nur in einem  #
# vorkommen (mit Schnittmenge)                                      #
######################################################################

sub get_a_minus_b{

        my @a = @{$_[0]};
        my @b = @{$_[1]};

        my (@diff,%hash);

        foreach (@a) {

                $hash{$_}++;
        }

        foreach (@b) {

                if ( !exists($hash{$_}) ) { push @diff,$_; }
        }

        return @diff;
}#get_a_minus_b

sub union{

        my @a = @{$_[0]};
        my @b = @{$_[1]};

        my (%hash);

        foreach (@a) {

                $hash{$_}++;
        }

        foreach (@b) {

                $hash{$_}++;
        }

        return keys(%hash);
}#get_a_minus_b

#################################################################
# sub in (@array) entfernt aus einem array alle Doppleten
# und gibt die neue Liste zurueck
#################################################################

sub in {

        my @quelle = @_;
        my @ziel;
        my ($elem, %schonda);

        @ziel = grep { !$schonda{$_}++ } @quelle;

        return @ziel;
}#in

##################################################################
# loescht aus einem array ein Element 
##################################################################

sub del_element{
    my @array = @{$_[0]};
    my $str   = $_[1];
    @array = grep !/^$str$/, @array;
    return @array;
}

#################################################################
# sub in (@array) entfernt aus einem array alle Doppleten
# und gibt die neue Liste zurueck
#################################################################

sub get_a_minus_b_2{

    my @a = @{$_[0]};
    my @b = @{$_[1]};

    my (@diff,%hash);
    
    foreach (@a) {
	    
	$hash{$_}++;
    }

    foreach (@b) {
	
	if ( exists($hash{$_}) ) { $hash{$_}--; }
    }

    foreach (@a) {
	
	if ( $hash{$_} == 1 ) { push(@diff,$_); }
    }    
    
    return @diff;
}#get_a_minus_b2

# ------------------
# sorting for number
# ------------------

sub NumberSort {
    if($a < $b)
    { return -1; }
    elsif($a == $b)
    { return 0; }
    else
    { return 1; }
}

############## MODUL- ENDE ##############################################
1;
