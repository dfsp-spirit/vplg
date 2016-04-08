graph [
  id 1
  label "VPLG Protein Graph 3ppe-A-alphalig[3,1]"
  comment "date=24-NOV-10|keywords=EXTRACELLULAR CADHERIN (EC) DOMAIN, BETA BARREL, IG-DOMAIN LIKE,DOMAIN SWAPPED DIMER INTERFACE, CALCIUM DEPENDENT CELL-CELLADHESION, CELL SURFACE, CELL ADHESION|graphclass=protein graph|pdb_org_common=BANTAM,CHICKENS|title=CRYSTAL STRUCTURE OF CHICKEN VE-CADHERIN EC1-2|resolution=2.1|pdb_all_chains=A, B|pdb_mol_id=1|pdb_mol_name=VASCULAR ENDOTHELIAL CADHERIN|pdbid=3ppe|graphtype=alphalig|experiment=X-RAY DIFFRACTION|chainid=A|pdb_org_sci=GALLUS GALLUS|pdb_ec_number=|header=CELL ADHESION|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3ppe"
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
    pdb_res_start "A-401- "
    pdb_res_end "A-401- "
    dssp_res_start 408
    dssp_res_end 408
    pdb_residues_full "A-401- "
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
    pdb_res_start "A-402- "
    pdb_res_end "A-402- "
    dssp_res_start 409
    dssp_res_end 409
    pdb_residues_full "A-402- "
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
    pdb_res_start "A-403- "
    pdb_res_end "A-403- "
    dssp_res_start 410
    dssp_res_end 410
    pdb_residues_full "A-403- "
    aa_sequence "J"
    lig_name " CA"
    sse_type "L"
    fg_notation_label "l"
  ]
  edge [
    source 0
    target 2
    label "j"
    spatial "j"
  ]
]
