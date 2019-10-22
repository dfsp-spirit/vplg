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

//$ptgl_base_url = "http://ptgl.uni-frankfurt.de";

include("../backend/config.php");

$server_name = $SITE_TITLE;
$ptgl_base_url = $SITE_BASE_URL;
$ptgl_api_url = rtrim($ptgl_base_url,"/") . "/api/index.php";


$api_settings = array();
$api_settings['prefer_files_to_db'] = TRUE;
//$api_settings['data_path'] = '../data/';
$api_settings['data_path'] = '../' . $IMG_ROOT_PATH;

 
// GET route
$app->get(
    '/',
    function () use($server_name, $ptgl_base_url, $ptgl_api_url) {
        $template = <<<EOT
<!DOCTYPE html>
    <html>
        <head>
            <meta charset="utf-8"/>
            <title>$server_name REST API</title>
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
                <title>$server_name API</title>
            </header>
            <h1>Welcome to the $server_name API</h1>
			
			<a href=#basics">Basics</a> | <a href="#addressing">Addressing data</a> | <a href="#examples">Usage Examples</a> | <a href="#metadata">Metadata queries</a> | <a href="#collection">Collection queries</a> | <a href="#example_restclient_code">Example API client code</a> | <a href="#help">Help, comments</a> | <a href="#author">Author, Imprint</a>
			
            <p>
			    <br>
                The $server_name Application Programming Interface (API) allows you to quickly retrieve data from the $server_name programmatically. This page explains
                how to use the API.
				<br><br>
				A typical use case is that you want more data than you are willing to download manually by clicking in your web browser. You may, for example, have a list of 250 PDB chains, and you want the albe protein graphs for all of them in GML format.
				<br><br>
				If you are not a programmer, you may prefer the <a href="../" target="_blank">HTML interface of the $server_name</a>.
                
            </p>
			<a name="basics"></a>
            <section>
                <h2>Basics</h2>
                This is a REST API, and you can query it with any HTTP client. There are specialized REST clients, but something like curl will do in principle. Of course, you can query this API with a programming language of your choice.
				We have <a href="#example_restclient_code">some example PHP code to access the API here</a>. 
				<br><br>
				All data is served in JavaScript Object Notation (JSON) format. For graph data, you can chose between Graph Modelling Language (GML) format and JSON format. Images are available in PNG and SVG formats.				 								
            </section>
            <section>
                <h2>Data available via the API</h2>
                <p>
                You can retrieve protein graph (PG) and folding graph (FG) data in GML and JSON formats. You can also retrieve the linear notations (linnots) of a folding graph as a string. It is also possible to retrieve
				the graph images -- both the PG visualization and the 4 different linnot visualizations.
                </p>
                <p>
                The $server_name is a protein topology database which works
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
				All data must be retrieved via GET. Atm, no authentication is required to access the API.
				<br><br>
				Learn howto address: <a href=#address_pg">Protein graphs</a> |  <a href="#address_fg">Folding graphs</a> | <a href="#address_linnot">Linear notations</a> | <a href="#motif">Motifs</a>
				<br>
				
				<a name="address_pg"></a>
				<h3>Addressing Protein graphs and their visualization</h3>
                The data is addressed by the following information for a <b>Protein graph</b>:
				<br><br>
				<i>/<b>pg</b>/&lt;pdbid&gt;/&lt;chain&gt;/&lt;graphtype&gt;/&lt;graphformat&gt;</i>
				<br><br>				 
                <ul>
                <li>&lt;pdbid&gt; an <a href="http://www.rcsb.org/pdb/" target="_blank">RCSB PDB</a> identifier for a protein (4 letter RCSB PDB protein code, e.g., <a href="http://www.rcsb.org/pdb/explore/explore.do?structureId=7tim" target="_blank">7tim</a>.)</li>
                <li>&lt;chain&gt; a PDB chain name (1 letter RCSB PDB chain name, e.g., A). See the <a href="#metadata">metadata section</a> to learn how to find all valid chain names for a certain protein.</li>
                <li>&lt;graphtype&gt; a graph type ($server_name graph type name. The following 6 values are valid: alpha, beta, albe, alphalig, betalig, albelig)</li>
				<li>&lt;graphformat&gt; a graph file format (The following 3 values are valid: gml, json, xml. For more information on these formats, search the web for Graph Modelling Language (gml), JavaScript Object Notation (json), or Extensible Markup Language (XML). For the XML, we are not using some home-brew XML, but the the eXtensible Graph Markup and Modeling Language (XGMML).)</li>
                </ul>
				<br>You can also get the <b>visualization of a protein graph</b> as an image:
				<br><br>
				<i>/<b>pgvis</b>/&lt;pdbid&gt;/&lt;chain&gt;/&lt;graphtype&gt;/&lt;imageformat&gt;</i>
				<br>
				<br> You need all the info from the PG except for the &lt;graphformat&gt;, plus the following extra information:
				<ul>
				<li>&lt;imageformat&gt; an image file format (The following 2 values are valid: png, svg)</li>
                </ul>
							

							
                <br><br>
				<a name="address_fg"></a>
				<h3>Addressing Folding graphs and their visualization</h3>
                A <b>Folding graph</b> (FG) is a single connected component of a protein graph (PG). To address a folding graph, you need:
				Folding graph query format: 
				<br><br>
				<i>/<b>fg</b>/&lt;pdbid&gt;/&lt;chain&gt;/&lt;graphtype&gt;/&lt;fold&gt;/&lt;graphformat&gt;</i> 
				<br><br>
                <ul>
                <li>all the data for a protein graph (see above) and</li>
                <li>&lt;fold&gt; the fold number, an integer equal to or greater than zero. See the <a href="#metadata">metadata section</a> to learn how to find all valid chain names for a certain protein.</li>
                </ul>
                <br>You can also get the <b>visualization of a folding graph</b> as an image. This will return the new DEF folding graph visualization, which shows the folding graph within the parent protein graph: the
				vertices and edges of the FG are colored, while the other (PG-only) vertices and edges are drawn in gray. See <a href="#address_linnot">addressing Linear notations</a> below if you want the ADJ, RED, SEQ or KEY notation visualization of the FG instead.
				<br><br>
				<i>/<b>fgvis</b>/&lt;pdbid&gt;/&lt;chain&gt;/&lt;graphtype&gt;/&lt;fold&gt;/&lt;imageformat&gt;</i>
				<br>
				<br> You need all the info from the FG except for the &lt;graphformat&gt;, plus the following extra information:
				<ul>
				<li>&lt;imageformat&gt; an image file format (The following 2 values are valid: png, svg)</li>
                </ul>
				
				
				
				
				<br><br>
				<a name="address_linnot"></a>
				<h3>Addressing Linear notations and their visualizations</h3>
                There exist 4 different <b>linear notation</b> strings to describe the same folding graph. Each of them has a diffenerent visualization. To address a linear notation, you need:
				<br><br>
				<i>/<b>linnot</b>/&lt;pdbid&gt;/&lt;chain&gt;/&lt;graphtype&gt;/&lt;fold&gt;/&lt;linnot&gt;</i> 
				<br><br>
                <ul>
                <li>all the data for a folding graph (see above) and</li>
                <li>&lt;linnot&gt; the linear notation name (The following 4 values are valid: adj, red, seq, key)</li>
                </ul>
                <br>You can also get the <b>visualization of a linear notation</b> as an image:
				<br><br>
				<i>/<b>linnotvis</b>/&lt;pdbid&gt;/&lt;chain&gt;/&lt;graphtype&gt;/&lt;fold&gt;/&lt;linnot&gt;/&lt;imageformat&gt;</i>
				<br>
				<br> You need all the info from the linnot, plus the following extra information:
				<ul>
				<li>&lt;imageformat&gt; an image file format (The following 2 values are valid: png, svg)</li>
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
		                    <li><i><a href="$ptgl_api_url/pg/7tim/A/albe/json" target="_blank">/api/index.php/pg/7tim/A/albe/json</a></i> retrieves the albe (alpha-beta) graph of PDB 7TIM, chain A in JSON format. </li>
			                <li><i><a href="$ptgl_api_url/pg/7tim/A/albe/gml" target="_blank">/api/index.php/pg/7tim/A/albe/gml</a></i> retrieves the same protein graph in Graph Modelling Language (GML) format. </li>
							<li><i><a href="$ptgl_api_url/pg/7tim/A/albe/xml" target="_blank">/api/index.php/pg/7tim/A/albe/xml</a></i> retrieves the same protein graph in XGMML, an XML-based graph format based on GML. </li>
							<li><i><a href="$ptgl_api_url/pgvis/7tim/A/albe/png" target="_blank">/api/index.php/pgvis/7tim/A/albe/png</a></i> retrieves the same protein graph's visualization in PNG format. </li>
			            </ul>
						
						
		                Folding graphs:
					    <ul>
						<li><i><a href="$ptgl_api_url/fg/7tim/A/albe/0/json" target="_blank">/api/index.php/fg/7tim/A/albe/0/json</a></i> retrieves the folding graph #0 of the alpha-beta graph of PDB 7TIM, chain A in JSON format. </li>
		                <li><i><a href="$ptgl_api_url/fg/7tim/A/albe/0/gml" target="_blank">/api/index.php/fg/7tim/A/albe/0/gml</a></i> retrieves the same folding graph in GML format. </li>
						<li><i><a href="$ptgl_api_url/fg/7tim/A/albe/0/xml" target="_blank">/api/index.php/fg/7tim/A/albe/0/xml</a></i> retrieves the same folding graph in XGMML (XML) format. </li>
						<li><i><a href="$ptgl_api_url/fgvis/7tim/A/albe/0/png" target="_blank">/api/index.php/fgvis/7tim/A/albe/0/png</a></i> retrieves the same folding graph's DEF visualization in PNG format. </li>
						</ul>
			
			            Linear notations:
					    <ul>
		                <li><i><a href="$ptgl_api_url/linnot/7tim/A/albe/0/adj/json"  target="_blank">api/index.php/linnot/7tim/A/albe/0/adj/json</a></i> retrieves the ADJ linear notation of folding graph #0 of the alpha-beta graph of PDB 7TIM, chain A. This is a string in JSON format. </li>
						<li><i><a href="$ptgl_api_url/linnotvis/7tim/A/albe/0/adj/png"  target="_blank">api/index.php/linnotvis/7tim/A/albe/0/adj/png</a></i> retrieves the visualization of the ADJ linear notation of folding graph #0 of the alpha-beta graph of PDB 7TIM, chain A. This is an image in PNG format. </li>
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
						
			            All chain names of a protein:
						
					    <ul>
						<li>format: <i>/<b>chains</b>/&lt;pdbid&gt;</i> </li>
			            <li>example: <i><a href="$ptgl_api_url/chains/7tim" target="_blank">/api/index.php/chains/7tim/</a></i> retrieves all available chain names of all chains of 7tim. This is a list of strings, the format is always JSON. Output would be ["A", "B"] for this example.</li>
						</ul>
						
						All folding graph numbers of a protein graph:
						<br>
					    <ul>
						<li>format: <i>/<b>folds</b>/&lt;pdbid&gt;/&lt;chain&gt;/&lt;graphtype&gt;</i></li>
			            <li>example: <i><a href="$ptgl_api_url/folds/7tim/A/albe" target="_blank">/api/index.php/folds/7tim/A/albe</a></i> retrieves all available fold numbers of the albe graph of 7tim chain A. This is a list of integers, the format is always JSON. Output would be [0, 1, 2, 3, 4, 5, 6, 7, 8, 9] for this example.</li>
						</ul>

                </p>                
            </section>
			
			
			<a name="collection"></a>
            <section style="padding-bottom: 20px">
                <h2>Collection queries</h2>
                <p>
				    All queries presented so far were entity queries, i.e., they return data on a single object. We also provide some collection queries, which return some handy lists of objects. All of these return data in JSON format only:
						<br><br>
						
						<!--
			            A JSON list of all protein graph types (alpha, beta, ...) of a protein chain:
						
					    <ul>
						<li>format: <i>/<b>pgs</b>/&lt;pdbid&gt;/&lt;chain&gt;/json</i> </li>
			            <li>example: <i><a href="$ptgl_api_url/pgs/7tim/A/json" target="_blank">/api/index.php/pgs/7tim/A/json</a></i> retrieves a list of protein graphs in JSON format.</li>
						</ul>
						<br>
						-->
						
						<!--
						A JSON list of all folding graphs of a protein graph (the number of FGs depends on the count of connected components of the PG):
						<br>
					    <ul>
						<li>format: <i>/<b>fgs</b>/&lt;pdbid&gt;/&lt;chain&gt;/&lt;graphtype&gt;/json</i></li>
			            <li>example: <i><a href="$ptgl_api_url/fgs/7tim/A/albe/json" target="_blank">/api/index.php/fgs/7tim/A/albe/json</a></i> retrieves a list of folding graphs in JSON format.</li>
						</ul>
						<br>
						-->
						
						A JSON list of all linear notation strings of a folding graph:
						<br>
					    <ul>
						<li>format: <i>/<b>linnots</b>/&lt;pdbid&gt;/&lt;chain&gt;/&lt;graphtype&gt;/&lt;fold&gt;/json</i></li>
			            <li>example: <i><a href="$ptgl_api_url/linnots/7tim/A/albe/0/json" target="_blank">/api/index.php/linnots/7tim/A/albe/0/json</a></i> retrieves a list of all linear notation strings in JSON format.</li>
						</ul>												

                </p>                
            </section>
            
            
            			<a name="motif"></a>
            <section style="padding-bottom: 20px">
                <h2>Motif queries</h2>
                <p>
                <br> To query for PDB chains containing a motif, you need the following information:
				<ul>
				<li>&lt;motifname&gt; the name of the motif.</li>
				</ul>
				Note that this returns a very long list for some motifs.
				
				The following names (in bold) are available:
				<ul>
				    <li><b>4helix</b> : 4 helix bundle</li>
				    <li><b>globin</b> : globin fold</li>
				    <li><b>barrel</b> : beta barrel</li>
				    <li><b>immuno</b> : immunoglobin fold</li>
				    <li><b>propeller</b> : beta propeller</li>
				    <li><b>jelly</b> : jelly roll</li>
				    <li><b>ubi</b> : ubiquitin fold</li>
				    <li><b>plait</b> : beta plait</li>
				    <li><b>rossman</b> : rossman fold</li>
				    <li><b>tim</b> : TIM barrel</li>
				    
				</ul>
				
                
                                    
				    All of these return data in JSON format only:
						<br><br>
						
			            A JSON list of all protein chains (e.g., '7timA') that contain a certain motif:
						
					    <ul>
						<li>format: <i>/<b>pdbchains_containing_motif</b>/&lt;motifname&gt;/json</i> </li>
			            <li>example: <i><a href="$ptgl_api_url/pdbchains_containing_motif/4helix/json" target="_blank">/api/index.php/pdbchains_containing_motif/4helix/json</a></i> retrieves a list of protein chains that contain the 4helix motif in JSON format.</li>
						</ul>
						<br>
						

                </p>                
            </section>

            
			
			
						<a name="example_restclient_code"></a>
			<section style="padding-bottom: 20px">
			<h2>Example code to access this API</h2>
                <p>
                    PHP<br/><br/>
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
<span style="color: #996633">&#36;url_linnot</span> <span style="color: #333333">=</span> <span style="background-color: #fff0f0">&#39;$ptgl_api_url/linnot/7tim/A/albe/0/adj&#39;</span>;
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

         <br/><br/>         
         <p>
         BASH shell<br/><br/>
         We also provide a BASH shell script for Linux that will download the GML files for a list of proteins or chains. The list can be in a CSV file. The script and an example list is available for download here: <a href="./usage_examples/bash_api_example.zip">bash_api_example.zip</a>. The script comes without any warranty. Make sure you understand it and adapt it to your needs before usage. The script uses wget, awk, tr and other standard unix tools.
         </p>
 
        </section>

			
            <a name="help"></a>
            <section style="padding-bottom: 20px">
                <h2>Comments, questions, bug reports, getting more help</h2>
                <p>
                   The <a href="../" target="_blank">$server_name</a> and the data this API serves are based on the <a href="http://www.bioinformatik.uni-frankfurt.de/tools/vplg/" target="_blank">VPLG software</a>. If you need more help, you should use the VPLG help options listed at the <a href="https://sourceforge.net/p/vplg/" target="_blank">VPLG sourceforge project page</a>. There is a ticket system and a forum.
                </p>                
            </section>
            
			<a name="author"></a>
            <section style="padding-bottom: 20px">
                <h2>Author, Citing, Contact and Imprint</h2>
                <p>
                    The <a href="../" target="_blank">$server_name</a> REST API was written by <a href="http://rcmd.org/ts/" target="_blank">Tim Schäfer</a> at the <a href="http://www.bioinformatik.uni-frankfurt.de/" target="_blank">MolBI group at Uni Frankfurt, Germany</a>. <br><br>Please <a href="../citing.php" target="_blank">cite the $server_name</a> if you use this API for scientific work. <br><br><a href="../imprint.php" target="_blank">Imprint.</a>
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
    'chain' => '[a-zA-Z0-9]{1,4}',
    'graphtype' => 'alpha|beta|albe|alphalig|betalig|albelig',
    'linnot' => 'adj|red|key|seq',
    'fold' => '[0-9]{1,}',
    'graphformat' => 'json|gml|xml',
    'graphformat_json' => 'json',
    'textformat_plain' => 'plain',
    'textformat_json' => 'json',
    'imageformat' => 'png|svg',
    'motifname' => '4helix|globin|barrel|immuno|propeller|jelly|ubi|plait|rossman|tim'
));

/**
 * Convert JSON to XML -- requires PEAR XML_Serializer. UNUSED.
 * @param string    - json
 * @return string   - XML
 */
 /*
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
*/

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


function get_motif_id($motif_abbreviation) {
	$motif_id = -1;
	if($motif_abbreviation === "4helix") {
		$motif_id = 1;
	}
	else if($motif_abbreviation === "globin") {
		$motif_id = 2;
	}
	else if($motif_abbreviation === "barrel") {
		$motif_id = 3;
	}
	else if($motif_abbreviation === "immuno") {
		$motif_id = 4;
	}
	else if($motif_abbreviation === "propeller") {
		$motif_id = 5;
	}
	else if($motif_abbreviation === "jelly") {
		$motif_id = 6;
	}
	else if($motif_abbreviation === "ubi") {
		$motif_id = 7;
	}
	else if($motif_abbreviation === "plait") {
		$motif_id = 8;
	}
	else if($motif_abbreviation === "rossman") {
		$motif_id = 9;
	}
	else if($motif_abbreviation === "tim") {
		$motif_id = 10;
	}
	else {
		$motif_id = -1;
	}
	return $motif_id;
}

function get_protein_graph_file_name_no_ext($pdbid, $chain, $graphtype_string) {
  return $pdbid . "_" . $chain . "_" . $graphtype_string . "_PG";
}

function get_path_to($pdbid, $chain) {
  $mid2chars = substr($pdbid, 1, 2);
  return $mid2chars . "/" . $pdbid . "/". $chain . "/";
}

function get_protein_graph_path_and_file_name_no_ext($pdbid, $chain, $graphtype_string) {
  $path = get_path_to($pdbid, $chain);
  $fname = get_protein_graph_file_name_no_ext($pdbid, $chain, $graphtype_string);
  return $path . $fname;
}

function get_folding_graph_file_name_no_ext($pdbid, $chain, $graphtype_string, $fg_number) {
  return $pdbid . "_" . $chain . "_" . $graphtype_string . "_FG_" . $fg_number;
}

function get_folding_graph_path_and_file_name_no_ext($pdbid, $chain, $graphtype_string, $fg_number) {
  $path = get_path_to($pdbid, $chain);
  $fname = get_folding_graph_file_name_no_ext($pdbid, $chain, $graphtype_string, $fg_number);
  return $path . $fname;
}

// ----------------- define the GET routes we need ---------------------

// get a single protein graph
$app->get('/pg/:pdbid/:chain/:graphtype/:graphformat', function ($pdbid, $chain, $graphtype, $graphformat) use($db, $api_settings) {    
    //echo "You requested the $graphtype graph of PDB $pdbid chain $chain.\n";
	$pdbid = strtolower($pdbid);
	
    if($api_settings['prefer_files_to_db']) {
        $file_basepath = $api_settings['data_path'] . get_protein_graph_path_and_file_name_no_ext($pdbid, $chain, $graphtype);
        $extension = FALSE;
        if($graphformat === "gml") {
	    $extension = ".gml";
	  }
	  if($graphformat === "json") {
	    $extension = ".json";
	  }
		  if($graphformat === "xml") {
	    $extension = ".xml";
	  }
	  
	  if($extension) {
	$json_from_file = file_get_contents($file_basepath . $extension);
        echo $json_from_file;
        }
    }
    else {
      $query = "SELECT g.graph_id, g.graph_string_json, g.graph_string_gml, g.graph_string_xml FROM plcc_graph g INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE p.pdb_id = '$pdbid' AND c.chain_name = '$chain' AND gt.graphtype_text = '$graphtype'";
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
		  if($graphformat === "xml") {
	    echo $arr['graph_string_xml'];
	  }
      }
    }
    //echo "Found $num_res graphs.\n";
});

// get all protein graphs of a chain -- JSON list only
$app->get('/pgs/:pdbid/:chain/:graphformat_json', function ($pdbid, $chain) use($db) {    
    //echo "You requested the $graphtype graph of PDB $pdbid chain $chain.\n";
	$pdbid = strtolower($pdbid);
    $query = "SELECT g.graph_id, g.graph_string_json, g.graph_string_gml FROM plcc_graph g INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE p.pdb_id = '$pdbid' AND c.chain_name = '$chain'";
    $result = pg_query($db, $query);
    
    $num_res = 0;
	$json_list_string = "[ ";
	$all_rows = pg_fetch_all($result);
    for($i = 0; $i < count($all_rows); $i++) {
	    $arr = $all_rows[$i];
		$num_res++;
        $json_list_string .= $arr['graph_string_json'];
        if($i < count($all_rows) - 1) {
		    $json_list_string .= ", ";
		}
    }
	$json_list_string .= "]";
    echo $json_list_string;
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
$app->get('/fg/:pdbid/:chain/:graphtype/:fold/:graphformat', function ($pdbid, $chain, $graphtype, $fold, $graphformat) use($db, $api_settings) {
    $pdbid = strtolower($pdbid);
    
    if($api_settings['prefer_files_to_db']) {
        $file_basepath = $api_settings['data_path'] . get_folding_graph_path_and_file_name_no_ext($pdbid, $chain, $graphtype, $fold);
        $extension = FALSE;
        if($graphformat === "gml") {
	    $extension = ".gml";
	  }
	  if($graphformat === "json") {
	    $extension = ".json";
	  }
		  if($graphformat === "xml") {
	    $extension = ".xml";
	  }
	  
	  if($extension) {
	$json_from_file = file_get_contents($file_basepath . $extension);
        echo $json_from_file;
        }
    }
    else {
      $query = "SELECT fg.foldinggraph_id, fg.graph_string_json, fg.graph_string_gml, fg.graph_string_xml FROM plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE p.pdb_id = '$pdbid' AND c.chain_name = '$chain' AND gt.graphtype_text = '$graphtype' AND fg.fg_number = $fold";
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
		  if($graphformat === "xml") {
	    echo $arr['graph_string_xml'];
	  }		
      }
    }
    //echo "You requested the folding graph of fold # $fold of the $graphtype protein graph of PDB $pdbid chain $chain. Found $num_res results.\n";
});


// get all folding graphs of a PG
$app->get('/fgs/:pdbid/:chain/:graphtype/:graphformat_json', function ($pdbid, $chain, $graphtype) use($db) {
    $pdbid = strtolower($pdbid);
    $query = "SELECT fg.foldinggraph_id, fg.graph_string_json, fg.graph_string_gml FROM plcc_foldinggraph fg INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE p.pdb_id = '$pdbid' AND c.chain_name = '$chain' AND gt.graphtype_text = '$graphtype'";
    $result = pg_query($db, $query);
    
    $num_res = 0;
	$json_list_string = "[ ";
	$all_rows = pg_fetch_all($result);
    for($i = 0; $i < count($all_rows); $i++) {
	    $arr = $all_rows[$i];
		$num_res++;
        $json_list_string .= $arr['graph_string_json'];
        if($i < count($all_rows) - 1) {
		    $json_list_string .= ", ";
		}
    }
	$json_list_string .= "]";
    echo $json_list_string;

});


// get a single folding graph visualization as an image -- this requests the DEF linnot visualization of the FG
$app->get('/fgvis/:pdbid/:chain/:graphtype/:fold/:imageformat', function ($pdbid, $chain, $graphtype, $fold, $imageformat) use($db, $app) {        
	$pdbid = strtolower($pdbid);
	$linnot = 'def';
	$query = "SELECT ln.linnot_id, ln.filepath_linnot_image_def_svg, ln.filepath_linnot_image_def_png FROM plcc_fglinnot ln INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE p.pdb_id = '$pdbid' AND c.chain_name = '$chain' AND gt.graphtype_text = '$graphtype' AND fg.fg_number = $fold";
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

// get a specific linear notation string of a folding graph
$app->get('/linnot/:pdbid/:chain/:graphtype/:fold/:linnot/:textformat_json', function ($pdbid, $chain, $graphtype, $fold, $linnot) use($db) {
    $pdbid = strtolower($pdbid);
    $query = "SELECT ln.linnot_id, ln.ptgl_linnot_adj, ln.ptgl_linnot_red, ln.ptgl_linnot_seq, ln.ptgl_linnot_key FROM plcc_fglinnot ln INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE p.pdb_id = '$pdbid' AND c.chain_name = '$chain' AND gt.graphtype_text = '$graphtype' AND fg.fg_number = $fold";
    $result = pg_query($db, $query);
    
    $num_res = 0;
    while ($arr = pg_fetch_array($result, NULL, PGSQL_ASSOC)){
	    $num_res++;
		$req_linnot = 'ptgl_linnot_' . $linnot;
	    echo json_encode($arr[$req_linnot]);
    }
    //echo "You requested the $linnot notation of fold # $fold of the $graphtype protein graph of PDB $pdbid chain $chain.\n";
});

// get all linear notation strings of a folding graph
$app->get('/linnots/:pdbid/:chain/:graphtype/:fold/:textformat_json', function ($pdbid, $chain, $graphtype, $fold) use($db) {
    $pdbid = strtolower($pdbid);
    $query = "SELECT ln.linnot_id, ln.ptgl_linnot_adj, ln.ptgl_linnot_red, ln.ptgl_linnot_seq, ln.ptgl_linnot_key FROM plcc_fglinnot ln INNER JOIN plcc_foldinggraph fg ON ln.linnot_foldinggraph_id = fg.foldinggraph_id INNER JOIN plcc_graph g ON fg.parent_graph_id = g.graph_id INNER JOIN plcc_chain c ON g.chain_id = c.chain_id INNER JOIN plcc_protein p ON c.pdb_id = p.pdb_id INNER JOIN plcc_graphtypes gt ON g.graph_type = gt.graphtype_id WHERE p.pdb_id = '$pdbid' AND c.chain_name = '$chain' AND gt.graphtype_text = '$graphtype' AND fg.fg_number = $fold";
    $result = pg_query($db, $query);
    
    $num_res = 0;
	$json = "[";
	$array = pg_fetch_all($result);
	for($i = 0; $i < count($array); $i++){
	    $row = $array[$i];
	    $num_res++;
		$all_linnots = array('ptgl_linnot_adj', 'ptgl_linnot_red', 'ptgl_linnot_seq', 'ptgl_linnot_key');
		for($j = 0; $j < count($all_linnots); $j++) {
		    $req_linnot = $all_linnots[$j];
		    
		    $str = json_encode($row[$req_linnot]);
		    //if($str === "null") {
		    //    $json .= '""';		      
		    //}
		    //else {
		        $json .= $str;
		    //}
			
		    
		    if($j < count($all_linnots) - 1) {
		        $json .= ", ";
		    }
		}
    }
	$json .= "]";
	echo $json;
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
        $num_res++;
		$json .= '"' . $row['chain_name'] . '"';
		if($i < count($array) - 1) {
		    $json .= ", ";
		}
	}
	$json .= "]";
	echo $json;
    //echo "You requested all chain names of PDB $pdbid.\n";
});


// get all chains with a certain motif
$app->get('/pdbchains_containing_motif/:motifname/:textformat_json', function ($motifname) use($db) {
    $motifid = get_motif_id($motifname);
    $query = "SELECT c.chain_name, p.pdb_id
                            FROM plcc_nm_chaintomotif c2m
                            INNER JOIN plcc_chain c
                            ON c2m.chain_id = c.chain_id				   
                            INNER JOIN plcc_protein p
                            ON p.pdb_id = c.pdb_id 
                            WHERE c2m.motif_id = '" . $motifid ."'";
    $result = pg_query($db, $query);
    
    $num_res = 0;
	//["somestring1", "somestring2"]
	$json = "[";
	$array = pg_fetch_all($result);
    for($i = 0; $i < count($array); $i++){
        $row = $array[$i];
        $num_res++;
		$json .= '"' . $row['pdb_id'] . $row['chain_name'] . '"';
		if($i < count($array) - 1) {
		    $json .= ", ";
		}
	}
	$json .= "]";
	echo $json;
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
