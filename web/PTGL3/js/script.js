/*

Custom JS
============

*/
$(document).ready(function() {

/* Scrollspy */
$('body').scrollspy({ target: '.navbar-scrollspy' });
$("#addSearchKey").hide();
$('#addSearchMotif').hide();
$('#addSearchSequence').hide();


$(function() {
	
	$('#alertMe').click(function(e) {
		e.preventDefault();
		$('#successAlert').slideDown();
	});
	
	
	$('a.pop').click(function(e) {
		e.preventDefault();
	});
	
	$('a.pop').popover();
	$('[rel="tooltip"]').tooltip();
	
	$('#advancedButton').click( function() {
		$('#advancedSearch').slideToggle();
		$('#arrow').toggleClass('rotateArrow');
	});
	
	$('#additionalSearch').click(function() {
		$('#addSearchKey').fadeToggle();
		$('#flipArrow').toggleClass('rotateArrow');
	});
	
	$('#additionalSearch2').click(function() {
		$('#addSearchMotif').slideToggle();
		$('#flipArrow2').toggleClass('rotateArrow');
	});
	
	$('#additionalSearch3').click(function() {
		$('#addSearchSequence').slideToggle();
		$('#flipArrow3').toggleClass('rotateArrow');
	});
	
	
	$('.chainCheckBox').click( function() {
		var selectedProteins = "";
		$('input[type=checkbox]').each(function () {
			if (this.checked) selectedProteins += this.value + " "; })
		
		$('#loadInput').val(selectedProteins);
	});
	
	// if clicking on "Select all protein chains...
	$('#selectAllBtn').click( function() {
		var selectedProteins = "";
		var numberOfChains = 0;
		$('input[type=checkbox]').each(function () {
			numberOfChains++;
			selectedProteins += this.value + " ";
			console.log(this.id + " is now " + $(this).attr('checked'));
		})
		
		if(numberOfChains > 20){
			var r = confirm("You selected more than 20 chains. Loading could take a while. Are you sure to proceed?");
			if(r == true) {
				$('#loadInput').val(selectedProteins);
				$('input[type=checkbox]').each(function () {
					$(this).prop('checked', true);
				})
			} else {
				$('input[type=checkbox]').each(function () {
					$(this).prop('checked', false);
					console.log(this.id + " is now " + $(this).attr('checked'));
				})
			} 
		} else {
			$('#loadInput').val(selectedProteins);
				$('input[type=checkbox]').each(function () {
					$(this).prop('checked', true);
			})
		}
	});
	
	
	$('#resetBtn').click( function() {
		$('#loadInput').val("");
		$('input[type=checkbox]').each(function () {
			$(this).prop('checked', false);
			console.log(this.id + " is now " + $(this).attr('checked'));
		})
	});	
});
	


});



