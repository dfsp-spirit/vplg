graph [
  id 1
  label "VPLG Protein Graph 3lnd-A-albelig-FG0[6V,5E]"
  comment "pdbid=3lnd|graphtype=albelig|chainid=A|graphclass=folding graph|foldinggraphnumber=1|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3lnd"
  chain_id "A"
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
    num_residues 4
    pdb_res_start "A-7- "
    pdb_res_end "A-10- "
    dssp_res_start 3
    dssp_res_end 6
    pdb_residues_full "A-7- ,A-8- ,A-9- ,A-10- "
    aa_sequence "FFLL"
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
    dssp_res_start 15
    dssp_res_end 19
    pdb_residues_full "A-19- ,A-20- ,A-21- ,A-22- ,A-23- "
    aa_sequence "QYVGK"
    index_in_parent_pg "1"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 3
    num_residues 3
    pdb_res_start "A-48- "
    pdb_res_end "A-50- "
    dssp_res_start 44
    dssp_res_end 46
    pdb_residues_full "A-48- ,A-49- ,A-50- "
    aa_sequence "FII"
    index_in_parent_pg "2"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 4
    num_residues 4
    pdb_res_start "A-56- "
    pdb_res_end "A-59- "
    dssp_res_start 52
    dssp_res_end 55
    pdb_residues_full "A-56- ,A-57- ,A-58- ,A-59- "
    aa_sequence "DIQA"
    index_in_parent_pg "3"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 6
    num_residues 3
    pdb_res_start "A-70- "
    pdb_res_end "A-72- "
    dssp_res_start 66
    dssp_res_end 68
    pdb_residues_full "A-70- ,A-71- ,A-72- "
    aa_sequence "VYI"
    index_in_parent_pg "5"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 5
    label "5-E"
    num_in_chain 7
    num_residues 5
    pdb_res_start "A-93- "
    pdb_res_end "A-97- "
    dssp_res_start 86
    dssp_res_end 90
    pdb_residues_full "A-93- ,A-94- ,A-95- ,A-96- ,A-97- "
    aa_sequence "IIKIH"
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
    target 5
    label "p"
    spatial "p"
  ]
  edge [
    source 1
    target 3
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
    source 4
    target 5
    label "p"
    spatial "p"
  ]
]
