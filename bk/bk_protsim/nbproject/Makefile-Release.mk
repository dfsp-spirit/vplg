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
CND_CONF=Release
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/BK_Output.o \
	${OBJECTDIR}/BronKerbosch.o \
	${OBJECTDIR}/GMLptglProteinParser.o \
	${OBJECTDIR}/MultAlign.o \
	${OBJECTDIR}/Mult_Output.o \
	${OBJECTDIR}/PG_Output.o \
	${OBJECTDIR}/ProductGraph.o \
	${OBJECTDIR}/mainmult.o \
	${OBJECTDIR}/mainpair.o

# Test Directory
TESTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}/tests

# Test Files
TESTFILES= \
	${TESTDIR}/TestFiles/f1 \
	${TESTDIR}/TestFiles/f3 \
	${TESTDIR}/TestFiles/f2 \
	${TESTDIR}/TestFiles/f4

# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/bk_protsim

${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/bk_protsim: ${OBJECTFILES}
	${MKDIR} -p ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}
	${LINK.cc} -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/bk_protsim ${OBJECTFILES} ${LDLIBSOPTIONS}

${OBJECTDIR}/BK_Output.o: BK_Output.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/BK_Output.o BK_Output.cpp

${OBJECTDIR}/BronKerbosch.o: BronKerbosch.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/BronKerbosch.o BronKerbosch.cpp

${OBJECTDIR}/GMLptglProteinParser.o: GMLptglProteinParser.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/GMLptglProteinParser.o GMLptglProteinParser.cpp

${OBJECTDIR}/MultAlign.o: MultAlign.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/MultAlign.o MultAlign.cpp

${OBJECTDIR}/Mult_Output.o: Mult_Output.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/Mult_Output.o Mult_Output.cpp

${OBJECTDIR}/PG_Output.o: PG_Output.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/PG_Output.o PG_Output.cpp

${OBJECTDIR}/ProductGraph.o: ProductGraph.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/ProductGraph.o ProductGraph.cpp

${OBJECTDIR}/mainmult.o: mainmult.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/mainmult.o mainmult.cpp

${OBJECTDIR}/mainpair.o: mainpair.cpp 
	${MKDIR} -p ${OBJECTDIR}
	${RM} "$@.d"
	$(COMPILE.cc) -O2 -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/mainpair.o mainpair.cpp

# Subprojects
.build-subprojects:

# Build Test Targets
.build-tests-conf: .build-conf ${TESTFILES}
${TESTDIR}/TestFiles/f1: ${TESTDIR}/tests/Test_BK_Output.o ${TESTDIR}/tests/testrunner_BK_Output.o ${OBJECTFILES:%.o=%_nomain.o}
	${MKDIR} -p ${TESTDIR}/TestFiles
	${LINK.cc}   -o ${TESTDIR}/TestFiles/f1 $^ ${LDLIBSOPTIONS} `cppunit-config --libs`   

${TESTDIR}/TestFiles/f3: ${TESTDIR}/tests/Test_BronKerbosch.o ${TESTDIR}/tests/testrunner_BronKerbosch.o ${OBJECTFILES:%.o=%_nomain.o}
	${MKDIR} -p ${TESTDIR}/TestFiles
	${LINK.cc}   -o ${TESTDIR}/TestFiles/f3 $^ ${LDLIBSOPTIONS} `cppunit-config --libs`   

${TESTDIR}/TestFiles/f2: ${TESTDIR}/tests/Test_PG_Output.o ${TESTDIR}/tests/testrunner_PG_Output.o ${OBJECTFILES:%.o=%_nomain.o}
	${MKDIR} -p ${TESTDIR}/TestFiles
	${LINK.cc}   -o ${TESTDIR}/TestFiles/f2 $^ ${LDLIBSOPTIONS} `cppunit-config --libs`   

${TESTDIR}/TestFiles/f4: ${TESTDIR}/tests/Test_ProductGraph.o ${TESTDIR}/tests/testrunner_ProductGraph.o ${OBJECTFILES:%.o=%_nomain.o}
	${MKDIR} -p ${TESTDIR}/TestFiles
	${LINK.cc}   -o ${TESTDIR}/TestFiles/f4 $^ ${LDLIBSOPTIONS} `cppunit-config --libs`   


${TESTDIR}/tests/Test_BK_Output.o: tests/Test_BK_Output.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -O2 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/Test_BK_Output.o tests/Test_BK_Output.cpp


${TESTDIR}/tests/testrunner_BK_Output.o: tests/testrunner_BK_Output.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -O2 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/testrunner_BK_Output.o tests/testrunner_BK_Output.cpp


${TESTDIR}/tests/Test_BronKerbosch.o: tests/Test_BronKerbosch.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -O2 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/Test_BronKerbosch.o tests/Test_BronKerbosch.cpp


${TESTDIR}/tests/testrunner_BronKerbosch.o: tests/testrunner_BronKerbosch.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -O2 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/testrunner_BronKerbosch.o tests/testrunner_BronKerbosch.cpp


${TESTDIR}/tests/Test_PG_Output.o: tests/Test_PG_Output.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -O2 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/Test_PG_Output.o tests/Test_PG_Output.cpp


${TESTDIR}/tests/testrunner_PG_Output.o: tests/testrunner_PG_Output.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -O2 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/testrunner_PG_Output.o tests/testrunner_PG_Output.cpp


${TESTDIR}/tests/Test_ProductGraph.o: tests/Test_ProductGraph.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -O2 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/Test_ProductGraph.o tests/Test_ProductGraph.cpp


${TESTDIR}/tests/testrunner_ProductGraph.o: tests/testrunner_ProductGraph.cpp 
	${MKDIR} -p ${TESTDIR}/tests
	${RM} "$@.d"
	$(COMPILE.cc) -O2 `cppunit-config --cflags` -MMD -MP -MF "$@.d" -o ${TESTDIR}/tests/testrunner_ProductGraph.o tests/testrunner_ProductGraph.cpp


${OBJECTDIR}/BK_Output_nomain.o: ${OBJECTDIR}/BK_Output.o BK_Output.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/BK_Output.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -O2 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/BK_Output_nomain.o BK_Output.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/BK_Output.o ${OBJECTDIR}/BK_Output_nomain.o;\
	fi

${OBJECTDIR}/BronKerbosch_nomain.o: ${OBJECTDIR}/BronKerbosch.o BronKerbosch.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/BronKerbosch.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -O2 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/BronKerbosch_nomain.o BronKerbosch.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/BronKerbosch.o ${OBJECTDIR}/BronKerbosch_nomain.o;\
	fi

${OBJECTDIR}/GMLptglProteinParser_nomain.o: ${OBJECTDIR}/GMLptglProteinParser.o GMLptglProteinParser.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/GMLptglProteinParser.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -O2 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/GMLptglProteinParser_nomain.o GMLptglProteinParser.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/GMLptglProteinParser.o ${OBJECTDIR}/GMLptglProteinParser_nomain.o;\
	fi

${OBJECTDIR}/MultAlign_nomain.o: ${OBJECTDIR}/MultAlign.o MultAlign.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/MultAlign.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -O2 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/MultAlign_nomain.o MultAlign.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/MultAlign.o ${OBJECTDIR}/MultAlign_nomain.o;\
	fi

${OBJECTDIR}/Mult_Output_nomain.o: ${OBJECTDIR}/Mult_Output.o Mult_Output.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/Mult_Output.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -O2 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/Mult_Output_nomain.o Mult_Output.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/Mult_Output.o ${OBJECTDIR}/Mult_Output_nomain.o;\
	fi

${OBJECTDIR}/PG_Output_nomain.o: ${OBJECTDIR}/PG_Output.o PG_Output.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/PG_Output.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -O2 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/PG_Output_nomain.o PG_Output.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/PG_Output.o ${OBJECTDIR}/PG_Output_nomain.o;\
	fi

${OBJECTDIR}/ProductGraph_nomain.o: ${OBJECTDIR}/ProductGraph.o ProductGraph.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/ProductGraph.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -O2 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/ProductGraph_nomain.o ProductGraph.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/ProductGraph.o ${OBJECTDIR}/ProductGraph_nomain.o;\
	fi

${OBJECTDIR}/mainmult_nomain.o: ${OBJECTDIR}/mainmult.o mainmult.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/mainmult.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -O2 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/mainmult_nomain.o mainmult.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/mainmult.o ${OBJECTDIR}/mainmult_nomain.o;\
	fi

${OBJECTDIR}/mainpair_nomain.o: ${OBJECTDIR}/mainpair.o mainpair.cpp 
	${MKDIR} -p ${OBJECTDIR}
	@NMOUTPUT=`${NM} ${OBJECTDIR}/mainpair.o`; \
	if (echo "$$NMOUTPUT" | ${GREP} '|main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T main$$') || \
	   (echo "$$NMOUTPUT" | ${GREP} 'T _main$$'); \
	then  \
	    ${RM} "$@.d";\
	    $(COMPILE.cc) -O2 -Dmain=__nomain -MMD -MP -MF "$@.d" -o ${OBJECTDIR}/mainpair_nomain.o mainpair.cpp;\
	else  \
	    ${CP} ${OBJECTDIR}/mainpair.o ${OBJECTDIR}/mainpair_nomain.o;\
	fi

# Run Test Targets
.test-conf:
	@if [ "${TEST}" = "" ]; \
	then  \
	    ${TESTDIR}/TestFiles/f1 || true; \
	    ${TESTDIR}/TestFiles/f3 || true; \
	    ${TESTDIR}/TestFiles/f2 || true; \
	    ${TESTDIR}/TestFiles/f4 || true; \
	else  \
	    ./${TEST} || true; \
	fi

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/bk_protsim

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
