graph [
  id 1
  label "VPLG Protein Graph 1zxk-B-betalig[7,7]"
  comment "date=08-JUN-05|keywords=CADHERIN, STRAND DIMER, CELL ADHESION|graphclass=protein graph|pdb_org_common=HOUSE MOUSE|title=CRYSTAL STRUCTURE OF CADHERIN8 EC1 DOMAIN|resolution=2.0|pdb_all_chains=A, B|pdb_mol_id=1|pdb_mol_name=CADHERIN-8|pdbid=1zxk|graphtype=betalig|experiment=X-RAY DIFFRACTION|chainid=B|pdb_org_sci=MUS MUSCULUS|pdb_ec_number=|header=CELL ADHESION|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "1zxk"
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
    num_residues 4
    pdb_res_start "B-6- "
    pdb_res_end "B-9- "
    dssp_res_start 104
    dssp_res_end 107
    pdb_residues_full "B-6- ,B-7- ,B-8- ,B-9- "
    aa_sequence "QMFV"
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
    dssp_res_start 117
    dssp_res_end 121
    pdb_residues_full "B-19- ,B-20- ,B-21- ,B-22- ,B-23- "
    aa_sequence "ILVGR"
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
    dssp_res_start 130
    dssp_res_end 136
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
    dssp_res_start 143
    dssp_res_end 145
    pdb_residues_full "B-48- ,B-49- ,B-50- "
    aa_sequence "FQI"
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
    dssp_res_start 151
    dssp_res_end 154
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
    dssp_res_start 165
    dssp_res_end 174
    pdb_residues_full "B-70- ,B-71- ,B-72- ,B-73- ,B-74- ,B-75- ,B-76- ,B-77- ,B-78- ,B-79- "
    aa_sequence "EYTLTAQAVD"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 8
    num_residues 7
    pdb_res_start "B-90- "
    pdb_res_end "B-96- "
    dssp_res_start 185
    dssp_res_end 191
    pdb_residues_full "B-90- ,B-91- ,B-92- ,B-93- ,B-94- ,B-95- ,B-96- "
    aa_sequence "SEFIIKV"
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
