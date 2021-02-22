<?php

namespace Molbi\VPLGWebBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Response;


class ChainsController extends Controller
{
    
    public function showAllAction()
    {
        // get the chains array from the DB here and hand it over to the template in the array()
        $conn = $this->get('database_connection');
        $chains = $conn->fetchAll('SELECT * FROM plcc_chain');
        return $this->render('MolbiVPLGWebBundle:Chains:showAll.html.twig', array('chains' => $chains));
    }
}
