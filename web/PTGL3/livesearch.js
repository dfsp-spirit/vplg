$(document).ready(function () {             
	function search() {
		var query_value = $('input#searchInput').val();
		$('b#search-string').html(query_value);
		if(query_value !== ''){
			$.ajax({
			type: "POST",
			url: "liveSearch.php",
			data: { query: query_value },
			cache: false,
			success: function(html){
				$("#liveSearchResults").html(html);
				}
			});
		} return false;    
	}
	


	$("input#searchInput").on("keyup", function(e) {
	// Set Timeout
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
	});
	
	$( "input#searchInput" ).focus(function() {
		$( this ).next( "span" ).css( "display", "inline" ).fadeOut( 1000 );
	});
	
	
	$(document).on('click', '.result', function(e) {
		var selectedElement = $( this ).attr( "title" );
		var selectedElement = selectedElement.split("-", 1);
		var selectedElement = selectedElement[0].trim();
		$('input#searchInput').val(selectedElement);
	});	
	
	
});