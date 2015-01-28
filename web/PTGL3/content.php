<!DOCTYPE html>
<?php 
include('./backend/config.php'); 

$title = "Content overview and statistics";
$title = $SITE_TITLE.$TITLE_SPACER.$title;
?>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="PTGL database content">
	<meta http-equiv="Cache-control" content="public">
	<meta name="author" content="">
	<link rel="shortcut icon" href="favicon.ico?v=1.0" type="image/x-icon" />

	<title><?php echo $title; ?></title>


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

        <script src="chart_js/Chart.min.js"></script>

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
    $res = 'var ' . $result_assign_var_name . ' = new Chart(' . $context_name . ').Bar(' . $dataset_var_name . ', { scaleBeginAtZero: true, scaleShowGridLines : true }' . ');' . "\n";
    return $res;
  }
  
  // creates a pe chart
  function do_plot_js_pie($context_name = "ctx", $dataset_var_name = "data", $result_assign_var_name = "myChart") {
    $res = 'var ' . $result_assign_var_name . ' = new Chart(' . $context_name . ').Pie(' . $dataset_var_name . ', { tooltipTemplate: "<%if (label){%><%=label%>: <%}%><%= value %> f", animation: false }' . ');' . "\n";
    //$res .= 'var legend = ' . $result_assign_var_name . '.generateLegend();' . "\n";
    //$res .= '$("#legend").html(legend);' . "\n";
    return $res;
  }
  
  // creates a doughnut chart
  function do_plot_js_doughnut($context_name = "ctx", $dataset_var_name = "data", $result_assign_var_name = "myChart") {
    $res = 'var ' . $result_assign_var_name . ' = new Chart(' . $context_name . ').Doughnut(' . $dataset_var_name . ', { tooltipTemplate: "<%if (label){%><%=label%>: <%}%><%= value %> f", animation: false }' . ');' . "\n";
    //$res .= 'var legend = ' . $result_assign_var_name . '.generateLegend();' . "\n";
    //$res .= '$("#legend").html(legend);' . "\n";
    return $res;
  }
  
  /*
  Returns something like this (see http://www.chartjs.org/docs/#doughnut-pie-chart-introduction):
  
  var data = [
    {
        value: 300,
        color:"#F7464A",
        highlight: "#FF5A5E",
        label: "Red"
    },
    {
        value: 50,
        color: "#46BFBD",
        highlight: "#5AD3D1",
        label: "Green"
    },
    {
        value: 100,
        color: "#FDB45C",
        highlight: "#FFC870",
        label: "Yellow"
    }
]

  */
  function get_plot_js_formatted_data_pie_doughnut($labels = array(), $data = array(), $colors = array("#F7464A", "#46BFBD" , "#FDB45C"), $highlights = array("#FF5A5E", "#5AD3D1" , "#FFC870")) {
    $res = "[" . "\n";     // open var name


    for($i = 0; $i < count($data); $i++) {
      $res .= "{\n";
      $res .= "value: " . $data[$i] . ",\n";
      
      $color_index = $i;
      if($color_index >= count($colors)) {
        $color_index = $color_index % count($colors);
      }
      
      $highlight_index = $i;
      if($highlight_index >= count($highlights)) {
        $highlight_index = $highlight_index % count($highlights);
      }
      
      $res .= "color: " . '"' . $colors[$color_index] . '"' . ",\n";
      $res .= "highlight: " . '"' . $highlights[$highlight_index] . '"' . ",\n";
      $res .= "label :" . '"' . $labels[$i] . '"' . "\n";
      $res .= "}\n";
      
      if($i < (count($data) - 1)) {
	$res .= ',';
      }
    }
    $res .= "]" . "\n";     // close everything

    return $res;
  }
  
  
  /*
  Returns something like this (see http://www.chartjs.org/docs/#bar-chart-introduction):
  
  
  var data = {
    labels: ["January", "February", "March", "April", "May", "June", "July"],
    datasets: [
        {
            label: "My First dataset",
            fillColor: "rgba(220,220,220,0.5)",
            strokeColor: "rgba(220,220,220,0.8)",
            highlightFill: "rgba(220,220,220,0.75)",
            highlightStroke: "rgba(220,220,220,1)",
            data: [65, 59, 80, 81, 56, 55, 40]
        },
        {
            label: "My Second dataset",
            fillColor: "rgba(151,187,205,0.5)",
            strokeColor: "rgba(151,187,205,0.8)",
            highlightFill: "rgba(151,187,205,0.75)",
            highlightStroke: "rgba(151,187,205,1)",
            data: [28, 48, 40, 19, 86, 27, 90]
        }
    ]
};  
  */
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
  
  
  function get_plot_code($unique_plot_id, $plot_type = "line", $labels = array(), $datasets = array()) {
    
    $ret_code = "";
    
    $canvas_name = "mycanvas" . $unique_plot_id;
    $ctx_name = "ctx" . $unique_plot_id;
    $data_name = "data" . $unique_plot_id;
    $chart_name = "mychart" . $unique_plot_id;

    $ret_code .= prepare_plot_js_canvas($canvas_name, $ctx_name, 700, 400);
    
    $ret_code .= "<script>\n";
    $ret_code .= "var " . $data_name . " = ";
    
    
    // start the actual plot
    if($plot_type == "bar") {
      $ret_code .= get_plot_js_formatted_data($labels, $datasets);
      $ret_code .= do_plot_js_bar($ctx_name, $data_name, $chart_name);
    }
    else if($plot_type == "pie") {
      $ret_code .= get_plot_js_formatted_data_pie_doughnut($labels, $datasets[0]);
      $ret_code .= do_plot_js_pie($ctx_name, $data_name, $chart_name);
    }
    else if($plot_type == "doughnut") {
      $ret_code .= get_plot_js_formatted_data_pie_doughnut($labels, $datasets[0]);
      $ret_code .= do_plot_js_doughnut($ctx_name, $data_name, $chart_name);
    }
    else if($plot_type == "line") {
      $ret_code .= get_plot_js_formatted_data($labels, $datasets);
      $ret_code .= do_plot_js_line($ctx_name, $data_name, $chart_name);
    }
    else {	// assume "line"
      $ret_code .= get_plot_js_formatted_data($labels, $datasets);
      $ret_code .= do_plot_js_line($ctx_name, $data_name, $chart_name);
    }
    $ret_code .= "</script>\n";
    
    return $ret_code;
  }

?>


<?php

      // ------------------------- data for the plots ---------------------------
      
    /*
    $last_update_month_and_year = "January 2015";
      
    $num_pdb_files = 98161;
    $num_protein_chains = 274459;
    $num_sses = 5088843;
    $num_intrachain_contacts = 4964550;
    
    $sses_in_graphtypes_labels = array("Alpha", "Beta", "Alpha-Beta", "Alphalig", "Betalig", "Alpha-Betalig");
    $sses_in_graphtype_alpha = array("Alpha", 247454, 0, 2357932);
    $sses_in_graphtype_beta = array("Beta", 247452, 169500, 2094599);
    $sses_in_graphtype_albe = array("Alpha-Beta", 247459, 169542, 4452531);
    $sses_in_graphtype_alphalig = array("Alphalig", 247454, 0, 2988890);
    $sses_in_graphtype_betalig = array("Betalig", 247452, 169560, 2723377);
    $sses_in_graphtype_albelig = array("Alpha-Betalig", 247459, 169542, 5099843);
  
    
    $sses_by_type_labels = array("Helix", "Strand", "Ligand");
    $sses_by_type = array(2357932, 2094599, 636312);
    
    $total_contacts_by_type_labels = array("Mixed", "Parallel", "Antiparallel", "Ligand");
    $total_contacts_by_type = array(1232714, 776965, 1553370, 1391501);
    
    $protein_functions_labels = array("Transcription", "Unknown", "Immune system", "Lyase", "Oxidureductase", "Transferase", "Hydrolase");
    $protein_functions = array(2302, 2394, 2609, 3257, 8892, 11330, 14696);
    
    $source_organisms_labels = array("Bos taurus", "Saccharomyces cerevisia", "Mus musculus", "Thermus thermophilus", "Unknown", "Escherichia coli", "Homo sapiens");
    $source_organisms = array(5219, 8822, 9349, 11464, 19161, 21117, 53667);
    */
    
    include('./temp_data/content_data.php'); 




  $table_data = array();      
  array_push($table_data, $sses_in_graphtype_alpha);
  array_push($table_data, $sses_in_graphtype_beta);
  array_push($table_data, $sses_in_graphtype_albe);
  array_push($table_data, $sses_in_graphtype_alphalig);
  array_push($table_data, $sses_in_graphtype_betalig);
  array_push($table_data, $sses_in_graphtype_albelig);

?>


<body id="customBackground">
	<noscript>
		<META HTTP-EQUIV="Refresh" CONTENT="0;URL=errorJS.php">
	</noscript>
	<div class="wrapper">

		<?php include('navbar.php'); ?>

	<div class="container" id="contentText">
		<h2> Current holdings </h2>
		
		
		<div id="PageIntro">
		<div class="container" id="pageintro">
		This page gives statistics on the current holdings of this database. The 3D atom data used to compute the protein graphs was retrieved from the RCSB PDB in <?php echo $last_update_month_and_year; ?>.
		</div><!-- end container-->
		</div><!-- end Home -->
		
		<br>

		<h4><?php echo $num_pdb_files; ?> PDB files</h4>
		<h4><?php echo $num_protein_chains; ?> Protein chains</h4>
		<h4><?php echo $num_sses; ?> Secondary structure elements (SSEs)</h4>
		<h4><?php echo $num_intrachain_contacts; ?> 3D contacts between SSEs of the same chain</h4>


		<div class="table-responsive" id="contentTable">
			<table class="table table-condensed table-hover borderless">
				<tr>
					<th class="tablecenter">Graph type</th>
					<th class="tablecenter">Number of graphs</th>
					<th class="tablecenter">Graphs containing beta-barrels</th>
					<th class="tablecenter">Number of SSEs in graphs</th>
				</tr>
				
				<!--
				<tr class="tablecenter">
					<th>Alpha</th>
					<td>274,454</td>
					<td>0</td>
					<td>2,357,932</td>
				</tr>
				-->			
	
				
				<?php
				  for($i = 0; $i < sizeof($table_data); $i++) {
				     echo '<tr class="tablecenter">' . "\n";
				     for($j = 0; $j < sizeof($table_data[$i]); $j++) {
				       if($j == 0) {
				         echo '<th>' . $table_data[$i][$j] . '</th>' . "\n";
				       } else {
				         echo '<td>' . $table_data[$i][$j] . '</td>' . "\n";
				       }
				     }
				     echo '</tr>' . "\n\n";
				  }
				?>
				
			</table>
		</div><!-- end table-responsive -->
		
<?php
                  
                  
		  // ------------------------------ plots -----------------------------------
		  
		  
		  
		  
		  //
		
		  // ----- SSEs in graphs -----
		  //$sses_in_graphtypes_labels = array("Alpha", "Beta", "Alpha-Beta", "Alphalig", "Betalig", "Alpha-Betalig");
		  $datasets = array();
		  $sses_in_graphtypes = array($table_data[0][3], $table_data[1][3], $table_data[2][3], $table_data[3][3], $table_data[4][3], $table_data[5][3]);
		  array_push($datasets, $sses_in_graphtypes);
		  $plot_type = "bar"; // "bar", "pie", "doughnut" or "line" are supported atm
		  //$plot_type = "pie";
		  
		  $code = get_plot_code("1", $plot_type, $sses_in_graphtypes_labels, $datasets);		  
		  
		  echo "<h4>Number of SSEs in graphs</h4>\n" . $code . "\n\n";
		  
		  
		  echo "<br/><br/><br/><br/>\n";
		  
		  // ----- total SSEs by type -----
		  
		  //$sses_by_type_labels = array("Helix", "Strand", "Ligand");
		  $datasets = array();
		  //
		  array_push($datasets, $sses_by_type);
		  
		  $plot_type = "bar";		  
		  $code = get_plot_code("2", $plot_type, $sses_by_type_labels, $datasets);		  
		  
		  echo "<h4>Number of SSEs by type</h4>\n" . $code . "\n\n";
		  
		  
		  
		  echo "<br/><br/><br/><br/>\n";
		  
		  
		  // ----- total contacts by type -----
		  
		  //$total_contacts_by_type_labels = array("Mixed", "Parallel", "Antiparallel", "Ligand");
		  $datasets = array();
		  //$total_contacts_by_type = array(1232714, 776965, 1553370, 1391501);
		  array_push($datasets, $total_contacts_by_type);
		  
		  $plot_type = "bar";		  
		  $code = get_plot_code("3", $plot_type, $total_contacts_by_type_labels, $datasets);		  
		  
		  echo "<h4>Number of contacts by type</h4>\n" . $code . "\n\n";
		  
		  echo "<br/><br/><br/><br/>\n";
		  
		  
		  // ----- protein functions -----
		  
		  //$protein_functions_labels = array("Transcription", "Unknown", "Immune system", "Lyase", "Oxidureductase", "Transferase", "Hydrolase");
		  $datasets = array();
		  //$protein_functions = array(2302, 2394, 2609, 3257, 8892, 11330, 14696);
		  array_push($datasets, $protein_functions);
		  
		  $plot_type = "bar";		  
		  $code = get_plot_code("4", $plot_type, $protein_functions_labels, $datasets);		  
		  
		  echo "<h4>Most common protein functions</h4>\n" . $code . "\n\n";
		  
		  echo "<br/><br/><br/><br/>\n";
		  
		  
		  // ----- source organism -----
		  
		  //$source_organisms_labels = array("Bos taurus", "Saccharomyces cerevisia", "Mus musculus", "Thermus thermophilus", "Unknown", "Escherichia coli", "Homo sapiens");
		  $datasets = array();
		  //$source_organisms = array(5219, 8822, 9349, 11464, 19161, 21117, 53667);
		  array_push($datasets, $source_organisms);
		  
		  $plot_type = "bar";		  
		  $code = get_plot_code("5", $plot_type, $source_organisms_labels, $datasets);		  
		  
		  echo "<h4>Most common source organisms</h4>\n" . $code . "\n\n";
		  
		  echo "<br/><br/><br/><br/>\n";
		  
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
