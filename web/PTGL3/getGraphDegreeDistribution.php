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

$res_contact_max = pg_query($db, $query_contact_max);
$contact_max_deg = pg_fetch_array($res_contact_max, NULL, PGSQL_ASSOC);
$contact_max_deg = $contact_max_deg["max"];

$res_sse_complex_max = pg_query($db, $query_sse_complex_max);
$sse_complex_max_deg = pg_fetch_array($res_sse_complex_max, NULL, PGSQL_ASSOC);
$sse_complex_max_deg = $sse_complex_max_deg["max"];        

$res_chain_complex_max = pg_query($db, $query_chain_complex_max);
$chain_complex_max_deg = pg_fetch_array($res_chain_complex_max, NULL, PGSQL_ASSOC);
$chain_complex_max_deg = $chain_complex_max_deg["max"]; 

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


$res_contact = pg_query($db, $query_contact);
$res_sse_complex = pg_query($db, $query_sse_complex);
$res_chain_complex = pg_query($db, $query_chain_complex);


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


function perc($sum, $part) {
    return round(($part/($sum/100)),3);
}

echo "SSE contact distribution\n";
if($asCSV){
    echo "Degree;Count;Percentage\n";
    for($i = 1; $i <= count($dist_contact); $i++ ){
        echo "" . $i . ";" . $dist_contact[$i] . ";" . perc(array_sum($dist_contact), $dist_contact[$i]) . "\n";
    }    
} else {
    echo "Degree\tCount\tPercentage\n";
    for($i = 1; $i <= count($dist_contact); $i++ ){
        echo "" . $i . "\t" . $dist_contact[$i] . "\t" . perc(array_sum($dist_contact), $dist_contact[$i]) . "\n";
    }

    echo "-----------------------end";
}
echo "\n\n\n";


echo "SSE complex contact distribution\n";
if($asCSV){
    echo "Degree;Count;Percentage\n";
    for($i = 1; $i <= count($dist_sse_complex); $i++ ){
        echo "" . $i . ";" . $dist_sse_complex[$i] . ";" . perc(array_sum($dist_sse_complex), $dist_sse_complex[$i]) .  "\n";
    }    
} else {
    echo "Degree\tCount\tPercentage\n";
    for($i = 1; $i <= count($dist_sse_complex); $i++ ){
        echo "" . $i . "\t" . $dist_sse_complex[$i] . "\t" . perc(array_sum($dist_sse_complex), $dist_sse_complex[$i]) . "\n";
    }

    echo "-----------------------end";
}
echo "\n\n\n";


echo "Chain complex contact distribution\n";
if($asCSV){
    echo "Degree;Count;Percentage\n";
    for($i = 1; $i <= count($dist_chain_complex); $i++ ){
        echo "" . $i . ";" . $dist_chain_complex[$i] . ";" . perc(array_sum($dist_chain_complex), $dist_chain_complex[$i]) . "\n";
    }    
} else {
    echo "Degree\tCount\tPercentage\n";
    for($i = 1; $i <= count($dist_chain_complex); $i++ ){
        echo "" . $i . "\t" . $dist_chain_complex[$i] . "\t" . perc(array_sum($dist_chain_complex), $dist_chain_complex[$i]) . "\n";
    }

    echo "-----------------------end";
}




