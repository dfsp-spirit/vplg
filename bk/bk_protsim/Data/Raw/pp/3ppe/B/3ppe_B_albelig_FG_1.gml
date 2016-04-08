graph [
  id 1
  label "VPLG Protein Graph 3ppe-B-albelig-FG1[8V,8E]"
  comment "pdbid=3ppe|graphtype=albelig|chainid=B|graphclass=folding graph|foldinggraphnumber=2|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3ppe"
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
    num_in_chain 8
    num_residues 7
    pdb_res_start "B-108- "
    pdb_res_end "B-114- "
    dssp_res_start 312
    dssp_res_end 318
    pdb_residues_full "B-108- ,B-109- ,B-110- ,B-111- ,B-112- ,B-113- ,B-114- "
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
    pdb_res_start "B-122- "
    pdb_res_end "B-125- "
    dssp_res_start 326
    dssp_res_end 329
    pdb_residues_full "B-122- ,B-123- ,B-124- ,B-125- "
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
    pdb_res_start "B-142- "
    pdb_res_end "B-148- "
    dssp_res_start 346
    dssp_res_end 352
    pdb_residues_full "B-142- ,B-143- ,B-144- ,B-145- ,B-146- ,B-147- ,B-148- "
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
    pdb_res_start "B-153- "
    pdb_res_end "B-155- "
    dssp_res_start 357
    dssp_res_end 359
    pdb_residues_full "B-153- ,B-154- ,B-155- "
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
    pdb_res_start "B-160- "
    pdb_res_end "B-163- "
    dssp_res_start 364
    dssp_res_end 367
    pdb_residues_full "B-160- ,B-161- ,B-162- ,B-163- "
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
    pdb_res_start "B-175- "
    pdb_res_end "B-184- "
    dssp_res_start 379
    dssp_res_end 388
    pdb_residues_full "B-175- ,B-176- ,B-177- ,B-178- ,B-179- ,B-180- ,B-181- ,B-182- ,B-183- ,B-184- "
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
    pdb_res_start "B-194- "
    pdb_res_end "B-202- "
    dssp_res_start 398
    dssp_res_end 406
    pdb_residues_full "B-194- ,B-195- ,B-196- ,B-197- ,B-198- ,B-199- ,B-200- ,B-201- ,B-202- "
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
    pdb_res_start "B-402- "
    pdb_res_end "B-402- "
    dssp_res_start 412
    dssp_res_end 412
    pdb_residues_full "B-402- "
    aa_sequence "J"
    index_in_parent_pg "15"
    lig_name " CA"
    sse_type "L"
    fg_notation_label "l"
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
  edge [
    source 5
    target 7
    label "j"
    spatial "j"
  ]
]
