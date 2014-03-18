$(document).ready(function() {

	$("#ugSearchkms").hide();
	$("#ugSearchkms1").hide();
	$("#ugSearchkms2").hide();
	$("#ugSearchkms3").hide();
	$("#ugAdvancedSearch").hide();
	
	
	$("#ugStandardSearch").click(function() {
		$("#ugSearchkms1").fadeOut(400);
		$("#ugSearchkms2").fadeOut(400);
		$("#ugSearchkms3").fadeOut(400);
		$("#ugSearchkms").fadeOut(400);
		$("#ugAdvancedSearch").fadeToggle(400);
		});
	
	$("#ugAdvancedSearch").click(function() {
		$("#ugSearchkms1").fadeOut(400);
		$("#ugSearchkms2").fadeOut(400);
		$("#ugSearchkms3").fadeOut(400);
		$("#ugSearchkms").fadeToggle(400);
	});
	
	$("#ugSearchk").click(function() {
		$("#ugSearchkms2").hide();
		$("#ugSearchkms3").hide();
		$("#ugSearchkms1").fadeToggle(400);
	});
	
	$("#ugSearchm").click(function() {
		$("#ugSearchkms1").hide();
		$("#ugSearchkms3").hide();
		$("#ugSearchkms2").fadeToggle(400);
	});
	
	$("#ugSearchs").click(function() {
		$("#ugSearchkms1").hide();
		$("#ugSearchkms2").hide();
		$("#ugSearchkms3").fadeToggle(400);
	});
	
	
});