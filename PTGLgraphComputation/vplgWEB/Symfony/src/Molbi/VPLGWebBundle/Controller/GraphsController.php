<?php

namespace Molbi\VPLGWebBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Response;


class GraphsController extends Controller
{
    
    public function showAllAction()
    {
        $conn = $this->get('database_connection');
        $graphs = $conn->fetchAll('SELECT * FROM plcc_graph');
        return $this->render('MolbiVPLGWebBundle:Graphs:showAll.html.twig', array('graphs' => $graphs));
    }

    public function graphTypeToString($gt_number) 
    {
        if($gt_number == 1) { return("alpha"); }
        elseif($gt_number == 2) { return("beta"); }
        elseif($gt_number == 3) { return("albe"); }
        elseif($gt_number == 4) { return("alphalig"); }
        elseif($gt_number == 5) { return("betalig"); }
        elseif($gt_number == 6) { return("albelig"); }
        else { return("unknown"); }
    }

    public function graphTypeToInt($gt_string) 
    {
        if($gt_string == "alpha") { return(1); }
        elseif($gt_string == "beta") { return(2); }
        elseif($gt_string == "albe") { return(3); }
        elseif($gt_string == "alphalig") { return(4); }
        elseif($gt_string == "betalig") { return(5); }
        elseif($gt_string == "albelig") { return(6); }
        else { return(0); }
    }

    public function editSQLResult(&$data)
    {
        #print "<pre>"; var_dump($data); print "</pre>";

        foreach ($data as $k => $graphdata) {
            #print "id: " . $graphdata['pdb_id'] . " ";
            $data[$k]['pdb_mid_letters'] = substr($graphdata['pdb_id'], 1, 2);
            $data[$k]['graph_type_string'] = $this->graphTypeToString($graphdata['graph_type']);
        }
    }    
    
    public function showGraphAction($pdbid, $chain, $graphtype)
    {
        $conn = $this->get('database_connection');

        #print "pdbid: $pdbid, chain: $chain, graphtype: $graphtype";
        # these need to be checked and we need to use prepared statements below
        $s_graphtype_int = $this->graphTypeToInt($graphtype);
        $s_pdbid = $pdbid;
        $s_chain = $chain;
 
        # debug: atm these are fixed here, the vars in the header are ignored
        $s_graphtype_int = 6;
        $s_pdbid = "1a0s";
        $s_chain = "P";

        #$query = "SELECT pdb_id, chain_name, graph_type FROM plcc_graphs WHERE ( pdb_id = '$s_pdbid' AND chain_name = '$s_chain' AND graph_type = $s_graphtype_int );";
        $query = "SELECT pdb_id, chain_name, graph_type FROM plcc_graphs;";
        $graphs = $conn->fetchAll($query);
        $this->editSQLResult($graphs);

        #print "<pre>"; var_dump($graphs); print "</pre>";


        return $this->render('MolbiVPLGWebBundle:Graphs:showGraph.html.twig', array('graphs' => $graphs));
    }


    
}
