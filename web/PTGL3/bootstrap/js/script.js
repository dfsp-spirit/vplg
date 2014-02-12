/*

My Custom JS
============

Author:  Brad Hussey
Updated: August 2013
Notes:	 Hand coded for Udemy.com

*/

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
	
	
	$('.chainCheckBox').click( function() {
		var selectedProteins = "";
		$('input[type=checkbox]').each(function () {
			var sThisVal = (this.checked ? this.value : "");
			selectedProteins += sThisVal + " ";
		})
		$('#loadInput').val(selectedProteins);
	});
	
	$('#selectAllBtn').click( function() {
		var selectedProteins = "";
		$('input[type=checkbox]').each(function () {
			selectedProteins += this.value + " ";
			$(this).attr('checked', true);
			console.log(this.id + " is now " + $(this).attr('checked'));
		})
		$('#loadInput').val(selectedProteins);
	});
	
	
	$('#resetBtn').click( function() {
		$('#loadInput').val("");
		$('input[type=checkbox]').each(function () {
			$(this).attr('checked', false);
			console.log(this.id + " is now " + $(this).attr('checked'));
		})
		
	});	
	
});