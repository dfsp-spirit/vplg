graph [
  id 1
  label "VPLG Protein Graph 3lnd-D-beta-FG1[7V,7E]"
  comment "pdbid=3lnd|graphtype=beta|chainid=D|graphclass=folding graph|foldinggraphnumber=2|"
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
    num_in_chain 8
    num_residues 7
    pdb_res_start "D-110- "
    pdb_res_end "D-116- "
    dssp_res_start 715
    dssp_res_end 721
    pdb_residues_full "D-110- ,D-111- ,D-112- ,D-113- ,D-114- ,D-115- ,D-116- "
    aa_sequence "VYTATVP"
    index_in_parent_pg "7"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 1
    label "1-E"
    num_in_chain 9
    num_residues 4
    pdb_res_start "D-124- "
    pdb_res_end "D-127- "
    dssp_res_start 729
    dssp_res_end 732
    pdb_residues_full "D-124- ,D-125- ,D-126- ,D-127- "
    aa_sequence "FVVQ"
    index_in_parent_pg "8"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 10
    num_residues 6
    pdb_res_start "D-145- "
    pdb_res_end "D-150- "
    dssp_res_start 750
    dssp_res_end 755
    pdb_residues_full "D-145- ,D-146- ,D-147- ,D-148- ,D-149- ,D-150- "
    aa_sequence "VYSILQ"
    index_in_parent_pg "9"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 11
    num_residues 3
    pdb_res_start "D-155- "
    pdb_res_end "D-157- "
    dssp_res_start 760
    dssp_res_end 762
    pdb_residues_full "D-155- ,D-156- ,D-157- "
    aa_sequence "FSV"
    index_in_parent_pg "10"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 12
    num_residues 4
    pdb_res_start "D-163- "
    pdb_res_end "D-166- "
    dssp_res_start 768
    dssp_res_end 771
    pdb_residues_full "D-163- ,D-164- ,D-165- ,D-166- "
    aa_sequence "IIKT"
    index_in_parent_pg "11"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 5
    label "5-E"
    num_in_chain 13
    num_residues 9
    pdb_res_start "D-178- "
    pdb_res_end "D-186- "
    dssp_res_start 779
    dssp_res_end 787
    pdb_residues_full "D-178- ,D-179- ,D-180- ,D-181- ,D-182- ,D-183- ,D-184- ,D-185- ,D-186- "
    aa_sequence "QYQVVIQAK"
    index_in_parent_pg "12"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 14
    num_residues 11
    pdb_res_start "D-196- "
    pdb_res_end "D-206- "
    dssp_res_start 797
    dssp_res_end 807
    pdb_residues_full "D-196- ,D-197- ,D-198- ,D-199- ,D-200- ,D-201- ,D-202- ,D-203- ,D-204- ,D-205- ,D-206- "
    aa_sequence "SGTTTVNITLT"
    index_in_parent_pg "13"
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
