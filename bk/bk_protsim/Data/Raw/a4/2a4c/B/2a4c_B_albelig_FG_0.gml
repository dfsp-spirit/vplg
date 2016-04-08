graph [
  id 1
  label "VPLG Protein Graph 2a4c-B-albelig-FG0[8V,7E]"
  comment "pdbid=2a4c|graphtype=albelig|chainid=B|graphclass=folding graph|foldinggraphnumber=1|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "2a4c"
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
    dssp_res_start 107
    dssp_res_end 111
    pdb_residues_full "B-6- ,B-7- ,B-8- ,B-9- ,B-10- "
    aa_sequence "QFFVI"
    index_in_parent_pg "0"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 1
    label "1-H"
    num_in_chain 2
    num_residues 3
    pdb_res_start "B-11- "
    pdb_res_end "B-13- "
    dssp_res_start 112
    dssp_res_end 114
    pdb_residues_full "B-11- ,B-12- ,B-13- "
    aa_sequence "EEY"
    index_in_parent_pg "1"
    sse_type "H"
    fg_notation_label "h"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 3
    num_residues 5
    pdb_res_start "B-19- "
    pdb_res_end "B-23- "
    dssp_res_start 120
    dssp_res_end 124
    pdb_residues_full "B-19- ,B-20- ,B-21- ,B-22- ,B-23- "
    aa_sequence "VLVGR"
    index_in_parent_pg "2"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 4
    num_residues 7
    pdb_res_start "B-35- "
    pdb_res_end "B-41- "
    dssp_res_start 136
    dssp_res_end 142
    pdb_residues_full "B-35- ,B-36- ,B-37- ,B-38- ,B-39- ,B-40- ,B-41- "
    aa_sequence "IKYILSG"
    index_in_parent_pg "3"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 5
    num_residues 3
    pdb_res_start "B-48- "
    pdb_res_end "B-50- "
    dssp_res_start 149
    dssp_res_end 151
    pdb_residues_full "B-48- ,B-49- ,B-50- "
    aa_sequence "FVI"
    index_in_parent_pg "4"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 5
    label "5-E"
    num_in_chain 6
    num_residues 4
    pdb_res_start "B-56- "
    pdb_res_end "B-59- "
    dssp_res_start 157
    dssp_res_end 160
    pdb_residues_full "B-56- ,B-57- ,B-58- ,B-59- "
    aa_sequence "NIHA"
    index_in_parent_pg "5"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 7
    num_residues 10
    pdb_res_start "B-70- "
    pdb_res_end "B-79- "
    dssp_res_start 171
    dssp_res_end 180
    pdb_residues_full "B-70- ,B-71- ,B-72- ,B-73- ,B-74- ,B-75- ,B-76- ,B-77- ,B-78- ,B-79- "
    aa_sequence "QYTLMAQAVD"
    index_in_parent_pg "6"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 7
    label "7-E"
    num_in_chain 8
    num_residues 13
    pdb_res_start "B-85- "
    pdb_res_end "B-97- "
    dssp_res_start 186
    dssp_res_end 198
    pdb_residues_full "B-85- ,B-86- ,B-87- ,B-88- ,B-89- ,B-90- ,B-91- ,B-92- ,B-93- ,B-94- ,B-95- ,B-96- ,B-97- "
    aa_sequence "PLEPPSEFIVKVQ"
    index_in_parent_pg "7"
    sse_type "E"
    fg_notation_label "e"
  ]
  edge [
    source 0
    target 1
    label "m"
    spatial "m"
  ]
  edge [
    source 0
    target 2
    label "a"
    spatial "a"
  ]
  edge [
    source 0
    target 7
    label "p"
    spatial "p"
  ]
  edge [
    source 2
    target 5
    label "a"
    spatial "a"
  ]
  edge [
    source 3
    target 6
    label "a"
    spatial "a"
  ]
  edge [
    source 4
    target 5
    label "a"
    spatial "a"
  ]
  edge [
    source 6
    target 7
    label "a"
    spatial "a"
  ]
]
