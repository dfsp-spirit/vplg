graph [
  id 1
  label "VPLG Protein Graph 3k6d-A-albe-FG0[4V,3E]"
  comment "pdbid=3k6d|graphtype=albe|chainid=A|graphclass=folding graph|foldinggraphnumber=1|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3k6d"
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
    num_residues 4
    pdb_res_start "A-7- "
    pdb_res_end "A-10- "
    dssp_res_start 7
    dssp_res_end 10
    pdb_residues_full "A-7- ,A-8- ,A-9- ,A-10- "
    aa_sequence "ISIP"
    index_in_parent_pg "0"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 1
    label "1-E"
    num_in_chain 3
    num_residues 5
    pdb_res_start "A-34- "
    pdb_res_end "A-38- "
    dssp_res_start 34
    dssp_res_end 38
    pdb_residues_full "A-34- ,A-35- ,A-36- ,A-37- ,A-38- "
    aa_sequence "KIKLY"
    index_in_parent_pg "2"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 6
    num_residues 9
    pdb_res_start "A-72- "
    pdb_res_end "A-80- "
    dssp_res_start 72
    dssp_res_end 80
    pdb_residues_full "A-72- ,A-73- ,A-74- ,A-75- ,A-76- ,A-77- ,A-78- ,A-79- ,A-80- "
    aa_sequence "SYQLQVETT"
    index_in_parent_pg "5"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 7
    num_residues 13
    pdb_res_start "A-86- "
    pdb_res_end "A-98- "
    dssp_res_start 86
    dssp_res_end 98
    pdb_residues_full "A-86- ,A-87- ,A-88- ,A-89- ,A-90- ,A-91- ,A-92- ,A-93- ,A-94- ,A-95- ,A-96- ,A-97- ,A-98- "
    aa_sequence "TIEGPVDLEILVI"
    index_in_parent_pg "6"
    sse_type "E"
    fg_notation_label "e"
  ]
  edge [
    source 0
    target 3
    label "p"
    spatial "p"
  ]
  edge [
    source 1
    target 2
    label "a"
    spatial "a"
  ]
  edge [
    source 2
    target 3
    label "a"
    spatial "a"
  ]
]
