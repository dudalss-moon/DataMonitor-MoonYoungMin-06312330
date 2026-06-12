@echo off
chcp 65001 >nul
set JAVA_HOME=C:\Users\User\.jdks\temurin-17.0.19
set PATH=%JAVA_HOME%\bin;%PATH%
call gradlew.bat compileJava -q
%JAVA_HOME%\bin\java.exe -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp build\classes\java\main org.example.DataMonitorApp
