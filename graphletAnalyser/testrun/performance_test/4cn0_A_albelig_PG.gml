graph [
  id 1
  label "VPLG Protein Graph 4cn0-A-albelig[10,8]"
  comment "title=AN INTERTWINED HOMODIMER OF THE PDZ HOMOLOGY DOMAIN OF AHNAK2|keywords=CELL CYCLE, DOMAIN SWAPPING, INTERTWINING, PDZ DOMAIN|pdbid=4cn0|experiment=X-RAY DIFFRACTION|pdb_mol_name=PROTEIN AHNAK2|chainid=A|graphtype=albelig|resolution=1.75|date=20-JAN-14|pdb_org_sci=HOMO SAPIENS|graphclass=protein graph|header=CELL CYCLE|pdb_org_common=HUMAN|"
  directed 0
  isplanar 0
  creator "PLCC version 0.97"
  pdb_id "4cn0"
  chain_id "A"
  graph_type "albelig"
  is_protein_graph 1
  is_folding_graph 0
  is_SSE_graph 1
  is_AA_graph 0
  is_all_chains_graph 0
  node [
    id 0
    label "0-E"
    num_in_chain 1
    num_residues 5
    pdb_res_start "A-112- "
    pdb_res_end "A-116- "
    dssp_res_start 4
    dssp_res_end 8
    pdb_residues_full "A-112- ,A-113- ,A-114- ,A-115- ,A-116- "
    aa_sequence "EVTLK"
    sse_type "E"
  ]
  node [
    id 1
    label "1-E"
    num_in_chain 2
    num_residues 5
    pdb_res_start "A-126- "
    pdb_res_end "A-130- "
    dssp_res_start 18
    dssp_res_end 22
    pdb_residues_full "A-126- ,A-127- ,A-128- ,A-129- ,A-130- "
    aa_sequence "YSVTG"
    sse_type "E"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 3
    num_residues 6
    pdb_res_start "A-136- "
    pdb_res_end "A-141- "
    dssp_res_start 28
    dssp_res_end 33
    pdb_residues_full "A-136- ,A-137- ,A-138- ,A-139- ,A-140- ,A-141- "
    aa_sequence "IFVKQV"
    sse_type "E"
  ]
  node [
    id 3
    label "3-H"
    num_in_chain 4
    num_residues 4
    pdb_res_start "A-146- "
    pdb_res_end "A-149- "
    dssp_res_start 38
    dssp_res_end 41
    pdb_residues_full "A-146- ,A-147- ,A-148- ,A-149- "
    aa_sequence "SAAK"
    sse_type "H"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 5
    num_residues 8
    pdb_res_start "A-158- "
    pdb_res_end "A-165- "
    dssp_res_start 50
    dssp_res_end 57
    pdb_residues_full "A-158- ,A-159- ,A-160- ,A-161- ,A-162- ,A-163- ,A-164- ,A-165- "
    aa_sequence "QLLSTTVF"
    sse_type "E"
  ]
  node [
    id 5
    label "5-H"
    num_in_chain 6
    num_residues 11
    pdb_res_start "A-171- "
    pdb_res_end "A-181- "
    dssp_res_start 63
    dssp_res_end 73
    pdb_residues_full "A-171- ,A-172- ,A-173- ,A-174- ,A-175- ,A-176- ,A-177- ,A-178- ,A-179- ,A-180- ,A-181- "
    aa_sequence "YEDALKILQYS"
    sse_type "H"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 7
    num_residues 9
    pdb_res_start "A-185- "
    pdb_res_end "A-193- "
    dssp_res_start 77
    dssp_res_end 85
    pdb_residues_full "A-185- ,A-186- ,A-187- ,A-188- ,A-189- ,A-190- ,A-191- ,A-192- ,A-193- "
    aa_sequence "KVQFKIRRQ"
    sse_type "E"
  ]
  node [
    id 7
    label "7-L"
    num_in_chain 8
    num_residues 1
    pdb_res_start "A-1196- "
    pdb_res_end "A-1196- "
    dssp_res_start 182
    dssp_res_end 182
    pdb_residues_full "A-1196- "
    aa_sequence "J"
    lig_name "PG4"
    sse_type "L"
  ]
  node [
    id 8
    label "8-L"
    num_in_chain 9
    num_residues 1
    pdb_res_start "A-1197- "
    pdb_res_end "A-1197- "
    dssp_res_start 183
    dssp_res_end 183
    pdb_residues_full "A-1197- "
    aa_sequence "J"
    lig_name "PG4"
    sse_type "L"
  ]
  node [
    id 9
    label "9-L"
    num_in_chain 10
    num_residues 1
    pdb_res_start "A-1198- "
    pdb_res_end "A-1198- "
    dssp_res_start 184
    dssp_res_end 184
    pdb_residues_full "A-1198- "
    aa_sequence "J"
    lig_name "PG4"
    sse_type "L"
  ]
  edge [
    source 1
    target 2
    label "a"
    spatial "a"
  ]
  edge [
    source 1
    target 8
    label "l"
    spatial "l"
  ]
  edge [
    source 1
    target 9
    label "l"
    spatial "l"
  ]
  edge [
    source 2
    target 4
    label "p"
    spatial "p"
  ]
  edge [
    source 2
    target 8
    label "l"
    spatial "l"
  ]
  edge [
    source 2
    target 9
    label "l"
    spatial "l"
  ]
  edge [
    source 5
    target 7
    label "l"
    spatial "l"
  ]
  edge [
    source 8
    target 9
    label "l"
    spatial "l"
  ]
]
