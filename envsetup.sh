# This script will setup the environment for GEM Development

GEMDIR=$(pwd)

echo "Adding Custom Environment Variables"
read -r -p "What is the path to your keystore file?"
echo 'export KEYSTORE="REPLY"' >> ~/.bashrc

echo "Updating PATH"
echo '' >> ~/.bashrc
echo 'if ! [[ :$PATH: == *:"$GEMDIR/scripts/linux":* ]] ; then' >> ~/.bashrc
echo 'export PATH="$GEMDIR/scripts/linux:$PATH"' >> ~/.bashrc
echo 'fi' >> ~/.bashrc

echo "Updating Environment"
source ~/.profile

echo Done
