<?php
	include("backend/config.php");
?>

<p>
	<?php echo "$SITE_TITLE"; ?> stands for Protein Topology Graph Library. It is a database of protein structure topologies modeled as undirected, labeled graphs.
	It provides a web server to visualize and analyze protein structure topologies at different scales, e.g., secondary structure level and chain level.
	The graph computation was done by our software <a href="http://www.bioinformatik.uni-frankfurt.de/tools/vplg/" target="_blank">Visualization of Protein-Ligand Graphs (VPLG)</a>. 
	VPLG is based on the 3D atomic coordinates from the <a href="http://www.rcsb.org/pdb/" target="_blank">PDB</a> either as legacy PDB file or as macromolecular Crystallographic Information File (mmCIF).
	Reading mmCIFs allows processing <a href="https://www.rcsb.org/pages/help/largeStructureInfo" target="_blank">large protein structures</a> (> 62 chains or > 99,999 atoms). 
	The secondary structure assignment is parsed with small adaptions from a <a href="http://swift.cmbi.ru.nl/gv/dssp/" target="_blank">DSSP</a> file.
</p>

