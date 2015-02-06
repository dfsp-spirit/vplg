$(document).ready(function () {             
	function search() {
		var query_value = $('input#searchInput').val();
		$('b#search-string').html(query_value);
		if(query_value !== ''){
			$.ajax({
			type: "POST",
			url: "./backend/liveSearch.php",
			data: { query: query_value },
			cache: false,
			success: function(html){
				$("#liveSearchResults").html(html);
				}
			});
		} return false;    
	}
	
	window.displayBoxIndex = -1;
	var Navigate = function(diff) {
		displayBoxIndex += diff;
		var oBoxCollection = $(".result");
		if (displayBoxIndex >= oBoxCollection.length)
			displayBoxIndex = 0;
		if (displayBoxIndex < 0)
			displayBoxIndex = oBoxCollection.length - 1;
		var cssClass = "resultHover";
		oBoxCollection.removeClass(cssClass).eq(displayBoxIndex).addClass(cssClass);
	}

	$("input#searchInput").on("keyup", function(e) {
		switch(e.keyCode) {
		case 40:
			Navigate(1);
			break;
		case 38:
			Navigate(-1);
			break;
		default:
			clearTimeout($.data(this, 'timer'));
			var search_string = $(this).val();
			if (search_string == '') {
				$("#liveSearchResults").fadeOut();
				//$('h4#results-text').fadeOut();
			} else {
				$("#liveSearchResults").fadeIn();
				//$('h4#results-text').fadeIn();
				$(this).data('timer', setTimeout(search, 100));
			};
		}
	});
	
	$( "input#searchInput" ).focus(function() {
		$( this ).next( "span" ).css( "display", "inline" ).fadeOut( 1000 );
	});
	


	$(document).on('click', '.result', function(e) {
		var selectedElement = $( this ).attr( "title" );
		var selectedElement = selectedElement.split("-", 1);
		var selectedElement = selectedElement[0].trim();
		$('input#searchInput').val(selectedElement);
                $('#liveSearchResults').hide();
	});	
	
	$(document).on('keyup', '.result', function(e) {
		if (e.keyCode ==	13) {
			var selectedElement = $(".resultHover").attr( "title" );
			var selectedElement = selectedElement.split("-", 1);
			var selectedElement = selectedElement[0].trim();
			$('input#searchInput').val(selectedElement);
		}
	});
	
	$('#sendit_advanced').click(function(e) {
 		content = $('#searchInput').val();
		pdb_content = $('#pdbid').val();
		title_content = $('#title').val();
		ligandname_con = $('#ligandname').val();
		molecule_con = $('#molecule').val();
		
                if(pdb_content.length > 0 && pdb_content.length < 4){
                    e.preventDefault();
                    alert("PDB ID must be 4 characters long!");
                } else if(content.length < 3 && pdb_content.length < 3 && 
		   title_content.length < 3 && ligandname_con.length < 3 && 
		   molecule_con.length < 3){
 			e.preventDefault();
 			alert("Please enter at least 3 characters!");
 		}
 	});
	
	$('#sendit_graphlets').click(function(e) {
 		content = $('#searchGraphlets').val();
		
		
 		if(content.length != 5){
 			e.preventDefault();
 			alert("Please enter exactly 5 characters, e.g., '7timA' for PDB 7tim chain A.");
 		}
 	});
	
	$('#sendit_nav').click(function(e) {
 		content = $('#searchInput').val();
 		if(content.length < 3){
 			e.preventDefault();
 			alert("Please enter at least 3 characters!");
 		}
 	});
	
	/* The button on the 'Retrieve -> All PGs of chain' page.        */
	$('#sendit_all_pgs_of_chain').click(function(e) {
 		content = $('#search_pgs_of_chain_pdbchain').val();
 		if(content.length != 5){
 			e.preventDefault();
 			alert("Please enter exactly 5 characters, e.g., '7timA' to query for PDB ID 7tim chain A.");
 		}
 	});

        /* The button on the 'Retrieve -> All FGs of a PG' page.        */
	$('#sendit_all_fgs_of_pg').click(function(e) {
 		content = $('#search_fgs_of_pg_pdbchain').val();
 		if(content.length != 5){
 			e.preventDefault();
 			alert("Please enter exactly 5 characters, e.g., '7timA' to query for PDB ID 7tim chain A.");
 		}
 	});

	/* The button on the 'Retrieve -> All visualizations of FG' page.        */
	$('#sendit_all_vis_of_fg').click(function(e) {
 		content = $('#search_vis_of_fg_pdbchain').val();
 		if(content.length != 5){
 			e.preventDefault();
 			alert("Please enter exactly 5 characters, e.g., '7timA' to query for PDB ID 7tim chain A.");
 		}
 	});	
	
	
	 
});