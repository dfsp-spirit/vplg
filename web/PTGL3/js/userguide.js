$(document).ready(function() {

	// $("#ugSearchkms").hide();
	// $("#ugSearchkms1").hide();
	$("#ugSearchkms2").hide(); // Hide the motif description
	// $("#ugSearchkms3").hide();
	// $("#ugAdvancedSearch").hide();
	
	/*
	$("#ugStandardSearch").click(function() {
		$("#ugSearchkms1").fadeOut(400);
		$("#ugSearchkms2").fadeOut(400);
		$("#ugSearchkms3").fadeOut(400);
		$("#ugSearchkms").fadeOut(400);
		$("#ugAdvancedSearch").scrollTop(3000);
		$("#ugAdvancedSearch").fadeToggle(400);
		$("html, body").animate({ scrollTop: $('#scroll1').offset().top }, 1000)
		});
	*/
	
	/*
	$("#ugAdvancedSearch").click(function() {
		$("#ugSearchkms1").fadeOut(400);
		$("#ugSearchkms2").fadeOut(400);
		$("#ugSearchkms3").fadeOut(400);
		$("#ugSearchkms").fadeToggle(400);
		
		
		$("html, body").animate({ scrollTop: $('#scroll2').offset().top}, 1000)
		});
	*/
	
	$("#ugSearchk").click(function() {
		$("#ugSearchkms2").hide();
		$("#ugSearchkms3").hide();
		$("#ugSearchkms1").fadeToggle(400);
		$("html, body").animate({ scrollTop: $('#scroll3').offset().top }, 1000);
	});
	
	$("#ugSearchm").click(function() {
		$("#ugSearchkms1").hide();
		$("#ugSearchkms3").hide();
		$("#ugSearchkms2").fadeToggle(400);
		$("html, body").animate({ scrollTop: $('#scroll3').offset().top }, 1000);
	});
	
	$("#ugSearchs").click(function() {
		$("#ugSearchkms1").hide();
		$("#ugSearchkms2").hide();
		$("#ugSearchkms3").fadeToggle(400);
		$("html, body").animate({ scrollTop: $('#scroll3').offset().top }, 1000);
	});
	
	
	
});