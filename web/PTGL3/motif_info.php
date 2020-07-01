		<br>
		<a class="anchor" id="motifsAlpha"></a>
		<h4> Alpha motifs </h4>

		<div class="anchor" id="4helix"></div>
		<div>
		    <h5><b> Four Helix Bundle </b></h5>
		    <p>
		    	The Four Helix Bundle is a protein motif which consists of four alpha helices which arrange in a bundle.
		    	There are two types of the Four Helix Bundle which differ in the connections between the alpha helices.
		    	The first type of the Four Helix Bundle is all antiparallel and the second type has two pairs of parallel helices which have an antiparallel connection. 
		    	<br>Found <?php print $all_motif_counts['4helix'];?> times in the current database.
		    </p>
		    <p><img class="motifimage" src="./images/4helixbeide_struktur.jpg" width="300" /></p>
		</div>
		
		
		<br>
		<div class="anchor" id="globin"></div>
		<div>
		    <h5><b> Globin Fold </b></h5>
		    <p>
		    	The Globin Fold is an alpha helix structure motif which is composed of a bundle, consisting of eight alpha helices, which are connected over short loop regions.
		    	The helices do not have a fixed arrangement, but the last two helices in sequential order are antiparallel.
		    	<br>Found <?php print $all_motif_counts['globin'];?> times in the current database.
		    </p>
		    <p><img class="motifimage" src="./images/globin_struktur.jpg" width="300" /></p>
		</div>
		
		<br>
		<a class="anchor" id="motifsBeta"></a>
		<h4> Beta motifs </h4>

		<div class="anchor" id="barrel"></div>
		<div>
		    <h5><b> Up-and-down barrel </b></h5>
		    <p>
		    	The up-and-down barrel is composed of a series of antiparallel beta strands which are connected via hydrogen bonds.
		    	There are two major families of the up-and-down barrel, the ten-stranded and the eight-stranded version.
		    	<br>Found <?php print $all_motif_counts['barrel'];?> times in the current database.
		    </p>
		    <p><img class="motifimage" src="./images/barrel_struktur.jpg" width="300" /></p>
		</div>
		
		
		<br>
		<div class="anchor" id="immuno"></div>
		<div>
		    <h5><b> Immunoglobin fold </b></h5>
		    <p>
		    	The immunoglobulin fold is a two-layer sandwich.
		    	Usually, it consists of seven antiparallel beta strands, arranged in two beta sheets.
		    	The first is composed of four and the second of three strands.
		    	Both are connected via a disulfide bond to build the sandwich.
		    	<br>Found <?php print $all_motif_counts['immuno'];?> times in the current database.
		    </p>
		    <p><img class="motifimage" src="./images/immuno_struktur.jpg" width="300" /></p>
		</div>
		
		
		<br>
		<div class="anchor" id="propeller"></div>
		<div>
		    <h5><b> Beta Propeller </b></h5>
		    <p>
		    	This beta motif contains between four and eight beta sheets, which are arranged around the center of the protein.
		    	Each sheet is formed by four antiparallel beta strands.
		    	One sheet makes up one of the propeller blades.
		    	To build a four-bladed propeller, for example, four of these sheets are grouped together.
		    	<br>Found <?php print $all_motif_counts['propeller'];?> times in the current database.
		    </p>
		    <p><img class="motifimage" src="./images/propeller_struktur.jpg" width="300" /></p>
		</div>
		
		
		<br>
		<div class="anchor" id="jelly"></div>
		<div>
		    <h5><b> Jelly Roll </b></h5>
		    <p>
		    	The Jelly Roll motif has a barrel structure, which seems like a jelly roll.
		    	The barrel includes eight beta strands, which build a two-layer sandwich of four strands.
		    	<br>Found <?php print $all_motif_counts['jelly'];?> times in the current database.
		    </p>
		    <p><img class="motifimage" src="./images/jelly_struktur.jpg" width="300" /></p>
		</div>
		
		<?php if($ENABLE_MOTIF_SEARCH_ALPHABETA) { ?>

		<br>
		<h4> Alpha and Beta motifs </h4>

		<div class="anchor" id="ubi"></div>
		<div>
		    <h5><b> Ubiquitin Roll </b></h5>
		    <p>
		    	The Ubiquitin Roll motif contains alpha helices and beta strands.
		    	Two different types must be considerd.
		    	The first is composed of two beta-hairpin motifs and an enclosed alpha helix and the second an alpha helix which is surrounded by two beta sheets.
		    	<br>Found <?php print $all_motif_counts['ubi'];?> times in the current database.
		    </p>
		    <p><img class="motifimage" src="./images/ubibeide_struktur.jpg" width="300" /></p>
		</div>
		
		
		<br>
		<div class="anchor" id="plait"></div>
		<div>
		    <h5><b> Alpha Beta Plait </b></h5>
		    <p>
		    	This motif has four or five beta strand, which form an antiparallel beta sheet. In-between the sheet are two or more alpha helices.
		    	<br>Found <?php print $all_motif_counts['plait'];?> times in the current database.
		    </p>
		    <p><img class="motifimage" src="./images/plait_struktur.jpg" width="300" /></p>
		</div>
		
		
		<br>
		<div class="anchor" id="rossman"></div>
		<div>
		    <h5><b> Rossman Fold </b></h5>
		    <p>
		    	The Rossman Fold folds in an open twisted parallel beta sheet, in which the strands lie in-between alpha helices.
		    	<br>Found <?php print $all_motif_counts['rossman'];?> times in the current database.
		    </p>
		    <p><img class="motifimage" src="./images/rossman_struktur.jpg" width="300" /></p>
		</div>
		
		<br>
		<div class="anchor" id="tim"></div>
		<div>
		    <h5><b> TIM barrel </b></h5>
		    <p>
		    	The TIM barrel includes eight beta strand and eight alpha helices.
		    	The eight parallel strands compose the inner part of the structure and the alpha helices lie around them to make up the outer part of the motif.
		    	<br>Found <?php print $all_motif_counts['tim'];?> times in the current database.
		    </p>
		    <p><img class="motifimage" src="./images/tim_struktur.jpg" width="300" /></p>
		</div>
		<?php } ?> 
