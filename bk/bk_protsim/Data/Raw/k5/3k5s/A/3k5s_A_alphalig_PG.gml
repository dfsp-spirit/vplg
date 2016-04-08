graph [
  id 1
  label "VPLG Protein Graph 3k5s-A-alphalig[4,1]"
  comment "date=07-OCT-09|keywords=CADHERIN, CALCIUM, CELL ADHESION, ALTERNATIVE SPLICING, CELLMEMBRANE, CLEAVAGE ON PAIR OF BASIC RESIDUES, GLYCOPROTEIN,GPI-ANCHOR, LIPOPROTEIN, MEMBRANE, STRUCTURAL PROTEIN|graphclass=protein graph|pdb_org_common=BANTAM,CHICKENS|title=CRYSTAL STRUCTURE OF CHICKEN T-CADHERIN EC1 EC2|resolution=2.9|pdb_all_chains=A, B|pdb_mol_id=1|pdb_mol_name=CADHERIN-13|pdbid=3k5s|graphtype=alphalig|experiment=X-RAY DIFFRACTION|chainid=A|pdb_org_sci=GALLUS GALLUS|pdb_ec_number=|header=STRUCTURAL PROTEIN|"
  directed 0
  isplanar 0
  creator "PLCC version 0.98.1"
  pdb_id "3k5s"
  chain_id "A"
  graph_type "alphalig"
  is_protein_graph 1
  is_folding_graph 0
  is_SSE_graph 1
  is_AA_graph 0
  is_all_chains_graph 0
  node [
    id 0
    label "0-H"
    num_in_chain 14
    num_residues 3
    pdb_res_start "A-198- "
    pdb_res_end "A-200- "
    dssp_res_start 198
    dssp_res_end 200
    pdb_residues_full "A-198- ,A-199- ,A-200- "
    aa_sequence "MGG"
    sse_type "H"
    fg_notation_label "h"
  ]
  node [
    id 1
    label "1-L"
    num_in_chain 16
    num_residues 1
    pdb_res_start "A-218- "
    pdb_res_end "A-218- "
    dssp_res_start 436
    dssp_res_end 436
    pdb_residues_full "A-218- "
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
    pdb_res_start "A-219- "
    pdb_res_end "A-219- "
    dssp_res_start 437
    dssp_res_end 437
    pdb_residues_full "A-219- "
    aa_sequence "J"
    lig_name " CA"
    sse_type "L"
    fg_notation_label "l"
  ]
  node [
    id 3
    label "3-L"
    num_in_chain 18
    num_residues 1
    pdb_res_start "A-220- "
    pdb_res_end "A-220- "
    dssp_res_start 438
    dssp_res_end 438
    pdb_residues_full "A-220- "
    aa_sequence "J"
    lig_name " CA"
    sse_type "L"
    fg_notation_label "l"
  ]
  edge [
    source 2
    target 3
    label "j"
    spatial "j"
  ]
]
