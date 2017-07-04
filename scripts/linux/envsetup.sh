#!/bin/bash
# This script will setup the environment for GEM Development
GEMDIR=$(pwd)

echo "Modding ~/.bashrc"
echo '' >> ~/.bashrc
echo '# GEM Player' >> ~/.bashrc 
echo "gemapp() { cd" $GEMDIR";}" >> ~/.bashrc

echo "Keystore setup"
read -e -p "Path of your keystore file: " STOREFILE
read -e -s -p "Keystore password: " STOREPASS
echo
read -e -p "Key alias: " KEYALIAS
read -e -s -p "Key password: " KEYPASS
echo

echo "Creating Gradle files"
rm mobile/gradle.properties
touch mobile/gradle.properties
echo "STOREFILE = "$STOREFILE >> mobile/gradle.properties
echo "" >> mobile/gradle.properties
echo "STOREPASS = "$STOREPASS >> mobile/gradle.properties
echo "" >> mobile/gradle.properties
echo "KEYALIAS = "$KEYALIAS >> mobile/gradle.properties
echo "" >> mobile/gradle.properties
echo "KEYPASS = "$KEYPASS >> mobile/gradle.properties

read -p "Modify PATH? (y/n): " -n 1 GO
echo
if [ "$GO" = "y" ]; then
  echo "Updating PATH"
  echo '' >> ~/.bashrc
  echo 'if ! [[ :$PATH: == *:"$GEMDIR/scripts/linux":* ]] ; then' >> ~/.bashrc
  echo 'export PATH="$GEMDIR/scripts/linux:$PATH"' >> ~/.bashrc
  echo 'fi' >> ~/.bashrc

  echo "Updating file permissions"
  chmod +x scripts/linux/adbtool
else
  echo "Skipping PATH modifications"
fi

echo "Updating Environment"
source ~/.profile

echo Done
