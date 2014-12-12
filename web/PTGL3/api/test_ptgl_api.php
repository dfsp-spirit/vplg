<html>
	<head>
		<title>PTGL API test</title>
	</head>
	
	<body>
	<h2>PTGL API test</h2>
	<p>
	<?php
	
	$url_pg = 'http://127.0.0.1/api/index.php/pg/7tim/A/albe/json';
	$url_linnot = 'http://127.0.0.1/api/index.php/linnot/7tim/A/albe/0/adj';
	$url_linnot_broken = 'http://127.0.0.1/api/index.php/linnot/7tim/A/albe/0/aj';
	
	//echo "<h3>Test 1: file_get_contents</h3>\n";
	//$linnot = json_decode(file_get_contents($url_linnot));
	//echo "result: " . $linnot;
	
	
	echo "<h3>Test 2: curl OK</h3>\n";
	$curl = curl_init();
    // Optional Authentication:
    //curl_setopt($curl, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
    //curl_setopt($curl, CURLOPT_USERPWD, "username:password");

    curl_setopt($curl, CURLOPT_URL, $url_linnot);
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
	curl_setopt($curl, CURLOPT_FAILONERROR, 1);

    $result = curl_exec($curl);
	
	if(curl_errno($curl)) {
		echo 'error: ' . curl_error($curl);
	}
	else {
        echo "result: " . json_decode($result);
	}
    
	
	echo "<h3>Test 3: curl error</h3>\n";
	curl_setopt($curl, CURLOPT_URL, $url_linnot_broken);
	$result = curl_exec($curl);
	
	if(curl_errno($curl)) {
		echo 'error:' . curl_error($curl);
	}
	else {
        echo "result: " . $result;
	}
	curl_close($curl);

	?>
    </p>

	</body>
</html>