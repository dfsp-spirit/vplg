<?php

namespace Molbi\VPLGWebBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Response;


class DefaultController extends Controller
{
    
    public function indexAction()
    {
        return $this->render('MolbiVPLGWebBundle:Default:index.html.twig', array());
	//return new Response('<html><body>Hello!</body></html>');
    }
}
