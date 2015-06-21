<html>
<head>
<title>PTGL3 -- SigmaJS Protein graph visualization</title>
<style type="text/css">
  #sigma-container {
    max-width: 800px;
    height: 600px;
    margin: auto;
   border: 0px solid black;
  }
</style>
</head>
<body>
<div id="sigma-container"></div>
<p>The albe graph of 7TIM, chain A.</p>
<script src="./js/sigmajs/sigma.min.js"></script>
<script src="./js/sigmajs/plugins/sigma.parsers.gexf.min.js"></script>
<script src="./js/sigmajs/plugins/sigma.layout.forceAtlas2.min.js"></script>
<script src="./js/sigmajs/plugins/sigma.renderers.customShapes.min.js"></script>


<script>
   s = new sigma({
  
});
CustomShapes.init(s);

  var actualGraph;

function showGraph(pathToFile) {

    if (actualGraph) {
        sigma.parsers.gexf( 
            pathToFile,
            actualGraph,
            function(s) {
               
			   s.graph.nodes().forEach(function(node, i, a) {
  node.x = Math.cos(Math.PI * 2 * i / a.length);
  node.y = Math.sin(Math.PI * 2 * i / a.length);
  node.type = 'square';  
	});
	  var edges = s.graph.edges(); 

	//Using for loop
	for (var i = 0; i < edges.length; i += 1){
		edges[i].type = 'curve';
	}
	s.refresh();
	
            }
        );    
    } else {    
        sigma.parsers.gexf(
            pathToFile,
            {container: 'sigma-container'},
            function(s) {
                actualGraph = s;
							   s.graph.nodes().forEach(function(node, i, a) {
  node.x = Math.cos(Math.PI * 2 * i / a.length);
  node.y = Math.sin(Math.PI * 2 * i / a.length);
  node.type = 'square';  
	});
		
		  var edges = s.graph.edges(); 

	//Using for loop
	for (var i = 0; i < edges.length; i += 1){
		edges[i].type = 'curve';
		edges[i].label = 'curve';
	}
	  
	  
	s.refresh();
            }
        );
    }   
}

  var inputFile = './js/sigmajs/data/protgraph.gexf';
  showGraph(inputFile);
      	

</script>
</body>
</html>