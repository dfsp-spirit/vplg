graph [
  id 1
  label "VPLG Protein Graph 3lnd-C-albelig-FG0[7V,7E]"
  comment "pdbid=3lnd|graphtype=albelig|chainid=C|graphclass=folding graph|foldinggraphnumber=1|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3lnd"
  chain_id "C"
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
    pdb_res_start "C-6- "
    pdb_res_end "C-10- "
    dssp_res_start 407
    dssp_res_end 411
    pdb_residues_full "C-6- ,C-7- ,C-8- ,C-9- ,C-10- "
    aa_sequence "QFFLL"
    index_in_parent_pg "0"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 1
    label "1-E"
    num_in_chain 2
    num_residues 5
    pdb_res_start "C-19- "
    pdb_res_end "C-23- "
    dssp_res_start 420
    dssp_res_end 424
    pdb_residues_full "C-19- ,C-20- ,C-21- ,C-22- ,C-23- "
    aa_sequence "QYVGK"
    index_in_parent_pg "1"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 3
    num_residues 7
    pdb_res_start "C-35- "
    pdb_res_end "C-41- "
    dssp_res_start 436
    dssp_res_end 442
    pdb_residues_full "C-35- ,C-36- ,C-37- ,C-38- ,C-39- ,C-40- ,C-41- "
    aa_sequence "LKYILSG"
    index_in_parent_pg "2"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 4
    num_residues 3
    pdb_res_start "C-48- "
    pdb_res_end "C-50- "
    dssp_res_start 449
    dssp_res_end 451
    pdb_residues_full "C-48- ,C-49- ,C-50- "
    aa_sequence "FII"
    index_in_parent_pg "3"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 5
    num_residues 4
    pdb_res_start "C-56- "
    pdb_res_end "C-59- "
    dssp_res_start 457
    dssp_res_end 460
    pdb_residues_full "C-56- ,C-57- ,C-58- ,C-59- "
    aa_sequence "DIQA"
    index_in_parent_pg "4"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 5
    label "5-E"
    num_in_chain 6
    num_residues 10
    pdb_res_start "C-70- "
    pdb_res_end "C-79- "
    dssp_res_start 471
    dssp_res_end 480
    pdb_residues_full "C-70- ,C-71- ,C-72- ,C-73- ,C-74- ,C-75- ,C-76- ,C-77- ,C-78- ,C-79- "
    aa_sequence "VYILRAQAVN"
    index_in_parent_pg "5"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 7
    num_residues 8
    pdb_res_start "C-90- "
    pdb_res_end "C-97- "
    dssp_res_start 491
    dssp_res_end 498
    pdb_residues_full "C-90- ,C-91- ,C-92- ,C-93- ,C-94- ,C-95- ,C-96- ,C-97- "
    aa_sequence "SEFIIKIH"
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
