graph [
  id 1
  label "VPLG Protein Graph 1zvn-B-betalig[7,7]"
  comment "date=02-JUN-05|keywords=CADHERIN, CELL ADHESION|graphclass=protein graph|pdb_org_common=CHICKEN|title=CRYSTAL STRUCTURE OF CHICK MN-CADHERIN EC1|resolution=2.16|pdb_all_chains=A, B|pdb_mol_id=1|pdb_mol_name=CADHERIN 1|pdbid=1zvn|graphtype=betalig|experiment=X-RAY DIFFRACTION|chainid=B|pdb_org_sci=GALLUS GALLUS|pdb_ec_number=|header=CELL ADHESION|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "1zvn"
  chain_id "B"
  graph_type "betalig"
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
    pdb_res_start "B-6- "
    pdb_res_end "B-10- "
    dssp_res_start 107
    dssp_res_end 111
    pdb_residues_full "B-6- ,B-7- ,B-8- ,B-9- ,B-10- "
    aa_sequence "QFFVL"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 1
    label "1-E"
    num_in_chain 3
    num_residues 5
    pdb_res_start "B-19- "
    pdb_res_end "B-23- "
    dssp_res_start 120
    dssp_res_end 124
    pdb_residues_full "B-19- ,B-20- ,B-21- ,B-22- ,B-23- "
    aa_sequence "LYVGK"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 4
    num_residues 7
    pdb_res_start "B-35- "
    pdb_res_end "B-41- "
    dssp_res_start 136
    dssp_res_end 142
    pdb_residues_full "B-35- ,B-36- ,B-37- ,B-38- ,B-39- ,B-40- ,B-41- "
    aa_sequence "IKYILSG"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 5
    num_residues 3
    pdb_res_start "B-48- "
    pdb_res_end "B-50- "
    dssp_res_start 149
    dssp_res_end 151
    pdb_residues_full "B-48- ,B-49- ,B-50- "
    aa_sequence "FTI"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 6
    num_residues 4
    pdb_res_start "B-56- "
    pdb_res_end "B-59- "
    dssp_res_start 157
    dssp_res_end 160
    pdb_residues_full "B-56- ,B-57- ,B-58- ,B-59- "
    aa_sequence "DIHA"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 5
    label "5-E"
    num_in_chain 7
    num_residues 10
    pdb_res_start "B-70- "
    pdb_res_end "B-79- "
    dssp_res_start 171
    dssp_res_end 180
    pdb_residues_full "B-70- ,B-71- ,B-72- ,B-73- ,B-74- ,B-75- ,B-76- ,B-77- ,B-78- ,B-79- "
    aa_sequence "QYTLRAQALD"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 8
    num_residues 8
    pdb_res_start "B-90- "
    pdb_res_end "B-97- "
    dssp_res_start 191
    dssp_res_end 198
    pdb_residues_full "B-90- ,B-91- ,B-92- ,B-93- ,B-94- ,B-95- ,B-96- ,B-97- "
    aa_sequence "SEFIIKIQ"
    sse_type "E"
    fg_notation_label "e"
  ]
  edge [
    source 0
    target 1
    label "a"
    spatial "a"
  ]
  edge [
    source 0
    target 6
    label "p"
    spatial "p"
  ]
  edge [
    source 1
    target 4
    label "a"
    spatial "a"
  ]
  edge [
    source 2
    target 3
    label "a"
    spatial "a"
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
    source 5
    target 6
    label "a"
    spatial "a"
  ]
]
