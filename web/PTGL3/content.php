<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<meta name="author" content="">
	<link rel="shortcut icon" href="../../docs-assets/ico/favicon.png">

	<title>PTGL 2.0</title>


	<!-- Mobile viewport optimized -->
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scale=1.0, user-scalable=no"/>


	<!-- Bootstrap CSS -->
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="css/bootstrap-glyphicons.css">


	<!-- Custom CSS -->
	<link rel="stylesheet" type="text/css" href="css/styles.css">
	<link rel="stylesheet" href="css/font-awesome.css"/>

	 <script src="//ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js"></script>
	<!-- Include Modernizr in the head, before any other JS -->
	<script src="js/modernizr-2.6.2.min.js"></script>

				<!-- Live Search for PDB IDs -->
	<script src="js/livesearch.js" type="text/javascript"></script>

        <script src="chart_js/Chart.js"></script>

</head>


<?php

  // should start in HTML part of document, NOT inside <script> tags!
  function prepare_plot_js_canvas($canvas_html_id = "mycanvas", $context_name = "ctx", $width = 700, $height = 400) {
    $res = "";
    $res .= '<canvas id="' . $canvas_html_id . '" width="' . $width . '" height="' . $height . '"></canvas>' . "\n";
    $res .=  '<script>' . "\n";
    $res .= 'var ' . $context_name . ' = document.getElementById("' . $canvas_html_id . '").getContext("2d");' . "\n";
    $res .= "</script>\n";
    return $res;
  }

  // should be called in <script> tags , creates a line chart
  function do_plot_js($context_name = "ctx", $dataset_var_name = "data", $result_assign_var_name = "myChart") {
    $res = 'var ' . $result_assign_var_name . ' = new Chart(' . $context_name . ').Line(' . $dataset_var_name . ');' . "\n";
    return $res;
  }

  // should be called in <script> tags , creates a line chart
  function do_plot_js_line($context_name = "ctx", $dataset_var_name = "data", $result_assign_var_name = "myChart") {
    return do_plot_js($context_name, $dataset_var_name, $result_assign_var_name);
  }

  // creates a bar chart
  function do_plot_js_bar($context_name = "ctx", $dataset_var_name = "data", $result_assign_var_name = "myChart") {
    $res = 'var ' . $result_assign_var_name . ' = new Chart(' . $context_name . ').Bar(' . $dataset_var_name . ');' . "\n";
    return $res;
  }
  
  function get_plot_js_formatted_data($labels = array(), $datasets = array()) {
  
    $format_commands = array();

    $fc0 = '';
    $fc0 .= '       fillColor : "rgba(151,187,205,0.5)",' . "\n";
    $fc0 .= '       strokeColor : "rgba(151,187,205,1)",' . "\n";
    $fc0 .= '       pointColor : "rgba(151,187,205,1)",' . "\n";
    $fc0 .= '       pointStrokeColor : "#fff",' . "\n";

    array_push($format_commands, $fc0);

    $fc1 = '';
    $fc1 .= '       fillColor : "rgba(220,220,220,0.5)",' . "\n";
    $fc1 .= '       strokeColor : "rgba(220,220,220,1)",' . "\n";
    $fc1 .= '       pointColor : "rgba(220,220,220,1)",' . "\n";
    $fc1 .= '       pointStrokeColor : "#fff",' . "\n";
    
    array_push($format_commands, $fc1);


    $res = "{" . "\n";     // open var name

    // print labels row
    $res .= "labels : [";

    for($i = 0; $i < count($labels); $i++) {
      $label = $labels[$i];
      $res .= '"' . $label . '"';
      if($i < (count($labels) - 1)) {
	$res .= ',';
      }
    }
    $res .= "]," . "\n";     // close labels

    // print datasets
    $res .= "datasets : [" . "\n";

    for($i = 0; $i < count($datasets); $i++) {
      
      $dataset = $datasets[$i];

      $res .= "    {" . "\n";   // open dataset

      if($i < count($format_commands)) {
	$res .= $format_commands[$i];
      }
      else {
	$res .= $format_commands[0]; // use the first if not enough different options
      }

      // format color stuff
      //$res .= '       fillColor : "rgba(151,187,205,0.5)",' . "\n";
      //$res .= '       strokeColor : "rgba(151,187,205,1)",' . "\n";
      //$res .= '       pointColor : "rgba(151,187,205,1)",' . "\n";
      //$res .= '       pointStrokeColor : "#fff",' . "\n";

      // the data itself
      $res .= '       data : [';
      for($j = 0; $j < count($dataset); $j++) {
	$date = $dataset[$j];
	$res .= $date;
	if($j < (count($dataset) - 1)) {
	  $res .= ',';
	}
      }
      $res .= ']' . "\n";

      $res .= "    }" . "\n";   // close dataset

      if($i < (count($datasets) - 1)) {
	$res .= "," . "\n";
      }
    }

    $res .= "  ]" . "\n"; // close datasets
    $res .= "}" . "\n"; // close var name
    
    return $res;
  }
  
  
  function get_lineplot_code($unique_plot_id, $labels = array(), $datasets = array()) {
    
    $ret_code = "";
    
    $canvas_name = "mycanvas" . $unique_plot_id;
    $ctx_name = "ctx" . $unique_plot_id;
    $data_name = "data" . $unique_plot_id;
    $chart_name = "mychart" . $unique_plot_id;

    $ret_code .= prepare_plot_js_canvas($canvas_name, $ctx_name, 700, 400);
    
    $ret_code .= "<script>\n";
    $ret_code .= "var " . $data_name . " = ";
    $ret_code .= get_plot_js_formatted_data($labels, $datasets);
    
    // start the actual plot
    $ret_code .= do_plot_js($ctx_name, $data_name, $chart_name);
    $ret_code .= "</script>\n";
    
    return $ret_code;
  }

?>

<body id="customBackground">
	<noscript>
		<META HTTP-EQUIV="Refresh" CONTENT="0;URL=errorJS.php">
	</noscript>
	<div class="wrapper">

		<?php include('navbar.php'); ?>

	<div class="container" id="contentText">
		<h2> Current holdings </h2>
		<br>

		<h4><font color="red">98,161</font> PDB files</h4>
		<h4><font color="red">274,459</font> Protein chains</h4>
		<h4><font color="red">5,088,843</font> Secondary structure elements (SSEs)</h4>
		<h4><font color="red">4,964,550</font> 3D contacts between SSEs</h4>


		<div class="table-responsive" id="contentTable">
			<table class="table table-condensed table-hover borderless">
				<tr>
					<th class="tablecenter">Graph-type</th>
					<th class="tablecenter">Number of graphs</th>
					<th class="tablecenter">Graphs containing beta-barrel</th>
					<th class="tablecenter">Number of SSEs in graphs</th>
				</tr>
				
				<tr class="tablecenter">
					<th>Alpha</th>
					<td>274,454</td>
					<td>0</td>
					<td>2,357,932</td>

				</tr>
				
				<tr class="tablecenter">
					<th>Beta</th>
					<td>274,452</td>
					<td>169,560</td>
					<td>2,094,599</td>
				</tr>
				
				<tr class="tablecenter">
					<th>Alpha-Beta</th>
					<td>274,459</td>
					<td>169,542</td>
					<td>4,452,531</td>
				</tr>
				
				<tr class="tablecenter">
					<th>Alphalig</th>
					<td>274,454</td>
					<td>0</td>
					<td>2,988,890</td>

				</tr>
				
				<tr class="tablecenter">
					<th>Betalig</th>
					<td>274,452</td>
					<td>169,560</td>
					<td>2,723,377</td>
				</tr>
				
				<tr class="tablecenter">
					<th>Alpha-Betalig</th>
					<td>274,459</td>
					<td>169,542</td>
					<td>5,099,843</td>
				</tr>
				
			</table>
		</div><!-- end table-responsive -->
		
<?php
		
		  $labels = array("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");        // mysql order weekdays
		  $datasets = array();
		  $dataset1 = array(11, 23, 3, 5, 24, 44, 7);
		  array_push($datasets, $dataset1);
		  $code = get_lineplot_code("1", $labels, $datasets);		  
		  echo $code;
		  
?>
		
		
</div><!-- end container and contentText -->
</div><!-- end wrapper -->


<?php include('footer.php'); ?>
	<!-- All Javascript at the bottom of the page for faster page loading -->
	<!-- also needed for the dropdown menus etc. ... -->

	<!-- First try for the online version of jQuery-->
	<script src="http://code.jquery.com/jquery.js"></script>

	<!-- If no online access, fallback to our hardcoded version of jQuery -->
	<script>window.jQuery || document.write('<script src="js/jquery-1.8.2.min.js"><\/script>')</script>

	<!-- Bootstrap JS -->
	<script src="js/bootstrap.min.js"></script>

	<!-- Custom JS -->
	<script src="js/script.js"></script>
</body>
</html>
