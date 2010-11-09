ECLIPSE_URL=http://galileo.intalio.com/downloads/eclipse-SDK-3.5-macosx-carbon.tar.gz

echo "Downloading Eclipse 3.5"
lwp-download $ECLIPSE_URL eclipse-3.5.tar.gz

echo "Unzipping file"
gunzip eclipse-3.5.tar.gz

echo "Untarring  file"
tar -xf eclipse-3.5.tar

echo "Renaming eclipse-3.5 directory"
mv eclipse eclipse-3.5

echo "Moving Eclipse 3.5"
mkdir ../clean-installs
mv eclipse-3.5 ../clean-installs/.

echo "Removing tar file"
rm eclipse-3.5.tar
