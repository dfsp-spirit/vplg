graph [
  id 1
  label "VPLG Protein Graph 3ppe-B-albelig-FG0[7V,6E]"
  comment "pdbid=3ppe|graphtype=albelig|chainid=B|graphclass=folding graph|foldinggraphnumber=1|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3ppe"
  chain_id "B"
  graph_type "albelig"
  is_protein_graph 0
  is_folding_graph 1
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
    dssp_res_start 210
    dssp_res_end 214
    pdb_residues_full "B-6- ,B-7- ,B-8- ,B-9- ,B-10- "
    aa_sequence "RMHIR"
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
    dssp_res_start 223
    dssp_res_end 227
    pdb_residues_full "B-19- ,B-20- ,B-21- ,B-22- ,B-23- "
    aa_sequence "HHVGK"
    index_in_parent_pg "1"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 3
    num_residues 7
    pdb_res_start "B-33- "
    pdb_res_end "B-39- "
    dssp_res_start 237
    dssp_res_end 243
    pdb_residues_full "B-33- ,B-34- ,B-35- ,B-36- ,B-37- ,B-38- ,B-39- "
    aa_sequence "AMYIIEG"
    index_in_parent_pg "2"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 4
    num_residues 4
    pdb_res_start "B-46- "
    pdb_res_end "B-49- "
    dssp_res_start 250
    dssp_res_end 253
    pdb_residues_full "B-46- ,B-47- ,B-48- ,B-49- "
    aa_sequence "FKVQ"
    index_in_parent_pg "3"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 5
    num_residues 4
    pdb_res_start "B-54- "
    pdb_res_end "B-57- "
    dssp_res_start 258
    dssp_res_end 261
    pdb_residues_full "B-54- ,B-55- ,B-56- ,B-57- "
    aa_sequence "DIYA"
    index_in_parent_pg "4"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 5
    label "5-E"
    num_in_chain 6
    num_residues 10
    pdb_res_start "B-68- "
    pdb_res_end "B-77- "
    dssp_res_start 272
    dssp_res_end 281
    pdb_residues_full "B-68- ,B-69- ,B-70- ,B-71- ,B-72- ,B-73- ,B-74- ,B-75- ,B-76- ,B-77- "
    aa_sequence "EYELTAHIID"
    index_in_parent_pg "5"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 7
    num_residues 8
    pdb_res_start "B-88- "
    pdb_res_end "B-95- "
    dssp_res_start 292
    dssp_res_end 299
    pdb_residues_full "B-88- ,B-89- ,B-90- ,B-91- ,B-92- ,B-93- ,B-94- ,B-95- "
    aa_sequence "SKFIIKVS"
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
