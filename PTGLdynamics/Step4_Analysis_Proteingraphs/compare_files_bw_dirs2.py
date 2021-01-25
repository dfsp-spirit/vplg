"""
Expects two paths to directories and compares files with same names (no sub directories).
Only files with an ending defined in the settings section are treated. Lines containing one of the keywords may be ignored.
Example usage:
 > compare_files_bw_dirs.py ./dir1 ./dir2       --> did not work
 > compare_files_bw_dirs.py dir1 dir2           --> did work
"""

########### settings ###########

# list of file endings that are considered
viable_file_endings = ["gml"]

# lines that are being ignored when they include one of the phrases
ignore_lines_containing = ["comment", "Exported", "All done, exiting. Total runtime was", "Starting computation for", 'noQ_r1_frame']

# regular expression for a prefix of file that is removed. Switched off when empty string.
ignore_file_prefixes = "noQ_r1_frame.*ns"
regExMode = True

########### imports ###########

import sys
import os
import re #module for regular expressions

########### check argument(s) ###########

if len(sys.argv) < 3:
    print("[Error] Expected two paths to directiories. Exiting now.")
    exit()

dir1 = sys.argv[1]
dir2 = sys.argv[2]

if dir1 not in os.listdir("."):
    print("Could not find " + dir1 + "! Exiting now.")
    exit()

if dir2 not in os.listdir("."):
    print("Could not find " + dir2 + "! Exiting now.")
    exit()

########### vamos ###########

## print info on settings
current_tag = "[Settings] "

print(current_tag + "Following file endings defined as viable:")
for ending in viable_file_endings:
    print("    " + ending)
print(current_tag + "Ignoring lines which include:")
for blacklist_word in ignore_lines_containing:
    print("    " + blacklist_word)

## find all viable files
current_tag = "[File check] "

# directory 1
dir1_filelist = os.listdir(dir1)
dir1_viable_files = []
dir1_count_ignored = 0

print(current_tag + "[" + dir1 + "] Files ignored because of file type ending:")
for fp in dir1_filelist:
    if fp.split(".")[len(fp.split(".")) - 1] in viable_file_endings:
        dir1_viable_files.append(fp)
    else:
        print("    " + fp)
        dir1_count_ignored += 1

print(current_tag + "[" + dir1 + "] --> Ignoring in total " + str(dir1_count_ignored) + " files.")

# directory 2
dir2_filelist = os.listdir(dir2)
dir2_viable_files = []
dir2_count_ignored = 0

print(current_tag + "[" + dir2 + "] Files ignored because of file type ending:")
for fp in dir2_filelist:
    if fp.split(".")[len(fp.split(".")) - 1] in viable_file_endings:
        dir2_viable_files.append(fp)
    else:
        print("    " + fp)
        dir2_count_ignored += 1

print(current_tag + "[" + dir2 + "] --> Ignoring in total " + str(dir2_count_ignored) + " files.")

## find matching file names
current_tag = "[File matching] "

# 
if regExMode:
    matching_names_suffixes = []
    dir1_viable_files_suffixes = []
    dir2_viable_files_suffixes = []
    dir1_only_files_suffixes = []
    dir2_only_files_suffixes = []

    # fill in dir1_viable_files_suffixes
    for fp in dir1_viable_files:
        if re.match(ignore_file_prefixes, fp):
            splitIndex= re.match(ignore_file_prefixes, fp).end()
            fp = fp[splitIndex:]
            dir1_viable_files_suffixes.append(fp)
                    
    # fill in dir2_viable_files_suffixes	
    for fp in dir2_viable_files:
        if re.match(ignore_file_prefixes, fp):
            splitIndex= re.match(ignore_file_prefixes, fp).end()
            fp = fp[splitIndex:]
            dir2_viable_files_suffixes.append(fp)

    # check files of dir1 in dir2		
    for fp in dir1_viable_files_suffixes:
        if fp in dir2_viable_files_suffixes:
            matching_names_suffixes.append(fp)
        else:
            dir1_only_files_suffixes.append(fp)

    # check files of dir2 in dir1 
    for fp in dir2_viable_files_suffixes:
        if fp not in dir1_viable_files_suffixes:
            dir2_only_files_suffixes.append(fp)

else:  
    matching_names = []
    dir1_only_files = []
    dir2_only_files = []

    for fp in dir1_viable_files:
        if fp in dir2_viable_files:
            matching_names.append(fp)
        else:
            dir1_only_files.append(fp)

    for fp in dir2_viable_files:
        if fp not in dir1_viable_files:
            dir2_only_files.append(fp)

## print non-matching

print(current_tag + "Finding matching file names ...")

# set variables depending on mode
if regExMode:
    dir1_only = dir1_only_files_suffixes
    dir2_only = dir2_only_files_suffixes
    matchingList = matching_names_suffixes
else:
    dir1_only = dir1_only_files
    dir2_only = dir2_only_files
    matchingList = matching_names


for fp in dir1_only:
    print(current_tag + "Only in " + dir1 + ": " + fp)
print(current_tag + "--> " + str(len(dir1_only)) + " files only in " + dir1)

for fp in dir2_only:
    print(current_tag + "Only in " + dir2 + ": " + fp)
print(current_tag + "--> " + str(len(dir2_only)) + " files only in " + dir2)

print(current_tag + "--> Found matching file names for " +
      str(len(matchingList)) + " files of " + str(len(dir1_viable_files)) +
      " in " + dir1 + " and " + str(len(dir1_viable_files)) + " in " + dir2 +
      " (only of viable files of course)")

## file comparison
current_tag = "[File comparison] "

dir1_cur_file_str = []
dir2_cur_file_str = []

count_equal = 0
count_not_equal = 0

print(current_tag + "Matching files that differ from each other:")

# choose list of matching file names depending on mode
if regExMode:
    matchingList = matching_names_suffixes
else:
    matchingList = matching_names
    
equalFiles = []
unequalFiles = []
for i in range(len(matchingList)):
    # different string builder for path depending on mode 
    if regExMode:
        with open("./" + dir1 + "/" + dir1+matchingList[i], "r") as f:  # add prefix to file name
            dir1_cur_file_str = f.read()
            dir1_cur_file_lines = dir1_cur_file_str.split("\n")

        with open("./" + dir2 + "/" + dir2+matchingList[i], "r") as f:
            dir2_cur_file_str = f.read()
            dir2_cur_file_lines = dir2_cur_file_str.split("\n")
    else:
        with open("./" + dir1 + "/" + matchingList[i], "r") as f:
            dir1_cur_file_str = f.read()
            dir1_cur_file_lines = dir1_cur_file_str.split("\n")

        with open("./" + dir2 + "/" + matchingList[i], "r") as f:
            dir2_cur_file_str = f.read()
            dir2_cur_file_lines = dir2_cur_file_str.split("\n")

    if dir1_cur_file_str == dir2_cur_file_str:
        count_equal += 1
        equalFiles.append(matchingList[i])
    elif len(dir1_cur_file_lines) == len(
            dir2_cur_file_lines):  # do more precise check for ignored lines if same line length
        counter_equal_lines = 0
        for j in range(len(dir1_cur_file_lines)):
            if dir1_cur_file_lines[j] != dir2_cur_file_lines[j]:
                bool_ignore_line = False
                for keyword in ignore_lines_containing:
                    if keyword in dir1_cur_file_lines[j] or keyword in dir2_cur_file_lines[j]:
                        bool_ignore_line = True
                        counter_equal_lines += 1
                if not bool_ignore_line:
                    print("    " + matchingList[i])
                    count_not_equal += 1
                    unequalFiles.append(matchingList[i])
                    break
            else:
                counter_equal_lines += 1
            if counter_equal_lines == len(dir1_cur_file_lines):
                count_equal += 1
                equalFiles.append(matchingList[i])
    else:
        print("    " + matchingList[i])
        count_not_equal += 1
        unequalFiles.append(matchingList[i])

print(current_tag + str(count_equal) + " files were equal.")
print(current_tag + str(count_not_equal) + " files were unequal.")

# create result file
with open("results_unequal_"+ dir1 + "_"+ dir2 + ".csv", "w") as f:
    f.write("Unequal files:\n") 
    for u in unequalFiles:
        f.write(u+'\n')
    pass
with open("results_equal_"+ dir1 + "_"+ dir2 + ".csv", "w") as f:
    f.write("Equal files:\n") 
    for e in equalFiles:
        f.write(e+'\n')
    pass
