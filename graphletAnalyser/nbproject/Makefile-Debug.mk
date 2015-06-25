#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran
AS=as

# Macros
CND_PLATFORM=GNU-Linux-x86
CND_DLIB_EXT=so
CND_CONF=Debug
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/Database.o \
	${OBJECTDIR}/GMLptglProteinParser.o \
	${OBJECTDIR}/GraphPTGLPrinter.o \
	${OBJECTDIR}/GraphPrinter.o \
	${OBJECTDIR}/GraphService.o \
	${OBJECTDIR}/GraphletCounts.o \
	${OBJECTDIR}/JSON_printer.o \
	${OBJECTDIR}/ProteinGraphService.o \
	${OBJECTDIR}/main.o

# Test Directory
TESTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}/tests

# Test Files
TESTFILES= \
	${TESTDIR}/TestFiles/f3 \
	${TESTDIR}/TestFiles/f2 \
	${TESTDIR}/TestFiles/f1 \
	${TESTDIR}/TestFiles/f4

# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=-std=gnu++11
CXXFLAGS=-std=gnu++11

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=-L/usr/lib64 -lboost_regex -lboost_graph -lpqxx -lpq

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/graphletanalyser

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/graphletanalyser: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/graphletanalyser ${OBJECTFILES} ${LDLIBSOPTIONS}

${OBJECTDIR}/Database.o: Database.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/Database.o Database.cpp

${OBJECTDIR}/GMLptglProteinParser.o: GMLptglProteinParser.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/GMLptglProteinParser.o GMLptglProteinParser.cpp

${OBJECTDIR}/GraphPTGLPrinter.o: GraphPTGLPrinter.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/GraphPTGLPrinter.o GraphPTGLPrinter.cpp

${OBJECTDIR}/GraphPrinter.o: GraphPrinter.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/GraphPrinter.o GraphPrinter.cpp

${OBJECTDIR}/GraphService.o: GraphService.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/GraphService.o GraphService.cpp

${OBJECTDIR}/GraphletCounts.o: GraphletCounts.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/GraphletCounts.o GraphletCounts.cpp

${OBJECTDIR}/JSON_printer.o: JSON_printer.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/JSON_printer.o JSON_printer.cpp

${OBJECTDIR}/ProteinGraphService.o: ProteinGraphService.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/ProteinGraphService.o ProteinGraphService.cpp

${OBJECTDIR}/main.o: main.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/main.o main.cpp

# Subprojects
.build-subprojects:

# Build Test Targets
.build-tests-conf: .build-conf ${TESTFILES}
${TESTDIR}/TestFiles/f3: ${TESTDIR}/tests/newtestclass2.o ${TESTDIR}/tests/newtestrunner2.o ${OBJECTFILES:%.o=%_nomain.o}
	${MKDIR} -p ${TESTDIR}/TestFiles
	${LINK.cc}   -o ${TESTDIR}/TestFiles/f3 $^ ${LDLIBSOPTIONS} -L/usr/lib64 -lboost_graph -lboost_regex -lpq -lpqxx `cppunit-config --libs`   

${TESTDIR}/TestFiles/f2: ${TESTDIR}/tests/newtestclass1.o ${TESTDIR}/tests/newtestrunner1.o ${OBJECTFILES:%.o=%_nomain.o}
	${MKDIR} -p ${TESTDIR}/TestFiles
	${LINK.cc}   -o ${TESTDIR}/TestFiles/f2 $^ ${LDLIBSOPTIONS} -L/usr/lib64 -lboost_graph -lboost_regex -lpq -lpqxx `cppunit-config --libs`   

${TESTDIR}/TestFiles/f1: ${TESTDIR}/tests/newtestclass.o ${TESTDIR}/tests/newtestrunner.o ${OBJECTFILES:%.o=%_nomain.o}
	${MKDIR} -p ${TESTDIR}/TestFiles
	${LINK.cc}   -o ${TESTDIR}/TestFiles/f1 $^ ${LDLIBSOPTIONS} -L/usr/lib64 -lboost_graph -lboost_regex -lpq -lpqxx `cppunit-config --libs`   

${TESTDIR}/TestFiles/f4: ${TESTDIR}/tests/newtestclass3.o ${TESTDIR}/tests/newtestrunner3.o ${OBJECTFILES:%.o=%_nomain.o}
	${MKDIR} -p ${TESTDIR}/TestFiles
	${LINK.cc}   -o ${TESTDIR}/TestFiles/f4 $^ ${LDLIBSOPTIONS} -L/usr/lib64 -lboost_graph -lboost_regex -lpq -lpqxx `cppunit-config --libs`   


${TESTDIR}/tests/newtestclass2.o: tests/newtestclass2.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -std=gnu++11 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/newtestclass2.o tests/newtestclass2.cpp


${TESTDIR}/tests/newtestrunner2.o: tests/newtestrunner2.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -std=gnu++11 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/newtestrunner2.o tests/newtestrunner2.cpp


${TESTDIR}/tests/newtestclass1.o: tests/newtestclass1.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -std=gnu++11 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/newtestclass1.o tests/newtestclass1.cpp


${TESTDIR}/tests/newtestrunner1.o: tests/newtestrunner1.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -std=gnu++11 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/newtestrunner1.o tests/newtestrunner1.cpp


${TESTDIR}/tests/newtestclass.o: tests/newtestclass.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -std=gnu++11 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/newtestclass.o tests/newtestclass.cpp


${TESTDIR}/tests/newtestrunner.o: tests/newtestrunner.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -std=gnu++11 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/newtestrunner.o tests/newtestrunner.cpp


${TESTDIR}/tests/newtestclass3.o: tests/newtestclass3.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -std=gnu++11 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/newtestclass3.o tests/newtestclass3.cpp


${TESTDIR}/tests/newtestrunner3.o: tests/newtestrunner3.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -std=gnu++11 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/newtestrunner3.o tests/newtestrunner3.cpp


${OBJECTDIR}/Database_nomain.o: ${OBJECTDIR}/Database.o Database.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/Database.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/Database_nomain.o Database.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/Database.o ${OBJECTDIR}/Database_nomain.o;\
	fi

${OBJECTDIR}/GMLptglProteinParser_nomain.o: ${OBJECTDIR}/GMLptglProteinParser.o GMLptglProteinParser.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/GMLptglProteinParser.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/GMLptglProteinParser_nomain.o GMLptglProteinParser.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/GMLptglProteinParser.o ${OBJECTDIR}/GMLptglProteinParser_nomain.o;\
	fi

${OBJECTDIR}/GraphPTGLPrinter_nomain.o: ${OBJECTDIR}/GraphPTGLPrinter.o GraphPTGLPrinter.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/GraphPTGLPrinter.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/GraphPTGLPrinter_nomain.o GraphPTGLPrinter.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/GraphPTGLPrinter.o ${OBJECTDIR}/GraphPTGLPrinter_nomain.o;\
	fi

${OBJECTDIR}/GraphPrinter_nomain.o: ${OBJECTDIR}/GraphPrinter.o GraphPrinter.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/GraphPrinter.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/GraphPrinter_nomain.o GraphPrinter.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/GraphPrinter.o ${OBJECTDIR}/GraphPrinter_nomain.o;\
	fi

${OBJECTDIR}/GraphService_nomain.o: ${OBJECTDIR}/GraphService.o GraphService.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/GraphService.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/GraphService_nomain.o GraphService.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/GraphService.o ${OBJECTDIR}/GraphService_nomain.o;\
	fi

${OBJECTDIR}/GraphletCounts_nomain.o: ${OBJECTDIR}/GraphletCounts.o GraphletCounts.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/GraphletCounts.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/GraphletCounts_nomain.o GraphletCounts.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/GraphletCounts.o ${OBJECTDIR}/GraphletCounts_nomain.o;\
	fi

${OBJECTDIR}/JSON_printer_nomain.o: ${OBJECTDIR}/JSON_printer.o JSON_printer.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/JSON_printer.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/JSON_printer_nomain.o JSON_printer.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/JSON_printer.o ${OBJECTDIR}/JSON_printer_nomain.o;\
	fi

${OBJECTDIR}/ProteinGraphService_nomain.o: ${OBJECTDIR}/ProteinGraphService.o ProteinGraphService.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/ProteinGraphService.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/ProteinGraphService_nomain.o ProteinGraphService.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/ProteinGraphService.o ${OBJECTDIR}/ProteinGraphService_nomain.o;\
	fi

${OBJECTDIR}/main_nomain.o: ${OBJECTDIR}/main.o main.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/main.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -g -Inbproject -I/usr/include -include Graph.h -include GraphService.h -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/main_nomain.o main.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/main.o ${OBJECTDIR}/main_nomain.o;\
	fi

# Run Test Targets
.test-conf:
	@if [ "${TEST}" = "" ]; \
	then  \
	    ${TESTDIR}/TestFiles/f3 || true; \
	    ${TESTDIR}/TestFiles/f2 || true; \
	    ${TESTDIR}/TestFiles/f1 || true; \
	    ${TESTDIR}/TestFiles/f4 || true; \
	else  \
	    ./${TEST} || true; \
	fi

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/graphletanalyser

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
