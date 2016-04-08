graph [
  id 1
  label "VPLG Protein Graph 3ppe-A-betalig-FG1[8V,7E]"
  comment "pdbid=3ppe|graphtype=betalig|chainid=A|graphclass=folding graph|foldinggraphnumber=2|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3ppe"
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
    num_in_chain 8
    num_residues 7
    pdb_res_start "A-108- "
    pdb_res_end "A-114- "
    dssp_res_start 108
    dssp_res_end 114
    pdb_residues_full "A-108- ,A-109- ,A-110- ,A-111- ,A-112- ,A-113- ,A-114- "
    aa_sequence "IFNGSVP"
    index_in_parent_pg "7"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 1
    label "1-E"
    num_in_chain 9
    num_residues 4
    pdb_res_start "A-122- "
    pdb_res_end "A-125- "
    dssp_res_start 122
    dssp_res_end 125
    pdb_residues_full "A-122- ,A-123- ,A-124- ,A-125- "
    aa_sequence "SVTK"
    index_in_parent_pg "8"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 10
    num_residues 7
    pdb_res_start "A-142- "
    pdb_res_end "A-148- "
    dssp_res_start 142
    dssp_res_end 148
    pdb_residues_full "A-142- ,A-143- ,A-144- ,A-145- ,A-146- ,A-147- ,A-148- "
    aa_sequence "VTYQIIK"
    index_in_parent_pg "9"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 11
    num_residues 3
    pdb_res_start "A-153- "
    pdb_res_end "A-155- "
    dssp_res_start 153
    dssp_res_end 155
    pdb_residues_full "A-153- ,A-154- ,A-155- "
    aa_sequence "FTV"
    index_in_parent_pg "10"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 12
    num_residues 4
    pdb_res_start "A-160- "
    pdb_res_end "A-163- "
    dssp_res_start 160
    dssp_res_end 163
    pdb_residues_full "A-160- ,A-161- ,A-162- ,A-163- "
    aa_sequence "VIFT"
    index_in_parent_pg "11"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 5
    label "5-E"
    num_in_chain 13
    num_residues 10
    pdb_res_start "A-175- "
    pdb_res_end "A-184- "
    dssp_res_start 175
    dssp_res_end 184
    pdb_residues_full "A-175- ,A-176- ,A-177- ,A-178- ,A-179- ,A-180- ,A-181- ,A-182- ,A-183- ,A-184- "
    aa_sequence "AYEIIVKAKD"
    index_in_parent_pg "12"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 14
    num_residues 9
    pdb_res_start "A-194- "
    pdb_res_end "A-202- "
    dssp_res_start 194
    dssp_res_end 202
    pdb_residues_full "A-194- ,A-195- ,A-196- ,A-197- ,A-198- ,A-199- ,A-200- ,A-201- ,A-202- "
    aa_sequence "TATVIIRLT"
    index_in_parent_pg "13"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 7
    label "7-L"
    num_in_chain 16
    num_residues 1
    pdb_res_start "A-402- "
    pdb_res_end "A-402- "
    dssp_res_start 409
    dssp_res_end 409
    pdb_residues_full "A-402- "
    aa_sequence "J"
    index_in_parent_pg "15"
    lig_name " CA"
    sse_type "L"
    fg_notation_label "l"
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
  edge [
    source 5
    target 7
    label "j"
    spatial "j"
  ]
]
