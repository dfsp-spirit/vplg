<?php

function show_the_errors($error_list) {
  if(count($error_list) > 0) {
      echo '<div class="bottom-bar" id="errors">'; 
	  foreach($error_list as $e) {
	      echo "ERROR: $e<br>\n";
	  }
      echo '</div>';
  }
}



?>