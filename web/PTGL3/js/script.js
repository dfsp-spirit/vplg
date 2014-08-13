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

// if checkedChains is set
if (!(typeof checkedChains === 'undefined')) {
	var selectedProteins = "";
    for(var i = 0; i < window.checkedChains.length; i++){
		chain = window.checkedChains[i];
		console.log(chain);
		selectedProteins += chain + " ";
		
		$('input:checkbox[value=' + chain + ']').prop('checked', true);
		$('#loadInput').val(selectedProteins);
	}
}

function fill_input_field(chains){
	var oldChainText = $('#loadInput').val();
	if(!(oldChainText == "")){
		var oldChains = oldChainText.split(" ");
	} else {
		var oldChains = []
	}
	
	chains = chains.concat(oldChains);
	chains.sort();
	
	var chainText = "";
	var uniqueChains = [];
	$.each(chains, function(i, el){
		if($.inArray(el, uniqueChains) === -1 && !(el == "")){
			uniqueChains.push(el);
		} 
	});
	console.log(uniqueChains);
	for(var i = 0; i < uniqueChains.length; i++){
		// if checkbox exists
		if($('input:checkbox[value=' + uniqueChains[i] + ']').length > 0){
			// and if its checked
			if($('input:checkbox[value=' + uniqueChains[i] + ']').prop('checked')){
				chainText += uniqueChains[i] + " ";
			}
		} else {
			chainText += uniqueChains[i] + " ";
		}
	}
	
	$('#loadInput').val(chainText);
}


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
		var chains = [];
		$('input[type=checkbox]').each(function () {
			if (this.checked) chains.push(this.value); })
		
		fill_input_field(chains);
	});
	
	// if clicking on "Select all protein chains...
	$('#selectAllBtn').click( function() {
		chains = [];
		var numberOfChains = 0;
		$('input[type=checkbox]').each(function () {
			numberOfChains++;
			chains.push(this.value);
		})
		
		if(numberOfChains > 20){
			var r = confirm("You selected more than 20 chains. Loading could take a while. Are you sure to proceed?");
			if(r == true) {
				fill_input_field(chains);
				$('input[type=checkbox]').each(function () {
					$(this).prop('checked', true);
				})
			} else {
				$('input[type=checkbox]').each(function () {
					$(this).prop('checked', false);

				})
			} 
		} else {
			fill_input_field(chains);
			$('input[type=checkbox]').each(function () {
				$(this).prop('checked', true);
			})
		}
	});
	
	
	$('#resetBtn').click( function() {
		$('#loadInput').val("");
		$('input[type=checkbox]').each(function () {
			$(this).prop('checked', false);
		})
	});

	$('.changepage').click( function (){
		var chains = new Array();
		var proteins = new Array();
		
		$('input[type=checkbox]').each(function () {
			if (this.checked) {
				chains.push(this.value);
			}
		})

		var dataToSend = {'chains[]' : chains};	
		$.ajax({
			type: "POST",
			url: "./backend/chains_to_session.php",
			data: dataToSend,
			cache: false,
			success: function(html){	
				console.log(html);
			}
		});
				
	});


});

});
