#! /bin/bash

## ----------------------
numcpus_=$(nproc)
quiet_=1
## ----------------------

echo "hello"

export qqq=""

if [ "$quiet_""x" == "1x" ]; then
	export qqq=" -qq "
fi


redirect_cmd() {
    if [ "$quiet_""x" == "1x" ]; then
        "$@" > /dev/null 2>&1
    else
        "$@"
    fi
}


echo "installing system packages ..."

redirect_cmd apt-get update $qqq

redirect_cmd apt-get install $qqq -y --force-yes --no-install-recommends lsb-release
system__=$(lsb_release -i|cut -d : -f2|sed -e "s#\s##g")
version__=$(lsb_release -r|cut -d : -f2|sed -e "s#\s##g")
echo "compiling on: $system__ $version__"

echo "installing more system packages ..."

pkgs="
    rsync
    clang
    cmake
    libconfig-dev
    libgtest-dev
    libopus-dev
    libsodium-dev
    libvpx-dev
    ninja-build
    pkg-config
"

for i in $pkgs ; do
    redirect_cmd apt-get install $qqq -y --force-yes --no-install-recommends $i
done

pkgs_z="
    binutils
    llvm-dev
    libavutil-dev
    libavcodec-dev
    libavformat-dev
    libavfilter-dev
    libx264-dev
"

for i in $pkgs_z ; do
    redirect_cmd apt-get install $qqq -y --force-yes --no-install-recommends $i
done


echo ""
echo ""
echo "--------------------------------"
echo "clang version:"
c++ --version
echo "--------------------------------"
echo ""
echo ""

echo "make a local copy ..."
redirect_cmd rsync -avz /src/circle_scripts /workspace/
redirect_cmd rsync -avz /src/jni-c-toxcore /workspace/

cd /workspace/
ls -al

# /src/circle_scripts/deps.sh

mkdir -p /artefacts/asan/
chmod a+rwx -R /workspace/
chmod a+rwx -R /artefacts/


