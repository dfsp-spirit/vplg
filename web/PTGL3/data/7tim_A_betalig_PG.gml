graph [
  id 1
  label "VPLG Protein Graph 7tim-A-betalig[9,11]"
  comment "title=STRUCTURE OF THE TRIOSEPHOSPHATE ISOMERASE-PHOSPHOGLYCOLOHYDROXAMATE COMPLEX: AN ANALOGUE OF THEINTERMEDIATE ON THE REACTION PATHWAY|keywords=INTRAMOLECULAR OXIDOREDUCTASE|pdbid=7tim|experiment=X-RAY DIFFRACTION|pdb_mol_name=TRIOSEPHOSPHATE ISOMERASE|chainid=A|graphtype=betalig|resolution=1.9|date=23-APR-91|pdb_org_sci=SACCHAROMYCES CEREVISIAE|graphclass=protein graph|header=INTRAMOLECULAR OXIDOREDUCTASE|pdb_org_common=BAKER'S YEAST|"
  directed 0
  isplanar 0
  creator "PLCC version 0.86"
  node [
    id 0
    label "0-E"
    num_in_chain 1
    sse_type "E"
    num_residues 6
    pdb_res_start "A-5- "
    pdb_res_end "A-10- "
    dssp_res_start 4
    dssp_res_end 9
    aa_sequence "FFVGGN"
  ]
  node [
    id 1
    label "1-E"
    num_in_chain 3
    sse_type "E"
    num_residues 6
    pdb_res_start "A-36- "
    pdb_res_end "A-41- "
    dssp_res_start 35
    dssp_res_end 40
    aa_sequence "VEVVIC"
  ]
  node [
    id 2
    label "2-E"
    num_in_chain 5
    sse_type "E"
    num_residues 5
    pdb_res_start "A-59- "
    pdb_res_end "A-63- "
    dssp_res_start 58
    dssp_res_end 62
    aa_sequence "VTVGA"
  ]
  node [
    id 3
    label "3-E"
    num_in_chain 7
    sse_type "E"
    num_residues 4
    pdb_res_start "A-90- "
    pdb_res_end "A-93- "
    dssp_res_start 89
    dssp_res_end 92
    aa_sequence "WVIL"
  ]
  node [
    id 4
    label "4-E"
    num_in_chain 10
    sse_type "E"
    num_residues 6
    pdb_res_start "A-122- "
    pdb_res_end "A-127- "
    dssp_res_start 121
    dssp_res_end 126
    aa_sequence "GVILCI"
  ]
  node [
    id 5
    label "5-E"
    num_in_chain 13
    sse_type "E"
    num_residues 5
    pdb_res_start "A-160- "
    pdb_res_end "A-164- "
    dssp_res_start 159
    dssp_res_end 163
    aa_sequence "VVVAY"
  ]
  node [
    id 6
    label "6-E"
    num_in_chain 17
    sse_type "E"
    num_residues 4
    pdb_res_start "A-206- "
    pdb_res_end "A-209- "
    dssp_res_start 205
    dssp_res_end 208
    aa_sequence "ILYG"
  ]
  node [
    id 7
    label "7-E"
    num_in_chain 19
    sse_type "E"
    num_residues 4
    pdb_res_start "A-228- "
    pdb_res_end "A-231- "
    dssp_res_start 227
    dssp_res_end 230
    aa_sequence "GFLV"
  ]
  node [
    id 8
    label "8-L"
    num_in_chain 22
    sse_type "L"
    num_residues 1
    pdb_res_start "A-249- "
    pdb_res_end "A-249- "
    dssp_res_start 496
    dssp_res_end 496
    aa_sequence "J"
    lig_name "PGH"
  ]
  edge [
    source 0
    target 1
    label "p"
    spatial "p"
  ]
  edge [
    source 0
    target 7
    label "p"
    spatial "p"
  ]
  edge [
    source 0
    target 8
    label "l"
    spatial "l"
  ]
  edge [
    source 1
    target 2
    label "p"
    spatial "p"
  ]
  edge [
    source 2
    target 3
    label "p"
    spatial "p"
  ]
  edge [
    source 3
    target 4
    label "p"
    spatial "p"
  ]
  edge [
    source 4
    target 5
    label "p"
    spatial "p"
  ]
  edge [
    source 4
    target 8
    label "l"
    spatial "l"
  ]
  edge [
    source 5
    target 6
    label "p"
    spatial "p"
  ]
  edge [
    source 6
    target 7
    label "p"
    spatial "p"
  ]
  edge [
    source 7
    target 8
    label "l"
    spatial "l"
  ]
]
