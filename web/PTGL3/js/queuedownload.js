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
		
		if(queue.length > 0) {	
		  $.ajax({
		  url: '/backend/downloadFiles.php',
		  type: 'post',
		  data: {queue},
		  success: function(response) {}
		  });
		}
	});
	
});