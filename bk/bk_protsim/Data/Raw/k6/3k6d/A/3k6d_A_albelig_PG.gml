graph [
  id 1
  label "VPLG Protein Graph 3k6d-A-albelig[8,5]"
  comment "date=08-OCT-09|keywords=T-CADHERIN, CELL ADHESION|graphclass=protein graph|pdb_org_common=FROG|title=CRYSTAL STRUCTURE OF XENOPUS LAEVIS T-CADHERIN EC1|resolution=1.8|pdb_all_chains=A|pdb_mol_id=1|pdb_mol_name=T-CADHERIN|pdbid=3k6d|graphtype=albelig|experiment=X-RAY DIFFRACTION|chainid=A|pdb_org_sci=XENOPUS LAEVIS|pdb_ec_number=|header=CELL ADHESION|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3k6d"
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
    num_residues 4
    pdb_res_start "A-7- "
    pdb_res_end "A-10- "
    dssp_res_start 7
    dssp_res_end 10
    pdb_residues_full "A-7- ,A-8- ,A-9- ,A-10- "
    aa_sequence "ISIP"
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
    dssp_res_start 19
    dssp_res_end 23
    pdb_residues_full "A-19- ,A-20- ,A-21- ,A-22- ,A-23- "
    aa_sequence "KIVGR"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 3
    num_residues 5
    pdb_res_start "A-34- "
    pdb_res_end "A-38- "
    dssp_res_start 34
    dssp_res_end 38
    pdb_residues_full "A-34- ,A-35- ,A-36- ,A-37- ,A-38- "
    aa_sequence "KIKLY"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 4
    num_residues 3
    pdb_res_start "A-50- "
    pdb_res_end "A-52- "
    dssp_res_start 50
    dssp_res_end 52
    pdb_residues_full "A-50- ,A-51- ,A-52- "
    aa_sequence "FKI"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 5
    num_residues 4
    pdb_res_start "A-58- "
    pdb_res_end "A-61- "
    dssp_res_start 58
    dssp_res_end 61
    pdb_residues_full "A-58- ,A-59- ,A-60- ,A-61- "
    aa_sequence "EVSV"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 5
    label "5-E"
    num_in_chain 6
    num_residues 9
    pdb_res_start "A-72- "
    pdb_res_end "A-80- "
    dssp_res_start 72
    dssp_res_end 80
    pdb_residues_full "A-72- ,A-73- ,A-74- ,A-75- ,A-76- ,A-77- ,A-78- ,A-79- ,A-80- "
    aa_sequence "SYQLQVETT"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 7
    num_residues 13
    pdb_res_start "A-86- "
    pdb_res_end "A-98- "
    dssp_res_start 86
    dssp_res_end 98
    pdb_residues_full "A-86- ,A-87- ,A-88- ,A-89- ,A-90- ,A-91- ,A-92- ,A-93- ,A-94- ,A-95- ,A-96- ,A-97- ,A-98- "
    aa_sequence "TIEGPVDLEILVI"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 7
    label "7-L"
    num_in_chain 8
    num_residues 1
    pdb_res_start "A-201- "
    pdb_res_end "A-201- "
    dssp_res_start 100
    dssp_res_end 100
    pdb_residues_full "A-201- "
    aa_sequence "J"
    lig_name " ZN"
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
