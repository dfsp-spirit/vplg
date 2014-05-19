$(document).ready(function() {


	var viewWidth = $(window).width();
	var slider = $('#carouselSlider').bxSlider({
		minSlide: 1,
		maxSlide: 1,
		slideWidth: viewWidth,
		infiniteLoop: false,
		hideControlOnEnd: true
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
 
