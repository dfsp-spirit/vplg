graph [
  id 1
  label "VPLG Protein Graph 3k5s-B-alphalig[3,1]"
  comment "date=07-OCT-09|keywords=CADHERIN, CALCIUM, CELL ADHESION, ALTERNATIVE SPLICING, CELLMEMBRANE, CLEAVAGE ON PAIR OF BASIC RESIDUES, GLYCOPROTEIN,GPI-ANCHOR, LIPOPROTEIN, MEMBRANE, STRUCTURAL PROTEIN|graphclass=protein graph|pdb_org_common=BANTAM,CHICKENS|title=CRYSTAL STRUCTURE OF CHICKEN T-CADHERIN EC1 EC2|resolution=2.9|pdb_all_chains=A, B|pdb_mol_id=1|pdb_mol_name=CADHERIN-13|pdbid=3k5s|graphtype=alphalig|experiment=X-RAY DIFFRACTION|chainid=B|pdb_org_sci=GALLUS GALLUS|pdb_ec_number=|header=STRUCTURAL PROTEIN|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3k5s"
  chain_id "B"
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
    pdb_res_start "B-218- "
    pdb_res_end "B-218- "
    dssp_res_start 439
    dssp_res_end 439
    pdb_residues_full "B-218- "
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
    pdb_res_start "B-219- "
    pdb_res_end "B-219- "
    dssp_res_start 440
    dssp_res_end 440
    pdb_residues_full "B-219- "
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
    pdb_res_start "B-220- "
    pdb_res_end "B-220- "
    dssp_res_start 441
    dssp_res_end 441
    pdb_residues_full "B-220- "
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
