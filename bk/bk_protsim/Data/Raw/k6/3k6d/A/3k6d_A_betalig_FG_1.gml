graph [
  id 1
  label "VPLG Protein Graph 3k6d-A-betalig-FG1[3V,2E]"
  comment "pdbid=3k6d|graphtype=betalig|chainid=A|graphclass=folding graph|foldinggraphnumber=2|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3k6d"
  chain_id "A"
  graph_type "betalig"
  is_protein_graph 0
  is_folding_graph 1
  is_SSE_graph 1
  is_AA_graph 0
  is_all_chains_graph 0
  node [
    id 0
    label "0-E"
    num_in_chain 2
    num_residues 5
    pdb_res_start "A-19- "
    pdb_res_end "A-23- "
    dssp_res_start 19
    dssp_res_end 23
    pdb_residues_full "A-19- ,A-20- ,A-21- ,A-22- ,A-23- "
    aa_sequence "KIVGR"
    index_in_parent_pg "1"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 1
    label "1-E"
    num_in_chain 4
    num_residues 3
    pdb_res_start "A-50- "
    pdb_res_end "A-52- "
    dssp_res_start 50
    dssp_res_end 52
    pdb_residues_full "A-50- ,A-51- ,A-52- "
    aa_sequence "FKI"
    index_in_parent_pg "3"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 5
    num_residues 4
    pdb_res_start "A-58- "
    pdb_res_end "A-61- "
    dssp_res_start 58
    dssp_res_end 61
    pdb_residues_full "A-58- ,A-59- ,A-60- ,A-61- "
    aa_sequence "EVSV"
    index_in_parent_pg "4"
    sse_type "E"
    fg_notation_label "e"
  ]
  edge [
    source 0
    target 2
    label "a"
    spatial "a"
  ]
  edge [
    source 1
    target 2
    label "a"
    spatial "a"
  ]
]
