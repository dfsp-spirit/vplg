graph [
  id 1
  label "VPLG Protein Graph 3lnd-D-alphalig[3,1]"
  comment "date=02-FEB-10|keywords=CADHERIN, CELL ADHESION, CELL MEMBRANE, MEMBRANE, TRANSMEMBRANE|graphclass=protein graph|pdb_org_common=MOUSE|title=CRYSTAL STRUCTURE OF CADHERIN-6 EC12 W4A|resolution=2.82|pdb_all_chains=A, B, C, D|pdb_mol_id=1|pdb_mol_name=CDH6 PROTEIN|pdbid=3lnd|graphtype=alphalig|experiment=X-RAY DIFFRACTION|chainid=D|pdb_org_sci=MUS MUSCULUS|pdb_ec_number=|header=CELL ADHESION|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3lnd"
  chain_id "D"
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
    pdb_res_start "D-208- "
    pdb_res_end "D-208- "
    dssp_res_start 818
    dssp_res_end 818
    pdb_residues_full "D-208- "
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
    pdb_res_start "D-209- "
    pdb_res_end "D-209- "
    dssp_res_start 819
    dssp_res_end 819
    pdb_residues_full "D-209- "
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
    pdb_res_start "D-210- "
    pdb_res_end "D-210- "
    dssp_res_start 820
    dssp_res_end 820
    pdb_residues_full "D-210- "
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
