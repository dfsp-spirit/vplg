graph [
  id 1
  label "VPLG Protein Graph 1trz-B-albelig[4,3]"
  comment "title=CRYSTALLOGRAPHIC EVIDENCE FOR DUAL COORDINATION AROUND ZINCIN THE T3R3 HUMAN INSULIN HEXAMER|keywords=HORMONE|pdbid=1trz|experiment=X-RAY DIFFRACTION|pdb_mol_name=INSULIN|chainid=B|graphtype=albelig|resolution=1.6|date=19-NOV-93|pdb_org_sci=UNKNOWN|graphclass=protein graph|header=HORMONE|pdb_org_common=UNKNOWN|"
  directed 0
  isplanar 0
  creator "PLCC version 0.86"
  node [
    id 0
    label "0-H"
    num_in_chain 1
    sse_type "H"
    num_residues 14
    pdb_res_start "B-9- "
    pdb_res_end "B-22- "
    dssp_res_start 31
    dssp_res_end 44
    aa_sequence "SHLVEALYLVCGER"
  ]
  node [
    id 1
    label "1-E"
    num_in_chain 2
    sse_type "E"
    num_residues 3
    pdb_res_start "B-24- "
    pdb_res_end "B-26- "
    dssp_res_start 46
    dssp_res_end 48
    aa_sequence "FFY"
  ]
  node [
    id 2
    label "2-L"
    num_in_chain 3
    sse_type "L"
    num_residues 1
    pdb_res_start "B-31- "
    pdb_res_end "B-31- "
    dssp_res_start 106
    dssp_res_end 106
    aa_sequence "J"
    lig_name " ZN"
  ]
  node [
    id 3
    label "3-L"
    num_in_chain 4
    sse_type "L"
    num_residues 1
    pdb_res_start "B-32- "
    pdb_res_end "B-32- "
    dssp_res_start 107
    dssp_res_end 107
    aa_sequence "J"
    lig_name " CL"
  ]
  edge [
    source 0
    target 2
    label "l"
    spatial "l"
  ]
  edge [
    source 0
    target 3
    label "l"
    spatial "l"
  ]
  edge [
    source 2
    target 3
    label "l"
    spatial "l"
  ]
]
