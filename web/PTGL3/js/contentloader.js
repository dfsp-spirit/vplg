$(document).ready(function () {             

function loadNext() {
		
		slider = $('#carouselSlider').bxSlider();
		var currentSlide = slider.getCurrentSlide();
		var dataToSend = {'currentSlide' : currentSlide, 'chainIDs[]' : <?php echo json_encode($allChainIDs); ?> } ;
		
		//Main Images
			$.ajax({
			type: "POST",
			url: "./backend/getImages.php",
			data: dataToSend
			cache: false,
			success: function(html){
				alert("successssss");
				$("#7timB").html(html);
				slider.reloadSlider({
    				startSlide: currentSlide + 1
    			});
			}
			});
		


		//	Thumbs / Pager
		if(query_value !== ''){
			$.ajax({
			type: "POST",
			url: "./backend/getImages.php",
			data: { query: query_value },
			cache: false,
			success: function(html){
				alert("success");
				$("#7timB").html(html);
				}
			});
		} return false;    
	}	


	var steps = 0;
	var prevsteps = 0;
	$('.bx-next').click( function() {
		steps++;
		if(steps % 1 == 0 && steps > prevsteps){
			loadNext();
			prevsteps = steps;
		}
	});

	$('.bx-prev').click( function() {
		steps--;
	});	
});