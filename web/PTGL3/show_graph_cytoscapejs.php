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
  <!-- <script src="http://cytoscape.github.io/cytoscape.js/api/cytoscape.js-latest/cytoscape.min.js"></script> -->
  <script src="./js/cytoscapejs-snapshot/build/cytoscape.min.js"></script>
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
        'text-outline-width': 1,
        'text-outline-color': '#000000',
        'background-color': '#000000',
        'color': '#909090',
		'text-valign' : 'center',
        'text-halign' : 'center',
		'font-size': '8px'
      })	  
	.selector('.sse_type_e')
      .css({
        'shape': 'rectangle',
        'text-outline-color': '#000000',
        'background-color': '#000000',
        'color': '#909090'
      })
	.selector('.sse_type_h')
      .css({
        'shape': 'ellipse',
        'text-outline-color': '#FF0000',
        'background-color': '#FF0000',
        'color': '#000000'
      })
	.selector('.sse_type_l')
      .css({
        'shape': 'ellipse',
        'text-outline-color': '#FF00FF',
        'background-color': '#FF00FF',
        'color': '#000000'
      })
	.selector('.pgedge')
     .css({
        'opacity': 0.666,
        'target-arrow-shape': 'triangle',
        'source-arrow-shape': 'none',
        'line-color': '#FF0000',
        'source-arrow-color': '#FF0000',
        'target-arrow-color': '#FF0000',
		'curve-style': 'unbundled-bezier',
        'control-point-distance': 'data(edgeHeight)',
        'control-point-weight': '0.5'
    })
	.selector('.edge_type_p')
      .css({
        'line-color': '#FF0000',
        'source-arrow-color': '#FF0000',
        'target-arrow-color': '#FF0000'
      })
	.selector('.edge_type_a')
      .css({
        'line-color': '#0000FF',
        'source-arrow-color': '#0000FF',
        'target-arrow-color': '#0000FF'
      })
	.selector('.edge_type_m')
      .css({
        'line-color': '#00FF00',
        'source-arrow-color': '#00FF00',
        'target-arrow-color': '#00FF00'
      })
	.selector('.edge_type_j')
      .css({
        'line-color': '#FF00FF',
        'source-arrow-color': '#FF00FF',
        'target-arrow-color': '#FF00FF'
      })
    ,
	  
    elements: {
    nodes: [
      { data: { id: '0', name: '1e' }, position: { x: 100, y: 50 }, classes: 'sse sse_type_e' },
      { data: { id: '1', name: '2h' }, position: { x: 150, y: 50 }, classes: 'sse sse_type_h' },
      { data: { id: '2', name: '3e' }, position: { x: 200, y: 50 }, classes: 'sse sse_type_e' },
      { data: { id: '3', name: '4h' }, position: { x: 250, y: 50 }, classes: 'sse sse_type_h' },
      { data: { id: '4', name: '5e' }, position: { x: 300, y: 50 }, classes: 'sse sse_type_e' },
      { data: { id: '5', name: '6h' }, position: { x: 350, y: 50 }, classes: 'sse sse_type_h' },
      { data: { id: '6', name: '7e' }, position: { x: 400, y: 50 }, classes: 'sse sse_type_e' },
      { data: { id: '7', name: '8h' }, position: { x: 450, y: 50 }, classes: 'sse sse_type_h' },
      { data: { id: '8', name: '9h' }, position: { x: 500, y: 50 }, classes: 'sse sse_type_h' },
      { data: { id: '9', name: '10e' }, position: { x: 550, y: 50 }, classes: 'sse sse_type_e' },
      { data: { id: '10', name: '11h' }, position: { x: 600, y: 50 }, classes: 'sse sse_type_h' },
      { data: { id: '11', name: '12h' }, position: { x: 650, y: 50 }, classes: 'sse sse_type_h' },
      { data: { id: '12', name: '13e' }, position: { x: 700, y: 50 }, classes: 'sse sse_type_e' },
      { data: { id: '13', name: '14h' }, position: { x: 750, y: 50 }, classes: 'sse sse_type_h' },
      { data: { id: '14', name: '15h' }, position: { x: 800, y: 50 }, classes: 'sse sse_type_h' },
      { data: { id: '15', name: '16h' }, position: { x: 850, y: 50 }, classes: 'sse sse_type_h' },
      { data: { id: '16', name: '17e' }, position: { x: 900, y: 50 }, classes: 'sse sse_type_e' },
      { data: { id: '17', name: '18h' }, position: { x: 950, y: 50 }, classes: 'sse sse_type_h' },
      { data: { id: '18', name: '19e' }, position: { x: 1000, y: 50 }, classes: 'sse sse_type_e' },
      { data: { id: '19', name: '20h' }, position: { x: 1050, y: 50 }, classes: 'sse sse_type_h' },
      { data: { id: '20', name: '21h' }, position: { x: 1100, y: 50 }, classes: 'sse sse_type_h' },
      { data: { id: '21', name: '22l' }, position: { x: 1150, y: 50 }, classes: 'sse sse_type_l' }
    ]
    ,
    edges: [
      { data: { source: '0', target: '2', edgeHeight: '-50px' }, classes: 'pgedge edge_type_p' },
      { data: { source: '0', target: '18', edgeHeight: '-450px' }, classes: 'pgedge edge_type_p' },
      { data: { source: '0', target: '21', edgeHeight: '-525px' }, classes: 'pgedge edge_type_j' },
      { data: { source: '1', target: '3', edgeHeight: '-50px' }, classes: 'pgedge edge_type_p' },
      { data: { source: '2', target: '4', edgeHeight: '-50px' }, classes: 'pgedge edge_type_p' },
      { data: { source: '4', target: '6', edgeHeight: '-50px' }, classes: 'pgedge edge_type_p' },
      { data: { source: '6', target: '9', edgeHeight: '-75px' }, classes: 'pgedge edge_type_p' },
      { data: { source: '7', target: '21', edgeHeight: '-350px' }, classes: 'pgedge edge_type_j' },
      { data: { source: '8', target: '11', edgeHeight: '-75px' }, classes: 'pgedge edge_type_p' },
      { data: { source: '9', target: '11', edgeHeight: '-50px' }, classes: 'pgedge edge_type_m' },
      { data: { source: '9', target: '12', edgeHeight: '-75px' }, classes: 'pgedge edge_type_p' },
      { data: { source: '9', target: '21', edgeHeight: '-300px' }, classes: 'pgedge edge_type_j' },
      { data: { source: '11', target: '14', edgeHeight: '-75px' }, classes: 'pgedge edge_type_p' },
      { data: { source: '12', target: '16', edgeHeight: '-100px' }, classes: 'pgedge edge_type_p' },
      { data: { source: '13', target: '21', edgeHeight: '-200px' }, classes: 'pgedge edge_type_j' },
      { data: { source: '14', target: '16', edgeHeight: '-50px' }, classes: 'pgedge edge_type_m' },
      { data: { source: '16', target: '18', edgeHeight: '-50px' }, classes: 'pgedge edge_type_p' },
      { data: { source: '18', target: '21', edgeHeight: '-75px' }, classes: 'pgedge edge_type_j' },
      { data: { source: '19', target: '21', edgeHeight: '-50px' }, classes: 'pgedge edge_type_j' }
    ]
  }  
  ,
  
  ready: function(){
    window.cy = this;	
  }
});

}); // on dom ready
</script>

</body>
</html>