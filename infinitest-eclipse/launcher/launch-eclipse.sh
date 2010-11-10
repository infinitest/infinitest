CURRENT_DIR=`pwd`
VERSION=$1
WORKSPACE=$2

echo "Launching Eclipse version $VERSION with workspace $WORKSPACE."

echo "Cleaning out working install directory."
rm -Rf working-install/*

echo "Cleaning out working workspace directory."
rm -Rf working-workspace/*
rm -Rf working-workspace/.metadata

echo 'Copying clean Eclipse install to working directory.'
cp -r clean-installs/eclipse-$VERSION working-install

echo 'Copying clean workspace into working directory.'
cp -r clean-workspaces/$WORKSPACE/* working-workspace/.
cp -r clean-workspaces/$WORKSPACE/.metadata working-workspace/.

echo "Copying Infinitest plugin."
cp ../eclipse-plugin/target/*.jar working-install/eclipse-$VERSION/plugins/.

echo "Launching Eclipse $VERSION."
./working-install/eclipse-$VERSION/Eclipse.app/Contents/MacOS/eclipse -data $CURRENT_DIR/working-workspace -consolelog

