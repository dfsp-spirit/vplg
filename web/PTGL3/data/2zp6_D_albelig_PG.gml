graph [
  id 1
  label "VPLG Protein Graph 2zp6-D-albelig[3,1]"
  comment "title=CRYSTAL STRUCTURE OF BOVINE INSULIN (HEXAMERIC FORM)|keywords=HEXAMERIC FORM, CARBOHYDRATE METABOLISM, CLEAVAGE ON PAIROF BASIC RESIDUES, GLUCOSE METABOLISM, HORMONE, SECRETED|pdbid=2zp6|experiment=X-RAY DIFFRACTION|pdb_mol_name=INSULIN B CHAIN|chainid=D|graphtype=albelig|resolution=2.56|date=27-JUN-08|pdb_org_sci=BOS TAURUS|graphclass=protein graph|header=HORMONE|pdb_org_common=BOVINE|"
  directed 0
  isplanar 0
  creator "PLCC version 0.86"
  node [
    id 0
    label "0-H"
    num_in_chain 1
    sse_type "H"
    num_residues 14
    pdb_res_start "D-9- "
    pdb_res_end "D-22- "
    dssp_res_start 84
    dssp_res_end 97
    aa_sequence "SHLVEALYLVCGER"
  ]
  node [
    id 1
    label "1-E"
    num_in_chain 2
    sse_type "E"
    num_residues 3
    pdb_res_start "D-24- "
    pdb_res_end "D-26- "
    dssp_res_start 99
    dssp_res_end 101
    aa_sequence "FFY"
  ]
  node [
    id 2
    label "2-L"
    num_in_chain 3
    sse_type "L"
    num_residues 1
    pdb_res_start "D-31- "
    pdb_res_end "D-31- "
    dssp_res_start 107
    dssp_res_end 107
    aa_sequence "J"
    lig_name " ZN"
  ]
  edge [
    source 0
    target 2
    label "l"
    spatial "l"
  ]
]
