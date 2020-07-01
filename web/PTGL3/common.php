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

/* Log with php to the browser console (https://stackify.com/how-to-log-to-console-in-php/)
Example usage:
	<?= console_log("test"); ?>  // <?= $x ?> means <? echo $x ?>
*/
function console_log($output, $with_script_tags = true) {
    $js_code = 'console.log(' . json_encode($output, JSON_HEX_TAG) . ');';
    if ($with_script_tags) {
        $js_code = '<script>' . $js_code . '</script>';
    }
    echo $js_code;
}

?>