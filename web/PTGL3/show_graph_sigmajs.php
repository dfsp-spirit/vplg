<html>
<head>
<style type="text/css">
  #sigma-container {
    max-width: 800px;
    height: 600px;
    margin: auto;
    border: 1px solid black;
  }
</style>
</head>
<body>
<div id="sigma-container"></div>
<script src="./js/sigmajs/sigma.min.js"></script>
<script src="./js/sigmajs/plugins/sigma.parsers.gexf.min.js"></script>
<script src="./js/sigmajs/plugins/sigma.layout.forceAtlas2.min.js"></script>
<script>
  sigma.parsers.gexf(
     './js/sigmajs/data/protgraph.gexf',
    // './data/simplegraph.gexf',
    // './data/LesMiserables.gexf',
    //'./data/les-miserables.gexf',
    { // Here is the ID of the DOM element that
      // will contain the graph:
      container: 'sigma-container',
      settings: {
	defaultNodeColor: '#808080'
      }
    },
    function(s) {
      // This function will be executed when the
      // graph is displayed, with "s" the related
      // sigma instance.
      //s.startForceAtlas2();
      s.graph.nodes().forEach(function(node, i, a) {
  node.x = Math.cos(Math.PI * 2 * i / a.length);
  node.y = Math.sin(Math.PI * 2 * i / a.length);
});
      s.refresh();
    }
  );
</script>
</body>
</html>