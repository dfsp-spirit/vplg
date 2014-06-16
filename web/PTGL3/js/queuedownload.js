/*

Custom JS
============

*/
$(document).ready(function() {
	
	$('#multidown').click( function() {
		var queue = new Array();
		$('input[type=checkbox]').each(function () {
			if (this.checked) {
				queue.push(this.value));
			}
		});	
		
		$.ajax({
    		url: '/backend/downloadFiles.php',
    		type: 'post',
    		data: {queue},
    		success: function(response) {}
		});
	});
	
});