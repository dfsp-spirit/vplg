<?php
header('Content-Type: text/plain');

// Call file with ?csv=1 parameter to get CSV-like output.
// e.g. $basURL/getGraphDegreeDistribution.php?csv=1
// otherwise output is more readable


ini_set('display_errors', 0);
ini_set('display_startup_errors', 0);
ini_set('log_errors', TRUE);
error_reporting(E_ERROR);

// get config values
include('./backend/config.php');

if($DEBUG){
	ini_set('display_errors', 1);
	ini_set('display_startup_errors', 1);
	ini_set('log_errors', TRUE);
	error_reporting(E_ALL);
}

$asCSV = false;
if(isset($_GET["csv"])){
    if($_GET["csv"] === "1"){
        $asCSV = true;
    }
}


$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER . " password=" . $DB_PASSWORD;
$db = pg_connect($conn_string);

//QUERYMANIA
// Select the maximum degree, the count of the 0-degree and the remaining degrees for
// each contact types (SSE interchain contacts, SSE complex contacts and chain complex contacts)

$query_contact_max = "SELECT MAX(y.count) 
                    FROM 
                        (SELECT c.sse1 as sse, COUNT(c.sse1) as count
                    FROM plcc_contact c
                    GROUP BY sse
                    UNION

                    SELECT c.sse2 as sse, COUNT(c.sse2) as count
                    FROM plcc_contact c
                    GROUP BY sse) y";

$query_sse_complex_max = "SELECT MAX(y.count) 
                        FROM 
                        (SELECT c.sse1 as sse, COUNT(c.sse1) as count
                        FROM plcc_ssecontact_complexgraph c
                        GROUP BY sse
                        UNION

                        SELECT c.sse2 as sse, COUNT(c.sse2) as count
                        FROM plcc_ssecontact_complexgraph c
                        GROUP BY sse) y";

$query_chain_complex_max = "SELECT MAX(y.count) 
                        FROM 
                        (SELECT c.chain1 as chain, COUNT(c.chain1) as count
                        FROM plcc_complex_contact c
                        GROUP BY chain
                        UNION

                        SELECT c.chain2 as chain, COUNT(c.chain2) as count
                        FROM plcc_complex_contact c
                        GROUP BY chain) y";


$query_contact_nulldeg = "SELECT COUNT(s.sse_id) 
                                FROM   plcc_sse s
                                WHERE s.sse_id NOT IN (SELECT sse1 as sse 
                                                        FROM plcc_contact
                                                        UNION
                                                        SELECT sse2 as sse 
                                                        FROM plcc_contact)";

$query_sse_complex_nulldeg = "SELECT COUNT(s.sse_id) 
                                FROM   plcc_sse s
                                WHERE s.sse_id NOT IN (SELECT sse1 as sse 
                                                        FROM plcc_ssecontact_complexgraph
                                                        UNION
                                                        SELECT sse2 as sse 
                                                        FROM plcc_ssecontact_complexgraph)";

$query_chain_complex_nulldeg = "SELECT COUNT(c.chain_id) 
                                FROM   plcc_chain c
                                WHERE c.chain_id NOT IN (SELECT chain1 as chain 
                                                        FROM plcc_complex_contact
                                                        UNION
                                                        SELECT chain2 as chain 
                                                        FROM plcc_complex_contact)";

$query_contact = "SELECT c.sse1 as sse, COUNT(c.sse1) as count
                    FROM plcc_contact c
                    GROUP BY sse
                    UNION

                    SELECT c.sse2 as sse, COUNT(c.sse2) as count
                    FROM plcc_contact c
                    GROUP BY sse
                  ORDER BY count DESC";

$query_sse_complex = "SELECT c.sse1 as sse, COUNT(c.sse1) as count
                        FROM plcc_ssecontact_complexgraph c
                        GROUP BY sse
                        UNION

                        SELECT c.sse2 as sse, COUNT(c.sse2) as count
                        FROM plcc_ssecontact_complexgraph c
                        GROUP BY sse
                      ORDER BY count DESC";

$query_chain_complex = "SELECT c.chain1 as chain, COUNT(c.chain1) as count
                        FROM plcc_complex_contact c
                        GROUP BY chain
                        UNION

                        SELECT c.chain2 as chain, COUNT(c.chain2) as count
                        FROM plcc_complex_contact c
                        GROUP BY chain
                      ORDER BY count DESC";

// GET MAX DEGREE
$res_contact_max = pg_query($db, $query_contact_max);
$contact_max_deg = pg_fetch_array($res_contact_max, NULL, PGSQL_ASSOC);
$contact_max_deg = $contact_max_deg["max"];

$res_sse_complex_max = pg_query($db, $query_sse_complex_max);
$sse_complex_max_deg = pg_fetch_array($res_sse_complex_max, NULL, PGSQL_ASSOC);
$sse_complex_max_deg = $sse_complex_max_deg["max"];        

$res_chain_complex_max = pg_query($db, $query_chain_complex_max);
$chain_complex_max_deg = pg_fetch_array($res_chain_complex_max, NULL, PGSQL_ASSOC);
$chain_complex_max_deg = $chain_complex_max_deg["max"];


// GET DEGREES OF ZERO
$res_contact_nulldeg = pg_query($db, $query_contact_nulldeg);
$contact_nulldeg = pg_fetch_array($res_contact_nulldeg, NULL, PGSQL_ASSOC);
$contact_nulldeg = $contact_nulldeg["count"];

$res_sse_complex_nulldeg = pg_query($db, $query_sse_complex_nulldeg);
$sse_complex_nulldeg = pg_fetch_array($res_sse_complex_nulldeg, NULL, PGSQL_ASSOC);
$sse_complex_nulldeg = $sse_complex_nulldeg["count"];

$res_chain_complex_nulldeg = pg_query($db, $query_chain_complex_nulldeg);
$chain_complex_nulldeg = pg_fetch_array($res_chain_complex_nulldeg, NULL, PGSQL_ASSOC);
$chain_complex_nulldeg = $chain_complex_nulldeg["count"];

// GET remaining degrees
$res_contact = pg_query($db, $query_contact);
$res_sse_complex = pg_query($db, $query_sse_complex);
$res_chain_complex = pg_query($db, $query_chain_complex);

// generate arrays for "remaining" degrees
$dist_contact = array();
$dist_sse_complex = array();
$dist_chain_complex = array();

for($i = 1; $i <= $contact_max_deg; $i++){
    $dist_contact[$i] = 0;
}

for($i = 1; $i <= $sse_complex_max_deg; $i++){
    $dist_sse_complex[$i] = 0;
}

for($i = 1; $i <= $chain_complex_max_deg; $i++){
    $dist_chain_complex[$i] = 0;
}


while ($arr = pg_fetch_array($res_contact, NULL, PGSQL_ASSOC)) {
    $degree = $arr["count"];
    $dist_contact[$degree]++;
}

while ($arr = pg_fetch_array($res_sse_complex, NULL, PGSQL_ASSOC)) {
    $degree = $arr["count"];
    $dist_sse_complex[$degree]++;
}

while ($arr = pg_fetch_array($res_chain_complex, NULL, PGSQL_ASSOC)) {
    $degree = $arr["count"];
    $dist_chain_complex[$degree]++;
}


pg_close($db);

// just to shorten code a bit
// calculate percentage
function perc($sum, $part) {
    return round(($part/($sum/100)),3);
}

// sums of 0-degrees and remaining degrees
$contact_sum = (array_sum($dist_contact) + $contact_nulldeg);
$sse_complex_sum = (array_sum($dist_sse_complex) + $sse_complex_nulldeg);
$chain_complex_sum = (array_sum($dist_chain_complex) + $chain_complex_nulldeg);


echo "// SSE contact distribution\n";
if($asCSV){
    echo "Degree;Count;Percentage\n";
    echo "0;" . $contact_nulldeg . ";" . perc($contact_sum, $contact_nulldeg)  . "\n";
    for($i = 1; $i <= count($dist_contact); $i++ ){
        echo "" . $i . ";" . $dist_contact[$i] . ";" . perc($contact_sum, $dist_contact[$i]) . "\n";
    }
    echo "SUM;" . $contact_sum . ";100\n";
} else {
    echo "Degree\tCount\tPercentage\n";
    echo "0\t" . $contact_nulldeg . "\t" . perc($contact_sum, $contact_nulldeg) . "\n";
    for($i = 1; $i <= count($dist_contact); $i++ ){
        echo "" . $i . "\t" . $dist_contact[$i] . "\t" . perc($contact_sum, $dist_contact[$i]) . "\n";
    }
    echo "\n";
    echo "SUM\t" . $contact_sum . "\t100\n";
    echo "-----------------------end";
}
echo "\n\n\n";


echo "// SSE complex contact distribution\n";
if($asCSV){
    echo "Degree;Count;Percentage\n";
    echo "0;" . $sse_complex_nulldeg . ";" . perc($sse_complex_sum, $sse_complex_nulldeg)  . "\n";
    for($i = 1; $i <= count($dist_sse_complex); $i++ ){
        echo "" . $i . ";" . $dist_sse_complex[$i] . ";" . perc($sse_complex_sum, $dist_sse_complex[$i]) .  "\n";
    }
    echo "SUM;" . $sse_complex_sum . ";100\n";
} else {
    echo "Degree\tCount\tPercentage\n";
    echo "0\t" . $sse_complex_nulldeg . "\t" . perc($sse_complex_sum, $sse_complex_nulldeg) . "\n";
    for($i = 1; $i <= count($dist_sse_complex); $i++ ){
        echo "" . $i . "\t" . $dist_sse_complex[$i] . "\t" . perc($sse_complex_sum, $dist_sse_complex[$i]) . "\n";
    }
    echo "\n";
    echo "SUM\t" . $sse_complex_sum . "\t100\n";
    echo "-----------------------end";
}
echo "\n\n\n";


echo "// Chain complex contact distribution\n";
if($asCSV){
    echo "Degree;Count;Percentage\n";
    echo "0;" . $chain_complex_nulldeg . ";" . perc($chain_complex_sum, $chain_complex_nulldeg)  . "\n";
    for($i = 1; $i <= count($dist_chain_complex); $i++ ){
        echo "" . $i . ";" . $dist_chain_complex[$i] . ";" . perc($chain_complex_sum, $dist_chain_complex[$i]) . "\n";
    }   
    echo "SUM;" . $chain_complex_sum . ";100\n";
} else {
    echo "Degree\tCount\tPercentage\n";
    echo "0\t" . $chain_complex_nulldeg . "\t" . perc($chain_complex_sum, $chain_complex_nulldeg) . "\n";
    for($i = 1; $i <= count($dist_chain_complex); $i++ ){
        echo "" . $i . "\t" . $dist_chain_complex[$i] . "\t" . perc($chain_complex_sum, $dist_chain_complex[$i]) . "\n";
    }
    echo "\n";
    echo "SUM\t" . $chain_complex_sum . "\t100\n";
    echo "-----------------------end";
}
