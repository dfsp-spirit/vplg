<?php
/**
 * Step 1: Require the Slim Framework
 *
 * If you are not using Composer, you need to require the
 * Slim Framework and register its PSR-0 autoloader.
 *
 * If you are using Composer, you can skip this step.
 */
require 'Slim/Slim.php';

\Slim\Slim::registerAutoloader();

/**
 * Step 2: Instantiate a Slim application
 *
 * This example instantiates a Slim application using
 * its default settings. However, you will usually configure
 * your Slim application now by passing an associative array
 * of setting names and values into the application constructor.
 */
$app = new \Slim\Slim();
$app->setName('ptgl3api');

/**
 * Step 3: Define the Slim application routes
 *
 * Here we define several Slim application routes that respond
 * to appropriate HTTP request methods. In this example, the second
 * argument for `Slim::get`, `Slim::post`, `Slim::put`, `Slim::patch`, and `Slim::delete`
 * is an anonymous function.
 */

// GET route
$app->get(
    '/',
    function () {
        $template = <<<EOT
<!DOCTYPE html>
    <html>
        <head>
            <meta charset="utf-8"/>
            <title>PTGL API</title>
            <style>
                html,body,div,span,object,iframe,
                h1,h2,h3,h4,h5,h6,p,blockquote,pre,
                abbr,address,cite,code,
                del,dfn,em,img,ins,kbd,q,samp,
                small,strong,sub,sup,var,
                b,i,
                dl,dt,dd,ol,ul,li,
                fieldset,form,label,legend,
                table,caption,tbody,tfoot,thead,tr,th,td,
                article,aside,canvas,details,figcaption,figure,
                footer,header,hgroup,menu,nav,section,summary,
                time,mark,audio,video{margin:0;padding:0;border:0;outline:0;font-size:100%;vertical-align:baseline;background:transparent;}
                body{line-height:1;}
                article,aside,details,figcaption,figure,
                footer,header,hgroup,menu,nav,section{display:block;}
                nav ul{list-style:none;}
                blockquote,q{quotes:none;}
                blockquote:before,blockquote:after,
                q:before,q:after{content:'';content:none;}
                a{margin:0;padding:0;font-size:100%;vertical-align:baseline;background:transparent;}
                ins{background-color:#ff9;color:#000;text-decoration:none;}
                mark{background-color:#ff9;color:#000;font-style:italic;font-weight:bold;}
                del{text-decoration:line-through;}
                abbr[title],dfn[title]{border-bottom:1px dotted;cursor:help;}
                table{border-collapse:collapse;border-spacing:0;}
                hr{display:block;height:1px;border:0;border-top:1px solid #cccccc;margin:1em 0;padding:0;}
                input,select{vertical-align:middle;}
                html{ background: #EDEDED; height: 100%; }
                body{background:#FFF;margin:0 auto;min-height:100%;padding:0 30px;width:600px;color:#666;font:14px/23px Arial,Verdana,sans-serif;}
                h1,h2,h3,p,ul,ol,form,section{margin:0 0 20px 0;}
                h1{color:#333;font-size:20px;}
                h2,h3{color:#333;font-size:14px;}
                h3{margin:0;font-size:12px;font-weight:bold;}
                ul,ol{list-style-position:inside;color:#999;}
                ul{list-style-type:square;}
                code,kbd{background:#EEE;border:1px solid #DDD;border:1px solid #DDD;border-radius:4px;-moz-border-radius:4px;-webkit-border-radius:4px;padding:0 4px;color:#666;font-size:12px;}
                pre{background:#EEE;border:1px solid #DDD;border-radius:4px;-moz-border-radius:4px;-webkit-border-radius:4px;padding:5px 10px;color:#666;font-size:12px;}
                pre code{background:transparent;border:none;padding:0;}
                a{color:#70a23e;}
                header{padding: 30px 0;text-align:center;}
            </style>
        </head>
        <body>
            <header>
                <title>PTGL API</title>
            </header>
            <h1>Welcome to the PTGL 3.0 API</h1>
			
			<a href=#basics">Basics</a> | <a href="#addressing">Addressing data</a> | <a href="#examples">Usage Examples</a> | <a href="#metadata">Metadata queries</a> | <a href="#example_restclient_code">Example API client code</a> | <a href="#help">Help, comments</a> | <a href="#author">Author</a>
			
            <p>
			    <br>
                The PTGL Application Programming Interface (API) allows you to quickly retrieve data from the PTGL programmatically. This page explains
                how to use the API.
				<br><br>
				A typical use case is that you want more data than you are willing to download manually by clicking in your web browser. You may, for example, have a list of 250 PDB chains, and you want the albe protein graphs for all of them in GML format.
				<br><br>
				If you are not a programmer, you may prefer the <a href="../" target="_blank">HTML interface of the PTGL 3</a>.
                
            </p>
			<a name="basics"></a>
            <section>
                <h2>Basics</h2>
                This is a REST API, and you can query it with any HTTP client. There are specialized REST clients, but something like curl will do in principle. Of course, you can query this API with a programming language of your choice.
				We have <a href="#example_restclient_code">some example PHP code to access the API here</a>. 
				<br><br>
				All data is served in JavaScript Object Notation (JSON) format. For graph data, you can chose between Graph Modelling Language (GML) format and JSON format.				 								
            </section>
            <section>
                <h2>Data available via the API</h2>
                <p>
                You can retrieve protein graph (PG) and folding graph (FG) data in GML and JSON formats. You can also retrieve the linear notations (linnots) of a folding graph as a string. It is also possible to retrieve
				the graph images -- both the PG visualization and the 4 different linnot visualizations.
                </p>
                <p>
                The PTGL is a protein topology database which works
                mainly on the level of a protein chain: a protein graph includes all the secondary structure elements (SSEs) of a chain. (Which SSEs types
                are included is determined by the graph type, e.g., the alpha graph only contains alpha helices, while the alpha-beta graph contains both alpha helices and beta strands.)
                </p>
				<p>
				You may have guessed it, but all methods provided are GET. You cannot alter (insert, update, delete) any data on the server using this API.
				</p>
            </section>
			<a name="addressing"></a>
            <section>
                <h2>Addressing data</h2>
                <p>
				
				<h3>Protein graphs and their visualization</h3>
                The data is addressed by the following information for a <b>Protein graph</b>:
				<br><br>
				<i>/pg/&lt;pdbid&gt;/&lt;chain&gt;/&lt;graphtype&gt;/&lt;format&gt;</i>
				<br>
				 
                <ul>
                <li>&lt;pdbid&gt; an <a href="http://www.rcsb.org/pdb/" target="_blank">RCSB PDB</a> identifier for a protein (4 letter RCSB PDB protein code, e.g., <a href="http://www.rcsb.org/pdb/explore/explore.do?structureId=7tim" target="_blank">7tim</a>.)</li>
                <li>&lt;chain&gt; a PDB chain name (1 letter RCSB PDB chain name, e.g., A). See the <a href="#metadata">metadata section</a> to learn how to find all valid chain names for a certain protein.</li>
                <li>&lt;graphtype&gt; a graph type (PTGL graph type name. The following 6 values are valid: alpha, beta, albe, alphalig, betalig, albelig)</li>
				<li>&lt;format&gt; a file format (The following 2 values are valid: gml, json)</li>
                </ul>
								
                <br><br>
				<h3>Folding graphs and their visualization</h3>
                A <b>Folding graph</b> (FG) is a single connected component of a protein graph (PG). To address a folding graph, you need:
				Folding graph query format: 
				<br><br>
				<i>/fg/&lt;pdbid&gt;/&lt;chain&gt;/&lt;graphtype&gt;/&lt;fold&gt;/&lt;format&gt;</i> 
				<br><br>
                <ul>
                <li>all the data for a protein graph (see above) and</li>
                <li>&lt;fold&gt; the fold number, an integer equal to or greater than zero. See the <a href="#metadata">metadata section</a> to learn how to find all valid chain names for a certain protein.</li>
                </ul>
                
				<br><br>
				<h3>Linear notations and their visualizations</h3>
                There exist 4 different <b>linear notation</b> strings to describe the same folding graph. Each of them has a diffenerent visualization. To address a linear notation, you need:
				<br><br>
				<i>/linnot/&lt;pdbid&gt;/&lt;chain&gt;/&lt;graphtype&gt;/&lt;fold&gt;/&lt;linnot&gt;</i> 
				<br><br>
                <ul>
                <li>all the data for a folding graph (see above) and</li>
                <li>&lt;linnot&gt; the linear notation name (The following 4 values are valid: adj, red, seq, key)</li>
                </ul>
                
                Note that the KEY notation is not defined for bifurcated graphs and may thus return the empty string for a fold.
                
                </p>
                
                
            </section>
            <a name="examples"></a>
            <section style="padding-bottom: 20px">
                <h2>Usage examples</h2>
                <p>
                    Here are some example queries against the API:<br><br>
					
                        Protein graphs:
					    <ul>
		                    <li><i><a href="http://127.0.0.1/api/index.php/pg/7tim/A/albe/json" target="_blank">/api/index.php/pg/7tim/A/albe/json</a></i> retrieves the albe (alpha-beta) graph of PDB 7TIM, chain A in JSON format. </li>
			                <li><i><a href="http://127.0.0.1/api/index.php/pg/7tim/A/albe/gml" target="_blank">/api/index.php/pg/7tim/A/albe/gml</a></i> retrieves the same protein graph in GML format. </li>
			            </ul>
						
						
		                Folding graphs:
					    <ul>
						<li><i><a href="http://127.0.0.1/api/index.php/fg/7tim/A/albe/0/json" target="_blank">/api/index.php/fg/7tim/A/albe/0/json</a></i> retrieves the folding graph #0 of the alpha-beta graph of PDB 7TIM, chain A in JSON format. </li>
		                <li><i><a href="http://127.0.0.1/api/index.php/fg/7tim/A/albe/0/gml" target="_blank">/api/index.php/fg/7tim/A/albe/0/gml</a></i> retrieves the same folding graph in GML format. </li>
						</ul>
			
			            Linear notations:
					    <ul>
		                <li><i><a href="http://127.0.0.1/api/index.php/linnot/7tim/A/albe/0/adj"  target="_blank">api/index.php/linnot/7tim/A/albe/0/adj</a></i> retrieves the ADJ linear notation of folding graph #0 of the alpha-beta graph of PDB 7TIM, chain A. This is a string in JSON format. </li>
			            </ul>
						
						
		            
                </p>                
            </section>
			
			<a name="metadata"></a>
            <section style="padding-bottom: 20px">
                <h2>Metadata queries</h2>
                <p>
				    To automatically process all data of a certain type, it is very handy to know which data is available. For example, you may want to process all chains of a protein -- but how do you know the chain names? The same applies to the 
						number of connected components (folding graphs) of a protein graph -- how do you know how many connected components a protein graph has? We also provide methods to get this information:
						<br><br>
						
			            All chains of a protein:
						
					    <ul>
						<li>format: <i>/chains/&lt;pdbid&gt;</i> </li>
			            <li>example: <i><a href="http://127.0.0.1/api/index.php/chains/7tim" target="_blank">/api/index.php/chains/7tim/</a></i> retrieves all available chain names of all chains of 7tim. This is a list of strings, the format is always JSON. Output would be ["A", "B"] for this example.</li>
						</ul>
						
						All folding graph numbers of a protein graph:
						<br>
					    <ul>
						<li>format: <i>/folds/&lt;pdbid&gt;/&lt;chain&gt;/&lt;graphtype&gt;</i></li>
			            <li>example: <i><a href="http://127.0.0.1/api/index.php/folds/7tim/A/albe" target="_blank">/api/index.php/folds/7tim/A/albe</a></i> retrieves all available fold numbers of the albe graph of 7tim chain A. This is a list of integers, the format is always JSON. Output would be [0, 1, 2, 3, 4, 5, 6, 7, 8, 9] for this example.</li>
						</ul>

                </p>                
            </section>
			
			
						<a name="example_restclient_code"></a>
			<section style="padding-bottom: 20px">
			<h2>Example code to access this API in PHP</h2>
                <p>
                    Here is some simple example code which queries this API from PHP, using <a href="http://curl.haxx.se/" target="_blank">libcurl</a>. The code retrieves the ADJ linear notation string of the albe graph of 7tim chain A. Feel free to use and extend it.
                </p>              

            <!-- HTML generated using hilite.me --><div style="background: #ffffff; overflow:auto;width:auto;border:solid gray;border-width:.1em .1em .1em .8em;padding:.2em .6em;"><table><tr><td><pre style="margin: 0; line-height: 125%"> 1
 2
 3
 4
 5
 6
 7
 8
 9
10
11
12
13
14
15
16
17
18
19</pre></td><td><pre style="margin: 0; line-height: 125%"><span style="color: #557799">&lt;?php</span>
<span style="color: #996633">&#36;url_linnot</span> <span style="color: #333333">=</span> <span style="background-color: #fff0f0">&#39;http://127.0.0.1/api/index.php/linnot/7tim/A/albe/0/adj&#39;</span>;
<span style="color: #996633">&#36;curl</span> <span style="color: #333333">=</span> <span style="color: #007020">curl_init</span>();
<span style="color: #007020">curl_setopt</span>(<span style="color: #996633">&#36;curl</span>, CURLOPT_URL, <span style="color: #996633">&#36;url_linnot</span>);
<span style="color: #007020">curl_setopt</span>(<span style="color: #996633">&#36;curl</span>, CURLOPT_RETURNTRANSFER, <span style="color: #0000DD; font-weight: bold">1</span>);
<span style="color: #007020">curl_setopt</span>(<span style="color: #996633">&#36;curl</span>, CURLOPT_FAILONERROR, <span style="color: #0000DD; font-weight: bold">1</span>);

<span style="color: #996633">&#36;result</span> <span style="color: #333333">=</span> <span style="color: #007020">curl_exec</span>(<span style="color: #996633">&#36;curl</span>);

<span style="color: #008800; font-weight: bold">if</span>(<span style="color: #007020">curl_errno</span>(<span style="color: #996633">&#36;curl</span>)) {
    <span style="color: #888888">// TODO: handle error here</span>
    <span style="color: #008800; font-weight: bold">echo</span> <span style="background-color: #fff0f0">&#39;ERROR: &#39;</span> <span style="color: #333333">.</span> <span style="color: #007020">curl_error</span>(<span style="color: #996633">&#36;curl</span>);
}
<span style="color: #008800; font-weight: bold">else</span> {
    <span style="color: #888888">// TODO: do something with the result here</span>
    <span style="color: #888888">// the result will be &quot;{h,3ph,-2me,-3pe,-2pe,-2pe,-2pe,18pe,-2pe,-4pe,-3pe,2zh,3ph,2me}&quot;</span>
    <span style="color: #008800; font-weight: bold">echo</span> <span style="background-color: #fff0f0">&quot;OK. adj string: &quot;</span> <span style="color: #333333">.</span> <span style="color: #008800; font-weight: bold">json_decode</span>(<span style="color: #996633">&#36;result</span>);    
}
<span style="color: #557799">?&gt;</span>
</pre></td></tr></table></div>
 
        </section>

			
            <a name="help"></a>
            <section style="padding-bottom: 20px">
                <h2>Comments, questions, bug reports, getting more help</h2>
                <p>
                   The PTGL and the data this API serves are based on the VPLG software. If you need more help, you should use the VPLG help options listed at the <a href="https://sourceforge.net/p/vplg/" target="_blank">VPLG sourceforge project page</a>. There is a ticket system and a forum.
                </p>                
            </section>
            
			<a name="author"></a>
            <section style="padding-bottom: 20px">
                <h2>Author</h2>
                <p>
                    The <a href="../" target="_blank">PTGL 3.0</a> REST API was written by <a href="http://rcmd.org/ts/" target="_blank">Tim Schäfer</a> at the <a href="http://www.bioinformatik.uni-frankfurt.de/" target="_blank">MolBI group at Uni Frankfurt, Germany</a>. <br><br><br><a href="../imprint.php" target="_blank">Imprint.</a>					
                </p>                
            </section>	
			
			
			
			
        </body>
    </html>
EOT;
        echo $template;
    }
);



// set application-wide route conditions
\Slim\Route::setDefaultConditions(array(
    'pdbid' => '[a-zA-Z0-9]{4}',
    'chain' => '[a-zA-Z0-9]{1}',
    'graphtype' => 'alpha|beta|albe|alphalig|betalig|albelig',
    'linnot' => 'adj|red|key|seq',
    'fold' => '[0-9]{1,}',
    'graphformat' => 'json|gml',
	'imageformat' => 'png|svg'
));

/**
 * Convert JSON to XML -- requires PEAR XML_Serializer
 * @param string    - json
 * @return string   - XML
 */
function json_to_xml($json) {
    include_once("XML/Serializer.php");

    $options = array (
      'addDecl' => TRUE,
      'encoding' => 'UTF-8',
      'indent' => '  ',
      'rootName' => 'json',
      'mode' => 'simplexml'
    );

    $serializer = new XML_Serializer($options);
    $obj = json_decode($json);
    if ($serializer->serialize($obj)) {
        return $serializer->getSerializedData();
    } else {
        return null;
    }
}

// get db connection
include('../backend/config.php');
$conn_string = "host=" . $DB_HOST . " port=" . $DB_PORT . " dbname=" . $DB_NAME . " user=" . $DB_USER ." password=" . $DB_PASSWORD;
$db = pg_connect($conn_string);

if(!$db) {
  echo "<p><b>ERROR --------------------- No DB connection.</b></p>\n";
}
//else {
//  echo "Connected to database $DB_NAME at host $DB_HOST.\n";
//}

// ----------------- define the GET routes we need ---------------------

// get a single protein graph
$app->get('/pg/:pdbid/:chain/:graphtype/:graphformat', function ($pdbid, $chain, $graphtype, $graphformat) use($db) {    
    //echo "You requested the $graphtype graph of PDB $pdbid chain $chain.\n";
	$pdbid = strtolower($pdbid);
    $query = "SELECT g.graph_id, g.graph_string_json, g.graph_string_gml FROM plcc_graph g INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE p.pdb_id = '$pdbid' AND c.chain_name = '$chain' AND gt.graphtype_text = '$graphtype'";
    $result = pg_query($db, $query);
    
    $num_res = 0;
    while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		$num_res++;
		if($graphformat === "gml") {
          echo $arr['graph_string_gml'];
        }
        if($graphformat === "json") {
          echo $arr['graph_string_json'];
        }
    }
    //echo "Found $num_res graphs.\n";
});

// get a single protein graph visualization as an image
$app->get('/pgvis/:pdbid/:chain/:graphtype/:imageformat', function ($pdbid, $chain, $graphtype, $imageformat) use($db, $app) {    
	$pdbid = strtolower($pdbid);
    $query = "SELECT g.graph_id, g.graph_image_png, g.graph_image_svg FROM plcc_graph g INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE p.pdb_id = '$pdbid' AND c.chain_name = '$chain' AND gt.graphtype_text = '$graphtype'";
    $result = pg_query($db, $query);
    
    $num_res = 0;
	$image_path = "";
    while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		$num_res++;
		$image_path = "";
		if($imageformat === "png") {
          $image_path = $arr['graph_image_png'];
        }
        if($imageformat === "svg") {
		    $image_path = $arr['graph_image_svg'];
        }
    }
	if( ! empty($image_path)) {
	    $image = file_get_contents("../data/" . $image_path);
		$finfo = new finfo(FILEINFO_MIME_TYPE);
		$app->response->header('Content-Type', 'content-type: ' . $finfo->buffer($image));
		echo $image;
	}
});



// get a specific folding graph
$app->get('/fg/:pdbid/:chain/:graphtype/:fold/:graphformat', function ($pdbid, $chain, $graphtype, $fold, $graphformat) use($db) {
    $pdbid = strtolower($pdbid);
    $query = "SELECT fg.foldinggraph_id, fg.graph_string_json, fg.graph_string_gml FROM plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE p.pdb_id = '$pdbid' AND c.chain_name = '$chain' AND gt.graphtype_text = '$graphtype' AND fg.fg_number = $fold";
    $result = pg_query($db, $query);
    
    $num_res = 0;
    while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
	    $num_res++;
	    if($graphformat === "gml") {
	        echo $arr['graph_string_gml'];
	    } 
        if($graphformat === "json") {
          echo $arr['graph_string_json'];
        }		
    }
    //echo "You requested the folding graph of fold # $fold of the $graphtype protein graph of PDB $pdbid chain $chain. Found $num_res results.\n";
});


// get a single folding graph visualization as an image -- this is a convenience method only, it is the same as requesting the adj linnot visualization of the FG
$app->get('/fgvis/:pdbid/:chain/:graphtype/:fold/:imageformat', function ($pdbid, $chain, $graphtype, $fold, $imageformat) use($db, $app) {        
	$pdbid = strtolower($pdbid);
	$linnot = 'adj';
	$query = "SELECT ln.linnot_id, ln.filepath_linnot_image_adj_svg, ln.filepath_linnot_image_adj_png,  ln.filepath_linnot_image_red_svg, ln.filepath_linnot_image_red_png, ln.filepath_linnot_image_seq_svg, ln.filepath_linnot_image_seq_png, ln.filepath_linnot_image_key_svg, ln.filepath_linnot_image_key_png FROM plcc_fglinnot ln INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE p.pdb_id = '$pdbid' AND c.chain_name = '$chain' AND gt.graphtype_text = '$graphtype' AND fg.fg_number = $fold";
    $result = pg_query($db, $query);
    
    $num_res = 0;
	$img_field_name = 'filepath_linnot_image_' . $linnot . '_' . $imageformat;
	$image_path = "";
    while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		$num_res++;
		$image_path = "";
        $image_path = $arr[$img_field_name];        
    }
	if( ! empty($image_path)) {
	    $image = file_get_contents("../data/" . $image_path);
		$finfo = new finfo(FILEINFO_MIME_TYPE);
		$app->response->header('Content-Type', 'content-type: ' . $finfo->buffer($image));
		echo $image;
	}
});


// get a single folding graph linnot visualization as an image
$app->get('/linnotvis/:pdbid/:chain/:graphtype/:fold/:linnot/:imageformat', function ($pdbid, $chain, $graphtype, $fold, $linnot, $imageformat) use($db, $app) {        
	$pdbid = strtolower($pdbid);
	$query = "SELECT ln.linnot_id, ln.filepath_linnot_image_adj_svg, ln.filepath_linnot_image_adj_png,  ln.filepath_linnot_image_red_svg, ln.filepath_linnot_image_red_png, ln.filepath_linnot_image_seq_svg, ln.filepath_linnot_image_seq_png, ln.filepath_linnot_image_key_svg, ln.filepath_linnot_image_key_png FROM plcc_fglinnot ln INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE p.pdb_id = '$pdbid' AND c.chain_name = '$chain' AND gt.graphtype_text = '$graphtype' AND fg.fg_number = $fold";
    $result = pg_query($db, $query);
    
    $num_res = 0;
	$img_field_name = 'filepath_linnot_image_' . $linnot . '_' . $imageformat;
	$image_path = "";
    while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
		$num_res++;
		$image_path = "";
        $image_path = $arr[$img_field_name];        
    }
	if( ! empty($image_path)) {
	    $image = file_get_contents("../data/" . $image_path);
		$finfo = new finfo(FILEINFO_MIME_TYPE);
		$app->response->header('Content-Type', 'content-type: ' . $finfo->buffer($image));
		echo $image;
	}
});

// get all fold names of a protein graph
$app->get('/folds/:pdbid/:chain/:graphtype', function ($pdbid, $chain, $graphtype) use($db) {
    $pdbid = strtolower($pdbid);
    $query = "SELECT fg.fg_number FROM plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE p.pdb_id = '$pdbid' AND c.chain_name = '$chain' AND gt.graphtype_text = '$graphtype' ORDER BY fg_number";
    $result = pg_query($db, $query);
	$json = "[";
	$array = pg_fetch_all($result);
    for($i = 0; $i < count($array); $i++){
        $row = $array[$i];
		$json .= $row['fg_number'];
		if($i < count($array) - 1) {
		    $json .= ", ";
		}
	}
	$json .= "]";
	echo $json;
    //echo "You requested all fold names of the $graphtype protein graph of PDB $pdbid chain $chain.\n";
});

// get a specific linear notation of a folding graph
$app->get('/linnot/:pdbid/:chain/:graphtype/:fold/:linnot', function ($pdbid, $chain, $graphtype, $fold, $linnot) use($db) {
    $pdbid = strtolower($pdbid);
    $query = "SELECT ln.linnot_id, ln.ptgl_linnot_adj, ln.ptgl_linnot_red, ln.ptgl_linnot_seq, ln.ptgl_linnot_key FROM plcc_fglinnot ln INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE p.pdb_id = '$pdbid' AND c.chain_name = '$chain' AND gt.graphtype_text = '$graphtype' AND fg.fg_number = $fold";
    $result = pg_query($db, $query);
    
    $num_res = 0;
    while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
	    $num_res++;
		$req_linnot = 'ptgl_linnot_' . $linnot;
	    echo '"' . $arr[$req_linnot] . '"';
    }
    //echo "You requested the $linnot notation of fold # $fold of the $graphtype protein graph of PDB $pdbid chain $chain.\n";
});

// get all chain names of a protein (PDB file)
$app->get('/chains/:pdbid', function ($pdbid) use($db) {
    $pdbid = strtolower($pdbid);
    $query = "SELECT c.chain_name FROM plcc_chain c INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id WHERE c.pdb_id = '$pdbid' ORDER BY chain_name ";
    $result = pg_query($db, $query);
    
    $num_res = 0;
	//["somestring1", "somestring2"]
	$json = "[";
	$array = pg_fetch_all($result);
    for($i = 0; $i < count($array); $i++){
        $row = $array[$i];
		$json .= '"' . $row['chain_name'] . '"';
		if($i < count($array) - 1) {
		    $json .= ", ";
		}
	}
	$json .= "]";
	echo $json;
    //echo "You requested all chain names of PDB $pdbid.\n";
});


// ----------------- no other routes (adding, editing) are currently used -----------------


// POST route
/*
$app->post(
    '/post',
    function () {
        echo 'This is a POST route';
    }
);
*/

// PUT route
/*
$app->put(
    '/put',
    function () {
        echo 'This is a PUT route';
    }
);
*/

// PATCH route
/*
$app->patch('/patch', function () {
    echo 'This is a PATCH route';
});
*/

// DELETE route
/*
$app->delete(
    '/delete',
    function () {
        echo 'This is a DELETE route';
    }
);
*/
/**
 * Step 4: Run the Slim application
 *
 * This method should be called last. This executes the Slim application
 * and returns the HTTP response to the HTTP client.
 */
$app->run();
