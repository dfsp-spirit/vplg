#!/bin/sh

# written by jnw in 2018

# Downloads both the mmcif type and the normal DSSP file via rsync (or just on of them if settings are changed).
# Arguments:
#     1) file path to list with PDB IDs (one per line)
#     2) optional: both || normal || cif changes settings to download respective files


########## Settings ##########

normal_file_ending=dssp
cif_type_file_ending=dssp_cif

download_normal=true
download_cif=true


########## Options ##########

if [ "$2" == "both" ]
    then
    download_normal=true
    download_cif=true
fi

if [ "$2" == "normal" ]
    then
    download_normal=true
    download_cif=false
fi

if [ "$2" == "cif" ]
    then
    download_normal=false
    download_cif=true
fi


########## Vamos ##########

if [ ! -f "$1" ]
    then
    echo "[Error] Could not find file list. Exiting now."
    exit 1
fi

while IFS= read aline
do
    # normal dssp file
    
    if [ $download_normal == true ]
    then
        if [ ! -f "$aline".$normal_file_ending ]
        then 
            rsync -avz rsync://rsync.cmbi.ru.nl/dssp/"$aline".dssp ./"$aline".$normal_file_ending
            if [ ! -f "$aline".$normal_file_ending ]
            then
                echo "[Error] Rsync seems to have failed for $aline.$normal_file_ending as file is not existing."
            fi
        else
            echo "$aline.$normal_file_ending already existing. Skipping that."
        fi
    fi
    
    # cif dssp file
    
    if [ $download_cif == true ]
    then
        if [ ! -f "$aline".$cif_type_file_ending ]
        then 
            rsync -avz rsync://rsync.cmbi.ru.nl/dssp-from-mmcif/"$aline".dssp ./"$aline".$cif_type_file_ending
            if [ ! -f "$aline".$cif_type_file_ending ]
            then
                echo "[Error] Rsync seems to have failed for $aline.$cif_type_file_ending as file is not existing."
            fi
        else
            echo "$aline.$cif_type_file_ending already existing. Skipping that."
        fi
    fi
    
done < $1
