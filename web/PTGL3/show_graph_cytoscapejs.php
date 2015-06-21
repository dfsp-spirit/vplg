<!DOCTYPE html>
<html>
<head>
  <link href="style.css" rel="stylesheet" />
  <meta charset=utf-8 />
  <title>PTGL3 -- CytoscapeJS Protein graph visualization</title>
<style type="text/css">
   body { 
  font: 14px helvetica neue, helvetica, arial, sans-serif;
}

#cy {
  height: 100%;
  width: 100%;
  position: absolute;
  left: 0;
  top: 0;
}
</style>
  <script src="http://ajax.googleapis.com/ajax/libs/jquery/1/jquery.min.js"></script>
  <script src="http://cytoscape.github.io/cytoscape.js/api/cytoscape.js-latest/cytoscape.min.js"></script>
  <script src="code.js"></script>
</head>
  
<body>
  <div id="cy"></div>

<script>
$(function(){ // on dom ready

$('#cy').cytoscape({
  layout: {
    name: 'preset',
    padding: 10
  },
  
  style: cytoscape.stylesheet()
    .selector('node')
      .css({
        'shape': 'data(faveShape)',
        'width': 20,
		'height': 20,
        'content': 'data(name)',
        'text-valign': 'center',
        'text-outline-width': 1,
        'text-outline-color': 'data(faveColor)',
        'background-color': 'data(faveColor)',
        'color': '#808080',
		'text-valign' : 'center',
        'text-halign' : 'center',
		'font-size': '8px'
      })
    .selector(':selected')
      .css({
        'border-width': 2,
        'border-color': '#333'
      })
    .selector('edge')
      .css({
        'opacity': 0.666,
        'width': 'mapData(strength, 70, 100, 2, 6)',
        'target-arrow-shape': 'none',
        'source-arrow-shape': 'none',
        'line-color': 'data(faveColor)',
        'source-arrow-color': 'data(faveColor)',
        'target-arrow-color': 'data(faveColor)',
		'curve-style': 'unbundled-bezier',
        'control-point-distance': '-200px',
        'control-point-weight': '0.5'
      })
    .selector('.sse')
      .css({
        'shape': 'rectangle',
        'width': 20,
		'height': 20,
        'content': 'data(name)',
        'text-valign': 'center',
        'text-outline-width': 1,
        'text-outline-color': '#000000',
        'background-color': '#000000',
        'color': '#909090',
		'text-valign' : 'center',
        'text-halign' : 'center',
		'font-size': '8px'
      })	  
	.selector('.strand')
      .css({
        'shape': 'rectangle',
        'text-outline-color': '#000000',
        'background-color': '#000000',
        'color': '#909090'
      })
	.selector('.helix')
      .css({
        'shape': 'ellipse',
        'text-outline-color': '#FF0000',
        'background-color': '#FF0000',
        'color': '#000000'
      })
	.selector('.ligand')
      .css({
        'shape': 'ellipse',
        'text-outline-color': '#FF00FF',
        'background-color': '#FF00FF',
        'color': '#000000'
      })
	.selector('.pgedge')
     .css({
        'opacity': 0.666,
        'target-arrow-shape': 'none',
        'source-arrow-shape': 'none',
        'line-color': '#FF0000',
        'source-arrow-color': '#FF0000',
        'target-arrow-color': '#FF0000',
		'curve-style': 'unbundled-bezier',
        'control-point-distance': '-200px',
        'control-point-weight': '0.5'
    })
	.selector('.edgeparallel')
      .css({
        'line-color': '#FF0000',
        'source-arrow-color': '#FF0000',
        'target-arrow-color': '#FF0000',
      })
	.selector('.edgeantiparallel')
      .css({
        'line-color': '#0000FF',
        'source-arrow-color': '#0000FF',
        'target-arrow-color': '#0000FF',
      })
	.selector('.edgemixed')
      .css({
        'line-color': '#00FF00',
        'source-arrow-color': '#00FF00',
        'target-arrow-color': '#00FF00',
      })
	.selector('.edgeligand')
      .css({
        'line-color': '#FF00FF',
        'source-arrow-color': '#FF00FF',
        'target-arrow-color': '#FF00FF',
      })
    ,
	  
  
  elements: {
    nodes: [
      { data: { id: '0', name: '1e' }, position: { x: 100, y: 50 }, classes: 'sse strand' },
      { data: { id: '1', name: '2h' }, position: { x: 150, y: 50 }, classes: 'sse helix' },
      { data: { id: '2', name: '3e' }, position: { x: 200, y: 50 }, classes: 'sse strand' },
      { data: { id: '3', name: '4h' }, position: { x: 250, y: 50 }, classes: 'sse helix' },
	  { data: { id: '4', name: '5e' }, position: { x: 300, y: 50 }, classes: 'sse strand' },
      { data: { id: '5', name: '6h' }, position: { x: 350, y: 50 }, classes: 'sse helix' },
      { data: { id: '6', name: '7e' }, position: { x: 400, y: 50 }, classes: 'sse strand' },
      { data: { id: '7', name: '8h' }, position: { x: 450, y: 50 }, classes: 'sse helix' },
	  { data: { id: '8', name: '9h' }, position: { x: 500, y: 50 }, classes: 'sse helix' },
      { data: { id: '9', name: '10e' }, position: { x: 550, y: 50 }, classes: 'sse strand' },
      { data: { id: '10', name: '11h' }, position: { x: 600, y: 50 }, classes: 'sse helix' },
      { data: { id: '11', name: '12h' }, position: { x: 650, y: 50 }, classes: 'sse helix' },
	  { data: { id: '12', name: '13e' }, position: { x: 700, y: 50 }, classes: 'sse strand' },
      { data: { id: '13', name: '14h' }, position: { x: 750, y: 50 }, classes: 'sse helix' },
      { data: { id: '14', name: '15h' }, position: { x: 800, y: 50 }, classes: 'sse helix' },
      { data: { id: '15', name: '16h' }, position: { x: 850, y: 50 }, classes: 'sse helix' }
    ],
    edges: [
      { data: { source: '0', target: '1' }, classes: 'pgedge edgeparallel' },
      { data: { source: '0', target: '2' }, classes: 'pgedge edgeantiparallel' },
      { data: { source: '2', target: '3' }, classes: 'pgedge edgeparallel' },
	  { data: { source: '2', target: '6' }, classes: 'pgedge edgemixed' }
    ]
  },
  
  ready: function(){
    window.cy = this;	
  }
});

}); // on dom ready
</script>

</body>
</html>