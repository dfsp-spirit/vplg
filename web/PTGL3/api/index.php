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
                body{background:#FFF;margin:0 auto;min-height:100%;padding:0 30px;width:440px;color:#666;font:14px/23px Arial,Verdana,sans-serif;}
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
            <p>
                The PTGL Advanced Programming Interface allows you to retrieve data from the PTGL programatically. This page explains
                the very basics of how to use the API.
                
            </p>
            <section>
                <h2>Basics</h2>
                This is a REST API, and you can query it with any HTTP client. There are specialized REST clients, but something like curl will do in principle.
            </section>
            <section>
                <h2>Data available via the API</h2>
                <p>
                You can retrieve protein graph and folding graph data in GML and JSON formats. You can also retrieve the linear notation of a folding graph as a string.
                </p>
                <p>
                The PTGL is a protein topology database which works
                mainly on the level of a protein chain: a protein graph includes all the secondary structure elements (SSEs) of a chain. (Which SSEs types
                are included is determined by the graph type, e.g., the alpha graph only contains alpha helices, while the alpha-beta graph contains both alpha helices and beta strands.)
                </p>
            </section>
            <section>
                <h2>Addressing data</h2>
                <p>
                Since the data is computed from PDB files and the SSE assignments of the DSSP algorithm, the data is addressed by the following information for a <b>Protein graph</b>:
                <ul>
                <li>a PDB identifier (4 letter RCSB PDB protein code, e.g., 7tim)</li>
                <li>a PDB chain name (1 letter RCSB PDB chain name, e.g., A)</li>
                <li>a graph type (PTGL graph type name. The following 6 values are valid: alpha, beta, albe, alphalig, betalig, albelig)</li>
                </ul>
                
                A <b>Folding graph</b> (FG) is a single connected component of a protein graph (PG). To address a folding graph, you need:
                <ul>
                <li>all the data for a protein graph (see above) and</li>
                <li>the fold number (an integer equal to or greater than zero)</li>
                </ul>
                
                A folding graph can be described by 4 different <b>linear notation</b> strings. To address a linear notation, you need:
                <ul>
                <li>all the data for a folding graph (see above) and</li>
                <li>the linear notation name (The following 4 values are valid: adj, red, seq, key)</li>
                </ul>
                
                Note that the KEY notation is not defined for bifurcated graphs and may thus return 'null'.
                
                </p>
                
                
            </section>
            
            <section style="padding-bottom: 20px">
                <h2>Usage examples</h2>
                <p>
                    Here are some example queries against the API, assuming <i>PTGL3_BASE</i> is the base URL of the PTGL3:
                    <ul>
		    <li><i>http://PTGL3_BASE/api/get/7tim/A/albe</i> retrieves the alpha-beta graph of PDB 7TIM, chain A in JSON format </li>
		    <li><i>http://PTGL3_BASE/api/get/7tim/A/albe?format=GML</i> retrieves the alpha-beta graph of PDB 7TIM, chain A in GML format </li>
		    <li><i>http://PTGL3_BASE/api/get/7tim/A/albe/0</i> retrieves the folding graph #0 of the alpha-beta graph of PDB 7TIM, chain A in GML format </li>
		    <li><i>http://PTGL3_BASE/api/get/7tim/A/albe/0/ADJ</i> retrieves the ADJ linear notation of folding graph #0 of the alpha-beta graph of PDB 7TIM, chain A in GML format </li>
		    </ul>
                </p>                
            </section>
            
            <section style="padding-bottom: 20px">
                <h2>Comments, questions, bug reports, getting more help</h2>
                <p>
                   The PTGL and the data this API serves are based on the VPLG software. If you need more help, you should use the VPLG help options listed at the <a href="https://sourceforge.net/p/vplg/" target="_blank">VPLG sourceforge project page</a>. There is a ticket system and a forum.
                </p>                
            </section>
            
            <section style="padding-bottom: 20px">
                <h2>Author</h2>
                <p>
                    This API was written by Tim Schäfer.
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
    'fold' => '[0-9]{1,}'
));

// ----------------- define the GET routes we need ---------------------

// get a single protein graph
$app->get('/pg/:pdbid/:chain/:graphtype', function ($pdbid, $chain, $graphtype) {
    echo "You requested the $graphtype graph of PDB $pdbid chain $chain.\n";
});

// get all protein graphs of a chain
$app->get('/pg/:pdbid/:chain', function ($name) {
    echo "You requested all 6 graph types of of PDB $pdbid chain $chain.\n";
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
