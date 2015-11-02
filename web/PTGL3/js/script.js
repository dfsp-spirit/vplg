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
$('#addSearchGraphletSimilarity').hide();
$('#addSearchCustomLinnots').hide();
$('#addSearchRandom').hide();
$('#addSearchComplex').hide();

// if checkedChains is set
if (!(typeof checkedChains === 'undefined')) {
	var selectedProteins = "";
    for(var i = 0; i < window.checkedChains.length; i++){
		chain = window.checkedChains[i];
		console.log(chain);
		selectedProteins += chain;
		if(i < window.checkedChains.length -1) {
		  selectedProteins += " ";
		}
		
		$('input:checkbox[value=' + chain + ']').prop('checked', true);
		$('#loadInput').val(selectedProteins);
	}
}

$('.notation').change(function(){
    notation = $('.notation').val();
    graphtype = $('.graphtype').val();
    value = "linnot" + graphtype + notation;
    $('#sendit_linnots').attr('name', value);
})

$('.graphtype').change(function(){
    notation = $('.notation').val();
    graphtype = $('.graphtype').val();
    value = "linnot" + graphtype + notation;
    $('#sendit_linnots').attr('name', value);
})

$('#searchLinnots').change(function(){
    string = $('#searchLinnots').val();
    $('#sendit_linnots').attr('value', string);
})



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
				console.log(uniqueChains[i]);
				chainText += uniqueChains[i] + " ";
			}
		} else {
			chainText += uniqueChains[i] + " ";
		}
		
	}
	// ensure there is no trailing space
	chainText = chainText.trim();
	//alert(chainText);
	
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
	
	$('#additionalSearch4').click(function() {
		$('#addSearchGraphletSimilarity').slideToggle();
		$('#flipArrow4').toggleClass('rotateArrow');
	});
	
	$('#additionalSearch5').click(function() {
		$('#addSearchCustomLinnots').slideToggle();
		$('#flipArrow5').toggleClass('rotateArrow');
	});
	
	$('#additionalSearch6').click(function() {
		$('#addSearchRandom').slideToggle();
		$('#flipArrow6').toggleClass('rotateArrow');
	});
	$('#additionalSearchComplex').click(function() {
		$('#addSearchComplex').slideToggle();
		$('#flipArrowComplex').toggleClass('rotateArrow');
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
		if(numberOfChains > 25){
			var r = confirm("You selected more than 25 chains. Loading could take a while. Are you sure to proceed?");
			if(r == true) {
				$('input[type=checkbox]').each(function () {
					$(this).prop('checked', true);
				})
				fill_input_field(chains);
			} else {
				$('input[type=checkbox]').each(function () {
					$(this).prop('checked', false);

				})
			} 
		} else {
			$('input[type=checkbox]').each(function () {
				$(this).prop('checked', true);
			})
			fill_input_field(chains);
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
