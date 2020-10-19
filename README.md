# Visualization of Protein-Ligand Graphs (VPLG)

This package includes software for the analysis of protein structure topologies.
The main part of this package is the computation of topology graphs based on three-dimensional structures from the Protein Data Bank (PDB) and the secondary structure assignment from Define Secondary Structure of Proteins (DSSP).
This software is able to read in legacy PDB files and macromolecular Crystallographic Information Files (mmCIFs) and therefore able to process large structures of > 62 chains or >99,999 atoms.
The software is used to fill the Protein Topology Graph Library (PTGL).

## Included software

Each top-level directory contains one software part of this package.

- 'plcc': Protein-Ligand Contact Computation (PLCC) is the main part of VPLG computing topology graphs of
three-dimensional protein structures
- 'web': files for the PTGL web server
- 'splitpdb': splits a multi-model legacy PDB file into single PDB files of one model
- 'graphletAnalyzer': finds graphlets in graph modeling language (GML) files
- 'bk': implementation of the Bron_Kerbosch algorithm to find cliques
- 'vpg': [closed] graphical user interface for VPLG

This is just the README for the code repository, see the doc/ subdirectory for help with the software in this repo.
For example, find the documentation of PLCC in [/plcc/doc](https://github.com/MolBIFFM/vplg/tree/master/plcc/doc).

## Installing

Both the 'splitpdb' and the 'plcc' directories contain not only the source code but the full Netbeans projects, so you should be able to open them directly if you use the Netbeans IDE.
To use the software, simply build & compile the project.

## Websites

- [VPLG website](http://www.bioinformatik.uni-frankfurt.de/tools/vplg/)
- [PTGL website](http://ptgl.uni-frankfurt.de/): the website VPLG's PLCC fills with data
- [Molbi group](https://www.uni-frankfurt.de/57211826/People)

## License

This project is licensed under the GPL 2 license - see the [license](https://github.com/MolBIFFM/vplg/blob/master/LICENSE) file for details.

## Notes

The source is at GitHub, at https://github.com/dfsp-spirit/vplg/ .
The code has been forked at April 26, 2019, to and will be maintained on https://github.com/MolBIFFM/vplg/ .

VPLG is free software and comes WITHOUT ANY WARRANTY. See the LICENSE file for details.

--
jnw