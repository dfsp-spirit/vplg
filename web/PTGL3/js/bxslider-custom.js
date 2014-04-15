$(document).ready(function() {


	var viewWidth = $(window).width();
	$('#carouselSlider').bxSlider({
		minSlide: 1,
		maxSlide: 1,
		slideWidth: viewWidth
	});
	
	$('.tada').bxSlider({
		startSlide: 0,
		controls: false,
		minSlide: 1,
		maxSlide: 1,
		slideWidth: 600,
		pagerCustom: '.bx-pager-own'
		});
	
});
 
