#!/usr/bin/perl
# calculates euklidian distance between two points in 3-dimensional space
# written by spirit

if($#ARGV != 5) { die "USAGE: '$0 1x 1y 1z 2x 2y 2z'     where 1 and 2 are two 3D points defined by their x, y and z coordinates.\n"; }



my $x1 = shift;# || die "ERROR 1\n";
my $x2 = shift;# || die "ERROR 2\n";
my $x3 = shift;# || die "ERROR 3\n";
my $y1 = shift;# || die "ERROR 4\n";
my $y2 = shift;# || die "ERROR 5\n";
my $y3 = shift;# || die "ERROR 6\n";

my $d1 = $x1 - $y1;
my $d2 = $x2 - $y2;
my $d3 = $x3 - $y3;

my $res = sqrt($d1*$d1 + $d2*$d2 + $d3*$d3);

print "distance of ($x1,$x2,$x3) and ($y1,$y2,$y3) = $res.\n";


