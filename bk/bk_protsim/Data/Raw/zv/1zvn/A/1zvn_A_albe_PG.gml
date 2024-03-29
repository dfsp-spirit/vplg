graph [
  id 1
  label "VPLG Protein Graph 1zvn-A-albe[8,8]"
  comment "date=02-JUN-05|keywords=CADHERIN, CELL ADHESION|graphclass=protein graph|pdb_org_common=CHICKEN|title=CRYSTAL STRUCTURE OF CHICK MN-CADHERIN EC1|resolution=2.16|pdb_all_chains=A, B|pdb_mol_id=1|pdb_mol_name=CADHERIN 1|pdbid=1zvn|graphtype=albe|experiment=X-RAY DIFFRACTION|chainid=A|pdb_org_sci=GALLUS GALLUS|pdb_ec_number=|header=CELL ADHESION|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "1zvn"
  chain_id "A"
  graph_type "albe"
  is_protein_graph 1
  is_folding_graph 0
  is_SSE_graph 1
  is_AA_graph 0
  is_all_chains_graph 0
  node [
    id 0
    label "0-E"
    num_in_chain 1
    num_residues 5
    pdb_res_start "A-6- "
    pdb_res_end "A-10- "
    dssp_res_start 7
    dssp_res_end 11
    pdb_residues_full "A-6- ,A-7- ,A-8- ,A-9- ,A-10- "
    aa_sequence "QFFVL"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 1
    label "1-H"
    num_in_chain 2
    num_residues 3
    pdb_res_start "A-11- "
    pdb_res_end "A-13- "
    dssp_res_start 12
    dssp_res_end 14
    pdb_residues_full "A-11- ,A-12- ,A-13- "
    aa_sequence "EEY"
    sse_type "H"
    fg_notation_label "h"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 3
    num_residues 5
    pdb_res_start "A-19- "
    pdb_res_end "A-23- "
    dssp_res_start 20
    dssp_res_end 24
    pdb_residues_full "A-19- ,A-20- ,A-21- ,A-22- ,A-23- "
    aa_sequence "LYVGK"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 4
    num_residues 7
    pdb_res_start "A-35- "
    pdb_res_end "A-41- "
    dssp_res_start 36
    dssp_res_end 42
    pdb_residues_full "A-35- ,A-36- ,A-37- ,A-38- ,A-39- ,A-40- ,A-41- "
    aa_sequence "IKYILSG"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 5
    num_residues 3
    pdb_res_start "A-48- "
    pdb_res_end "A-50- "
    dssp_res_start 49
    dssp_res_end 51
    pdb_residues_full "A-48- ,A-49- ,A-50- "
    aa_sequence "FTI"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 5
    label "5-E"
    num_in_chain 6
    num_residues 4
    pdb_res_start "A-56- "
    pdb_res_end "A-59- "
    dssp_res_start 57
    dssp_res_end 60
    pdb_residues_full "A-56- ,A-57- ,A-58- ,A-59- "
    aa_sequence "DIHA"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 7
    num_residues 10
    pdb_res_start "A-70- "
    pdb_res_end "A-79- "
    dssp_res_start 71
    dssp_res_end 80
    pdb_residues_full "A-70- ,A-71- ,A-72- ,A-73- ,A-74- ,A-75- ,A-76- ,A-77- ,A-78- ,A-79- "
    aa_sequence "QYTLRAQALD"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 7
    label "7-E"
    num_in_chain 8
    num_residues 8
    pdb_res_start "A-90- "
    pdb_res_end "A-97- "
    dssp_res_start 91
    dssp_res_end 98
    pdb_residues_full "A-90- ,A-91- ,A-92- ,A-93- ,A-94- ,A-95- ,A-96- ,A-97- "
    aa_sequence "SEFIIKIQ"
    sse_type "E"
    fg_notation_label "e"
  ]
  edge [
    source 0
    target 1
    label "m"
    spatial "m"
  ]
  edge [
    source 0
    target 2
    label "a"
    spatial "a"
  ]
  edge [
    source 0
    target 7
    label "p"
    spatial "p"
  ]
  edge [
    source 2
    target 5
    label "a"
    spatial "a"
  ]
  edge [
    source 3
    target 4
    label "a"
    spatial "a"
  ]
  edge [
    source 3
    target 6
    label "a"
    spatial "a"
  ]
  edge [
    source 4
    target 5
    label "a"
    spatial "a"
  ]
  edge [
    source 6
    target 7
    label "a"
    spatial "a"
  ]
]
