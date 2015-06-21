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
    .selector('edge.questionable')
      .css({
        'line-style': 'dotted',
        'target-arrow-shape': 'diamond'
      })
    .selector('.faded')
      .css({
        'opacity': 0.25,
        'text-opacity': 0
      }),
  
  elements: {
    nodes: [
      { data: { id: '0', name: 'H1', weight: 10, faveColor: '#FF0000', faveShape: 'ellipse' }, position: { x: 100, y: 50 } },
      { data: { id: '1', name: 'H2', weight: 10, faveColor: '#FF0000', faveShape: 'ellipse' }, position: { x: 200, y: 50 } },
      { data: { id: '2', name: 'E3', weight: 10, faveColor: '#000000', faveShape: 'rectangle' }, position: { x: 300, y: 50 } },
      { data: { id: '3', name: 'L4', weight: 10, faveColor: '#FF00FF', faveShape: 'ellipse' }, position: { x: 400, y: 50 } }
    ],
    edges: [
      { data: { source: '0', target: '1', faveColor: '#00FF00', strength: 10 } },
      { data: { source: '0', target: '2', faveColor: '#FF0000', strength: 10 } },
      { data: { source: '2', target: '3', faveColor: '#FF00FF', strength: 10 } },
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