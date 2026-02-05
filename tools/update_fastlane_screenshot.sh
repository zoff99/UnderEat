#! /bin/sh
url_001='https://github.com/zoff99/UnderEat/releases/download/nightly/android_screen01_29.png'
url_002='https://github.com/zoff99/UnderEat/releases/download/nightly/info_screen01_29.png'

_HOME2_=$(dirname $0)
export _HOME2_
_HOME_=$(cd $_HOME2_;pwd)
export _HOME_

basedir="$_HOME_""/../"

cd "$basedir"
wget "$url_001" -O ./fastlane/metadata/android/en-US/images/phoneScreenshots/000.png
wget "$url_002" -O ./fastlane/metadata/android/en-US/images/phoneScreenshots/010.png
