graph [
  id 1
  label "VPLG Protein Graph 3lnd-A-beta-FG1[7V,7E]"
  comment "pdbid=3lnd|graphtype=beta|chainid=A|graphclass=folding graph|foldinggraphnumber=2|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3lnd"
  chain_id "A"
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
    pdb_res_start "A-110- "
    pdb_res_end "A-116- "
    dssp_res_start 103
    dssp_res_end 109
    pdb_residues_full "A-110- ,A-111- ,A-112- ,A-113- ,A-114- ,A-115- ,A-116- "
    aa_sequence "VYTATVP"
    index_in_parent_pg "6"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 1
    label "1-E"
    num_in_chain 9
    num_residues 4
    pdb_res_start "A-124- "
    pdb_res_end "A-127- "
    dssp_res_start 117
    dssp_res_end 120
    pdb_residues_full "A-124- ,A-125- ,A-126- ,A-127- "
    aa_sequence "FVVQ"
    index_in_parent_pg "7"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 10
    num_residues 6
    pdb_res_start "A-145- "
    pdb_res_end "A-150- "
    dssp_res_start 138
    dssp_res_end 143
    pdb_residues_full "A-145- ,A-146- ,A-147- ,A-148- ,A-149- ,A-150- "
    aa_sequence "VYSILQ"
    index_in_parent_pg "8"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 12
    num_residues 3
    pdb_res_start "A-155- "
    pdb_res_end "A-157- "
    dssp_res_start 148
    dssp_res_end 150
    pdb_residues_full "A-155- ,A-156- ,A-157- "
    aa_sequence "FSV"
    index_in_parent_pg "9"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 13
    num_residues 4
    pdb_res_start "A-163- "
    pdb_res_end "A-166- "
    dssp_res_start 156
    dssp_res_end 159
    pdb_residues_full "A-163- ,A-164- ,A-165- ,A-166- "
    aa_sequence "IIKT"
    index_in_parent_pg "10"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 5
    label "5-E"
    num_in_chain 14
    num_residues 9
    pdb_res_start "A-178- "
    pdb_res_end "A-186- "
    dssp_res_start 170
    dssp_res_end 178
    pdb_residues_full "A-178- ,A-179- ,A-180- ,A-181- ,A-182- ,A-183- ,A-184- ,A-185- ,A-186- "
    aa_sequence "QYQVVIQAK"
    index_in_parent_pg "11"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 16
    num_residues 11
    pdb_res_start "A-196- "
    pdb_res_end "A-206- "
    dssp_res_start 188
    dssp_res_end 198
    pdb_residues_full "A-196- ,A-197- ,A-198- ,A-199- ,A-200- ,A-201- ,A-202- ,A-203- ,A-204- ,A-205- ,A-206- "
    aa_sequence "SGTTTVNITLT"
    index_in_parent_pg "12"
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
