graph [
  id 1
  label "VPLG Protein Graph 2a4e-A-albe-FG0[7V,6E]"
  comment "pdbid=2a4e|graphtype=albe|chainid=A|graphclass=folding graph|foldinggraphnumber=1|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "2a4e"
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
    dssp_res_start 7
    dssp_res_end 11
    pdb_residues_full "A-6- ,A-7- ,A-8- ,A-9- ,A-10- "
    aa_sequence "QFFVI"
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
    dssp_res_start 20
    dssp_res_end 24
    pdb_residues_full "A-19- ,A-20- ,A-21- ,A-22- ,A-23- "
    aa_sequence "VLVGR"
    index_in_parent_pg "1"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 3
    num_residues 7
    pdb_res_start "A-35- "
    pdb_res_end "A-41- "
    dssp_res_start 36
    dssp_res_end 42
    pdb_residues_full "A-35- ,A-36- ,A-37- ,A-38- ,A-39- ,A-40- ,A-41- "
    aa_sequence "IKYILSG"
    index_in_parent_pg "2"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 4
    num_residues 3
    pdb_res_start "A-48- "
    pdb_res_end "A-50- "
    dssp_res_start 49
    dssp_res_end 51
    pdb_residues_full "A-48- ,A-49- ,A-50- "
    aa_sequence "FVI"
    index_in_parent_pg "3"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 5
    num_residues 4
    pdb_res_start "A-56- "
    pdb_res_end "A-59- "
    dssp_res_start 57
    dssp_res_end 60
    pdb_residues_full "A-56- ,A-57- ,A-58- ,A-59- "
    aa_sequence "NIHA"
    index_in_parent_pg "4"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 5
    label "5-E"
    num_in_chain 6
    num_residues 10
    pdb_res_start "A-70- "
    pdb_res_end "A-79- "
    dssp_res_start 71
    dssp_res_end 80
    pdb_residues_full "A-70- ,A-71- ,A-72- ,A-73- ,A-74- ,A-75- ,A-76- ,A-77- ,A-78- ,A-79- "
    aa_sequence "QYTLMAQAVD"
    index_in_parent_pg "5"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 7
    num_residues 13
    pdb_res_start "A-85- "
    pdb_res_end "A-97- "
    dssp_res_start 86
    dssp_res_end 98
    pdb_residues_full "A-85- ,A-86- ,A-87- ,A-88- ,A-89- ,A-90- ,A-91- ,A-92- ,A-93- ,A-94- ,A-95- ,A-96- ,A-97- "
    aa_sequence "PLEPPSEFIVKVQ"
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
