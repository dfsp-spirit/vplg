graph [
  id 1
  label "VPLG Protein Graph 3ppe-A-albe-FG0[7V,6E]"
  comment "pdbid=3ppe|graphtype=albe|chainid=A|graphclass=folding graph|foldinggraphnumber=1|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3ppe"
  chain_id "A"
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
    num_residues 5
    pdb_res_start "A-6- "
    pdb_res_end "A-10- "
    dssp_res_start 6
    dssp_res_end 10
    pdb_residues_full "A-6- ,A-7- ,A-8- ,A-9- ,A-10- "
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
    pdb_res_start "A-19- "
    pdb_res_end "A-23- "
    dssp_res_start 19
    dssp_res_end 23
    pdb_residues_full "A-19- ,A-20- ,A-21- ,A-22- ,A-23- "
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
    pdb_res_start "A-33- "
    pdb_res_end "A-39- "
    dssp_res_start 33
    dssp_res_end 39
    pdb_residues_full "A-33- ,A-34- ,A-35- ,A-36- ,A-37- ,A-38- ,A-39- "
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
    pdb_res_start "A-46- "
    pdb_res_end "A-49- "
    dssp_res_start 46
    dssp_res_end 49
    pdb_residues_full "A-46- ,A-47- ,A-48- ,A-49- "
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
    pdb_res_start "A-54- "
    pdb_res_end "A-57- "
    dssp_res_start 54
    dssp_res_end 57
    pdb_residues_full "A-54- ,A-55- ,A-56- ,A-57- "
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
    pdb_res_start "A-68- "
    pdb_res_end "A-77- "
    dssp_res_start 68
    dssp_res_end 77
    pdb_residues_full "A-68- ,A-69- ,A-70- ,A-71- ,A-72- ,A-73- ,A-74- ,A-75- ,A-76- ,A-77- "
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
    pdb_res_start "A-88- "
    pdb_res_end "A-95- "
    dssp_res_start 88
    dssp_res_end 95
    pdb_residues_full "A-88- ,A-89- ,A-90- ,A-91- ,A-92- ,A-93- ,A-94- ,A-95- "
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
