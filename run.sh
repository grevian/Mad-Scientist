#!/bin/sh
set -x
java -cp 'src/:libs/slick.jar:libs/lwjgl-2.2.1/jar/*' -Djava.library.path="libs/lwjgl-2.2.1/native/linux/" SlickTest
