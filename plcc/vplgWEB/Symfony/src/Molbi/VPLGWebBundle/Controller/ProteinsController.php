<?php

namespace Molbi\VPLGWebBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Response;


class ProteinsController extends Controller
{
    
    public function showAllAction()
    {
        $conn = $this->get('database_connection');
        $proteins = $conn->fetchAll('SELECT * FROM plcc_protein');
        return $this->render('MolbiVPLGWebBundle:Proteins:showAll.html.twig', array('proteins' => $proteins));
    }
}
