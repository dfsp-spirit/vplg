graph [
  id 1
  label "VPLG Protein Graph 3k6f-B-albe[7,7]"
  comment "date=08-OCT-09|keywords=T-CADHERIN, CELL ADHESION, CALCIUM, CELL MEMBRANE, CLEAVAGEON PAIR OF BASIC RESIDUES, GLYCOPROTEIN, GPI-ANCHOR,LIPOPROTEIN, MEMBRANE|graphclass=protein graph|pdb_org_common=MOUSE|title=CRYSTAL STRUCTURE OF MOUSE T-CADHERIN EC1|resolution=1.81|pdb_all_chains=A, B|pdb_mol_id=1|pdb_mol_name=T-CADHERIN|pdbid=3k6f|graphtype=albe|experiment=X-RAY DIFFRACTION|chainid=B|pdb_org_sci=MUS MUSCULUS|pdb_ec_number=|header=CELL ADHESION|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3k6f"
  chain_id "B"
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
    num_residues 4
    pdb_res_start "B-7- "
    pdb_res_end "B-10- "
    dssp_res_start 109
    dssp_res_end 112
    pdb_residues_full "B-7- ,B-8- ,B-9- ,B-10- "
    aa_sequence "ILIP"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 1
    label "1-E"
    num_in_chain 2
    num_residues 5
    pdb_res_start "B-19- "
    pdb_res_end "B-23- "
    dssp_res_start 121
    dssp_res_end 125
    pdb_residues_full "B-19- ,B-20- ,B-21- ,B-22- ,B-23- "
    aa_sequence "RDVGK"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 3
    num_residues 6
    pdb_res_start "B-34- "
    pdb_res_end "B-39- "
    dssp_res_start 136
    dssp_res_end 141
    pdb_residues_full "B-34- ,B-35- ,B-36- ,B-37- ,B-38- ,B-39- "
    aa_sequence "KFRLTG"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 4
    num_residues 3
    pdb_res_start "B-50- "
    pdb_res_end "B-52- "
    dssp_res_start 152
    dssp_res_end 154
    pdb_residues_full "B-50- ,B-51- ,B-52- "
    aa_sequence "FRI"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 5
    num_residues 4
    pdb_res_start "B-58- "
    pdb_res_end "B-61- "
    dssp_res_start 160
    dssp_res_end 163
    pdb_residues_full "B-58- ,B-59- ,B-60- ,B-61- "
    aa_sequence "SVSV"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 5
    label "5-E"
    num_in_chain 6
    num_residues 9
    pdb_res_start "B-72- "
    pdb_res_end "B-80- "
    dssp_res_start 174
    dssp_res_end 182
    pdb_residues_full "B-72- ,B-73- ,B-74- ,B-75- ,B-76- ,B-77- ,B-78- ,B-79- ,B-80- "
    aa_sequence "TYQLYVETT"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 7
    num_residues 8
    pdb_res_start "B-91- "
    pdb_res_end "B-98- "
    dssp_res_start 193
    dssp_res_end 200
    pdb_residues_full "B-91- ,B-92- ,B-93- ,B-94- ,B-95- ,B-96- ,B-97- ,B-98- "
    aa_sequence "VPLEVIVI"
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
