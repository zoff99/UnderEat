#! /bin/sh
url='https://github.com/zoff99/UnderEat/releases/download/nightly/android_screen01_29.png'

_HOME2_=$(dirname $0)
export _HOME2_
_HOME_=$(cd $_HOME2_;pwd)
export _HOME_

basedir="$_HOME_""/../"

cd "$basedir"
wget "$url" -O ./fastlane/metadata/android/en-US/images/phoneScreenshots/000.png
