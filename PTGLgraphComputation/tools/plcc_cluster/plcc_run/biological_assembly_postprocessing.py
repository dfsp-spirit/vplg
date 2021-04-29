"""
Expects a Cif File of a biological assembly as input.
Goes through each line and once the new atoms of this biological assembly are reached, exchanges the chain ID so there are no duplicates.
Output is the Cif file which is identical to the input file except for the new auth chain IDs.
When .cif is used as endig, output name is original cif file name (in case of 1out it would be 1out.cif).
Example usage:
    > python3 biological_assembly_postprocessing.py 1out-1.cif .cif
"""


########### imports ###########

import sys
import os

########### check argument(s) ###########

if len(sys.argv) < 2:
    print("[ERROR] Expected a file name and a file ending as arguments. Exiting now.")
    exit()
    
if sys.argv[1] not in os.listdir("."):
    print("Could not find " + sys.argv[1] + "! Exiting now.")
    exit()

ba = sys.argv[1]

ending = sys.argv[2]

########### vamos ###########

## functions


def nextLetter(currentLetter):
# checks if current letter is Z or something else and returns next letter
    if currentLetter != "Z":
        return (chr(ord(currentLetter) + 1))
    else:
        return ("A")
                     

def createNewChain():
# creates new chain ID, adds it to existingChains
    lastAddedChain = existingChains[-1]
    newChain = lastAddedChain
    
    for i in range(len(lastAddedChain) -1, -1, -1):
        letter = lastAddedChain[i]
        if nextLetter(letter) != "A":
            newChain = newChain[:i] + nextLetter(letter) + newChain[i+1:]
            break
        else:
            newChain = newChain[:i] + nextLetter(letter) + newChain[i+1:]
    
    if newChain in existingChains:
        newChain = "A" + newChain
        
    existingChains.append(newChain)
    return newChain
    

def exchangeChainId(newChainId):
# exchanges the lines' chain ID with the input ID and saves the line in allLines
    currentLineString = str(singleLine)
        
    itemCount = 0
    isSpace = True
    i = 0
    for letter in currentLineString:
        i += 1
        if letter != " " and isSpace == True:
            isSpace = False
            itemCount += 1
            
        elif letter == " " and isSpace == False:
            isSpace = True
        
        if itemCount == 19:
            currentLineString = currentLineString[:i-1] + newChainId + currentLineString[i:]
            break
        
    allLines.append(currentLineString)


## read through file line by line and exchange chain ID once a duplicated unit is found


inAtomSiteLine = False      # whether the current line is part of the atom_site category
existingModels =[]          # list of all units
currentModel = ""           # the unit that is currently processed
existingChains = []         # list of all chains
currentChain = ""           # the chain that is currently processed
chainPlaceholder = ""       # the original chain ID in the last line that was processed
lastChain = ""              # the chain ID assigned to the last line that was processed
chainDict = {}              # dictionary of chains for current duplicated unit (chain ID in the file : assigned chain ID)
allLines = []               # all lines including corrected chains

with open(ba, "r") as f:
    for singleLine in f.read().split("\n"):
        
        if inAtomSiteLine == True and singleLine.startswith("#"):
        # end of atom list
            inAtomSiteLine = False
            allLines.append(singleLine)
        
        elif singleLine.startswith("_atom_site."):
        # atom site line categories
            inAtomSiteLine = True
            allLines.append(singleLine)
        
        elif inAtomSiteLine == True and not singleLine.startswith("_atom_site."):
        # list of atoms
            currentLineArray = singleLine.split()
            currentModel = currentLineArray[20]
            currentChain = currentLineArray[18]
            
            if ((currentChain != chainPlaceholder) or (not currentModel in existingModels)):
            # new chain and/or new unit
                            
                if not currentModel in existingModels:
                # new unit
                    existingModels.append(currentModel)
                    if len(existingModels) == 1:
                    # first unit
                        allLines.append(singleLine)
                        lastChain = currentChain
                        existingChains.append(lastChain)
                        chainDict[currentChain] = lastChain
                        
                    else:
                    # new duplicated unit
                        chainDict.clear()
                        chainPlaceholder = currentChain
                        lastChain = createNewChain()
                        exchangeChainId(lastChain)
                        chainDict[currentChain] = lastChain
                    
                else:
                # same unit as last line, but new chain
                    if len(existingModels) == 1:
                    # first unit
                        allLines.append(singleLine)
                        lastChain = currentChain
                        existingChains.append(lastChain)
                        
                    else:
                    # duplicated unit
                        chainPlaceholder = currentChain
                        if currentChain in chainDict:
                            lastChain = chainDict[currentChain]
                        else:
                            lastChain = createNewChain()
                            chainDict[currentChain] = lastChain
                        exchangeChainId(lastChain)
            
            else:
            # same unit and chain as last line
                exchangeChainId(lastChain)
                    
        else:
        # everything outside atom site lines
            allLines.append(singleLine)

    
## write all lines into new file

root = ba[0:4]

with open(root + ending, "w") as f:
    for line in allLines:
        lineAsArray = ''.join(line)
        f.write(lineAsArray + "\n")
