@echo off
start javaw -cp libs/lwjgl-2.2.1/jar/ibxm.jar;libs/lwjgl-2.2.1/jar/lwjgl.jar;libs/slick.jar;bin/; -Djava.library.path="libs/lwjgl-2.2.1/native/windows/" SlickTest > runlog.txt