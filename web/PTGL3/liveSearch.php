<?php
/* This file provides the livesearch in the XML file of currently released PDB IDs on
 * http://www.rcsb.org 
 * This script is a modiefied copy of the W3schools Ajax Search example
 */
$xmlDoc=new DOMDocument();
$xmlDoc->load("http://www.rcsb.org/pdb/rest/getCurrent");

$x=$xmlDoc->getElementsByTagName('PDB');

//get the q parameter from URL
$q=$_GET["q"];

//lookup all links from the xml file if length of q>0
if (strlen($q)>0)
{
$hint="";
for($i=0; $i<($x->length); $i++)
  {
  $y=$x->item($i)->getAttribute('structureId');
 // $z=$x->item($i)->getElementsByTagName('url');
  if ($y->item(0)->nodeType==1)
    {
    //find a link matching the search text
    if (stristr($y->item(0)->childNodes->item(0)->nodeValue,$q))
      {
      if ($hint=="")
        {
        $hint="<a href='" .
        $z->item(0)->childNodes->item(0)->nodeValue .
        "' target='_blank'>" .
        $y->item(0)->childNodes->item(0)->nodeValue . "</a>";
        }
      else
        {
        $hint=$hint . "<br /><a href='" .
        $z->item(0)->childNodes->item(0)->nodeValue .
        "' target='_blank'>" .
        $y->item(0)->childNodes->item(0)->nodeValue . "</a>";
        }
      }
    }
  }
}

// Set output to "no suggestion" if no hint were found
// or to the correct values
if ($hint=="")
  {
  $response="no suggestion";
  }
else
  {
  $response=$hint;
  }

//output the response
echo $response;
?> 