JOPT_SIMPLE_DIR=../clean-workspaces/jopt-simple
mkdir $JOPT_SIMPLE_DIR
cd $JOPT_SIMPLE_DIR
cvs -z3  -d :pserver:anonymous@jopt-simple.cvs.sourceforge.net:/cvsroot/jopt-simple co -r jopt-simple-3_1 .
cd ../../scripts