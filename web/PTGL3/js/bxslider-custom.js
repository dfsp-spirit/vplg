$(document).ready(function() {


	var viewWidth = $(window).width();
	$('#carouselSlider').bxSlider({
		minSlide: 1,
		maxSlide: 1,
		slideWidth: viewWidth
	});
	
	$('#tada').bxSlider({
		minSlide: 1,
		maxSlide: 1,
		slideWidth: 500,
		pagerCustom: '#bx-pager'
		});
	
});
 
