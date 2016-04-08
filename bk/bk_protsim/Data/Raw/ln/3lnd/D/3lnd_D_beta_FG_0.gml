graph [
  id 1
  label "VPLG Protein Graph 3lnd-D-beta-FG0[7V,6E]"
  comment "pdbid=3lnd|graphtype=beta|chainid=D|graphclass=folding graph|foldinggraphnumber=1|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3lnd"
  chain_id "D"
  graph_type "beta"
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
    pdb_res_start "D-7- "
    pdb_res_end "D-10- "
    dssp_res_start 612
    dssp_res_end 615
    pdb_residues_full "D-7- ,D-8- ,D-9- ,D-10- "
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
    pdb_res_start "D-19- "
    pdb_res_end "D-23- "
    dssp_res_start 624
    dssp_res_end 628
    pdb_residues_full "D-19- ,D-20- ,D-21- ,D-22- ,D-23- "
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
    pdb_res_start "D-35- "
    pdb_res_end "D-41- "
    dssp_res_start 640
    dssp_res_end 646
    pdb_residues_full "D-35- ,D-36- ,D-37- ,D-38- ,D-39- ,D-40- ,D-41- "
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
    pdb_res_start "D-48- "
    pdb_res_end "D-50- "
    dssp_res_start 653
    dssp_res_end 655
    pdb_residues_full "D-48- ,D-49- ,D-50- "
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
    pdb_res_start "D-56- "
    pdb_res_end "D-59- "
    dssp_res_start 661
    dssp_res_end 664
    pdb_residues_full "D-56- ,D-57- ,D-58- ,D-59- "
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
    pdb_res_start "D-70- "
    pdb_res_end "D-79- "
    dssp_res_start 675
    dssp_res_end 684
    pdb_residues_full "D-70- ,D-71- ,D-72- ,D-73- ,D-74- ,D-75- ,D-76- ,D-77- ,D-78- ,D-79- "
    aa_sequence "VYILRAQAVN"
    index_in_parent_pg "5"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 7
    num_residues 13
    pdb_res_start "D-85- "
    pdb_res_end "D-97- "
    dssp_res_start 690
    dssp_res_end 702
    pdb_residues_full "D-85- ,D-86- ,D-87- ,D-88- ,D-89- ,D-90- ,D-91- ,D-92- ,D-93- ,D-94- ,D-95- ,D-96- ,D-97- "
    aa_sequence "PVEPESEFIIKIH"
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
