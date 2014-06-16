graph [
  id 1
  label "VPLG Protein Graph 1trz-B-betalig[3,1]"
  comment "title=CRYSTALLOGRAPHIC EVIDENCE FOR DUAL COORDINATION AROUND ZINCIN THE T3R3 HUMAN INSULIN HEXAMER|keywords=HORMONE|pdbid=1trz|experiment=X-RAY DIFFRACTION|pdb_mol_name=INSULIN|chainid=B|graphtype=betalig|resolution=1.6|date=19-NOV-93|pdb_org_sci=UNKNOWN|graphclass=protein graph|header=HORMONE|pdb_org_common=UNKNOWN|"
  directed 0
  isplanar 0
  creator "PLCC version 0.86"
  node [
    id 0
    label "0-E"
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
    id 1
    label "1-L"
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
    id 2
    label "2-L"
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
    source 1
    target 2
    label "l"
    spatial "l"
  ]
]
