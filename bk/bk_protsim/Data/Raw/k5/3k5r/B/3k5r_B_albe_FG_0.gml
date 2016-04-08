graph [
  id 1
  label "VPLG Protein Graph 3k5r-B-albe-FG0[7V,7E]"
  comment "pdbid=3k5r|graphtype=albe|chainid=B|graphclass=folding graph|foldinggraphnumber=1|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3k5r"
  chain_id "B"
  graph_type "albe"
  is_protein_graph 0
  is_folding_graph 1
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
    dssp_res_start 227
    dssp_res_end 230
    pdb_residues_full "B-7- ,B-8- ,B-9- ,B-10- "
    aa_sequence "ILIP"
    index_in_parent_pg "0"
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
    dssp_res_start 239
    dssp_res_end 243
    pdb_residues_full "B-19- ,B-20- ,B-21- ,B-22- ,B-23- "
    aa_sequence "RDVGK"
    index_in_parent_pg "1"
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
    dssp_res_start 254
    dssp_res_end 259
    pdb_residues_full "B-34- ,B-35- ,B-36- ,B-37- ,B-38- ,B-39- "
    aa_sequence "KFRLTG"
    index_in_parent_pg "2"
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
    dssp_res_start 270
    dssp_res_end 272
    pdb_residues_full "B-50- ,B-51- ,B-52- "
    aa_sequence "FRI"
    index_in_parent_pg "3"
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
    dssp_res_start 278
    dssp_res_end 281
    pdb_residues_full "B-58- ,B-59- ,B-60- ,B-61- "
    aa_sequence "SVSV"
    index_in_parent_pg "4"
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
    dssp_res_start 292
    dssp_res_end 300
    pdb_residues_full "B-72- ,B-73- ,B-74- ,B-75- ,B-76- ,B-77- ,B-78- ,B-79- ,B-80- "
    aa_sequence "TYQLYVETT"
    index_in_parent_pg "5"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 7
    num_residues 13
    pdb_res_start "B-86- "
    pdb_res_end "B-98- "
    dssp_res_start 306
    dssp_res_end 318
    pdb_residues_full "B-86- ,B-87- ,B-88- ,B-89- ,B-90- ,B-91- ,B-92- ,B-93- ,B-94- ,B-95- ,B-96- ,B-97- ,B-98- "
    aa_sequence "TLEGPVPLEVIVI"
    index_in_parent_pg "6"
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
