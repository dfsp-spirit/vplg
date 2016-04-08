graph [
  id 1
  label "VPLG Protein Graph 1zxk-A-beta[7,6]"
  comment "date=08-JUN-05|keywords=CADHERIN, STRAND DIMER, CELL ADHESION|graphclass=protein graph|pdb_org_common=HOUSE MOUSE|title=CRYSTAL STRUCTURE OF CADHERIN8 EC1 DOMAIN|resolution=2.0|pdb_all_chains=A, B|pdb_mol_id=1|pdb_mol_name=CADHERIN-8|pdbid=1zxk|graphtype=beta|experiment=X-RAY DIFFRACTION|chainid=A|pdb_org_sci=MUS MUSCULUS|pdb_ec_number=|header=CELL ADHESION|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "1zxk"
  chain_id "A"
  graph_type "beta"
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
    pdb_res_start "A-6- "
    pdb_res_end "A-9- "
    dssp_res_start 6
    dssp_res_end 9
    pdb_residues_full "A-6- ,A-7- ,A-8- ,A-9- "
    aa_sequence "QMFV"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 1
    label "1-E"
    num_in_chain 3
    num_residues 5
    pdb_res_start "A-19- "
    pdb_res_end "A-23- "
    dssp_res_start 19
    dssp_res_end 23
    pdb_residues_full "A-19- ,A-20- ,A-21- ,A-22- ,A-23- "
    aa_sequence "ILVGR"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 4
    num_residues 7
    pdb_res_start "A-35- "
    pdb_res_end "A-41- "
    dssp_res_start 34
    dssp_res_end 40
    pdb_residues_full "A-35- ,A-36- ,A-37- ,A-38- ,A-39- ,A-40- ,A-41- "
    aa_sequence "IKYILSG"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 5
    num_residues 3
    pdb_res_start "A-48- "
    pdb_res_end "A-50- "
    dssp_res_start 47
    dssp_res_end 49
    pdb_residues_full "A-48- ,A-49- ,A-50- "
    aa_sequence "FQI"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 6
    num_residues 4
    pdb_res_start "A-56- "
    pdb_res_end "A-59- "
    dssp_res_start 55
    dssp_res_end 58
    pdb_residues_full "A-56- ,A-57- ,A-58- ,A-59- "
    aa_sequence "DIHA"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 5
    label "5-E"
    num_in_chain 7
    num_residues 10
    pdb_res_start "A-70- "
    pdb_res_end "A-79- "
    dssp_res_start 69
    dssp_res_end 78
    pdb_residues_full "A-70- ,A-71- ,A-72- ,A-73- ,A-74- ,A-75- ,A-76- ,A-77- ,A-78- ,A-79- "
    aa_sequence "EYTLTAQAVD"
    sse_type "E"
    fg_notation_label "e"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 8
    num_residues 7
    pdb_res_start "A-90- "
    pdb_res_end "A-96- "
    dssp_res_start 89
    dssp_res_end 95
    pdb_residues_full "A-90- ,A-91- ,A-92- ,A-93- ,A-94- ,A-95- ,A-96- "
    aa_sequence "SEFIIKV"
    sse_type "E"
    fg_notation_label "e"
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
