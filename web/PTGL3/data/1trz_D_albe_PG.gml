graph [
  id 1
  label "VPLG Protein Graph 1trz-D-albe[2,1]"
  comment "title=CRYSTALLOGRAPHIC EVIDENCE FOR DUAL COORDINATION AROUND ZINCIN THE T3R3 HUMAN INSULIN HEXAMER|keywords=HORMONE|pdbid=1trz|experiment=X-RAY DIFFRACTION|pdb_mol_name=INSULIN|chainid=D|graphtype=albe|resolution=1.6|date=19-NOV-93|pdb_org_sci=UNKNOWN|graphclass=protein graph|header=HORMONE|pdb_org_common=UNKNOWN|"
  directed 0
  isplanar 0
  creator "PLCC version 0.86"
  node [
    id 0
    label "0-H"
    num_in_chain 1
    sse_type "H"
    num_residues 19
    pdb_res_start "D-4- "
    pdb_res_end "D-22- "
    dssp_res_start 79
    dssp_res_end 97
    aa_sequence "QHLCGSHLVEALYLVCGER"
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
  edge [
    source 0
    target 1
    label "m"
    spatial "m"
  ]
]
