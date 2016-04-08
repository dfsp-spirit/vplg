graph [
  id 1
  label "VPLG Protein Graph 2a4e-A-alphalig[3,1]"
  comment "date=28-JUN-05|keywords=CADHERIN, DIMER, CALCIUM BINDING, CELL ADHESION|graphclass=protein graph|pdb_org_common=HOUSE MOUSE|title=CRYSTAL STRUCTURE OF MOUSE CADHERIN-11 EC1-2|resolution=3.2|pdb_all_chains=A|pdb_mol_id=1|pdb_mol_name=CADHERIN-11|pdbid=2a4e|graphtype=alphalig|experiment=X-RAY DIFFRACTION|chainid=A|pdb_org_sci=MUS MUSCULUS|pdb_ec_number=|header=CELL ADHESION|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "2a4e"
  chain_id "A"
  graph_type "alphalig"
  is_protein_graph 1
  is_folding_graph 0
  is_SSE_graph 1
  is_AA_graph 0
  is_all_chains_graph 0
  node [
    id 0
    label "0-L"
    num_in_chain 15
    num_residues 1
    pdb_res_start "A-301- "
    pdb_res_end "A-301- "
    dssp_res_start 209
    dssp_res_end 209
    pdb_residues_full "A-301- "
    aa_sequence "J"
    lig_name " CA"
    sse_type "L"
    fg_notation_label "l"
  ]
  node [
    id 1
    label "1-L"
    num_in_chain 16
    num_residues 1
    pdb_res_start "A-302- "
    pdb_res_end "A-302- "
    dssp_res_start 210
    dssp_res_end 210
    pdb_residues_full "A-302- "
    aa_sequence "J"
    lig_name " CA"
    sse_type "L"
    fg_notation_label "l"
  ]
  node [
    id 2
    label "2-L"
    num_in_chain 17
    num_residues 1
    pdb_res_start "A-303- "
    pdb_res_end "A-303- "
    dssp_res_start 211
    dssp_res_end 211
    pdb_residues_full "A-303- "
    aa_sequence "J"
    lig_name " CA"
    sse_type "L"
    fg_notation_label "l"
  ]
  edge [
    source 1
    target 2
    label "j"
    spatial "j"
  ]
]
