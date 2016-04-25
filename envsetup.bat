@echo off
::This script will setup the environment for GEM Development
SET GEMDIR=%CD%

echo Adding GEM specific commands
echo @echo off>>scripts\windowscommands\gemapp.cmd
echo cd %GEMDIR%>>scripts\windowscommands\gemapp.cmd
set PATH=%PATH%;%GEMDIR%\scripts\windowscommands

echo Keystore Setup
echo Note: Passwords will be displayed (due to limitations in windows)
echo it is suggested to make sure nobody can see what is being entered here
set /p STOREFILE="Path of your keystore file: "
set /p STOREPASS="Keystore password: "
set /p KEYALIAS="Key alias: "
set /p KEYPASS="Key password: "

echo Creating Gradle files
del mobile\gradle.properties >nul
echo STOREFILE = %STOREFILE%>>mobile\gradle.properties
echo STOREPASS = %STOREPASS%>>mobile\gradle.properties
echo KEYALIAS = %KEYALIAS%>>mobile\gradle.properties
echo KEYPASS = %KEYPASS%>>mobile\gradle.properties

echo Time to set your PATH. This is an issue on Windows, because there is no safe command that can add something to PATH permanently without possibly damaging it.
echo You will have to add the path manually. All you have to do is select "Environment Variables" in the dialog that opens, find and select "Path", and hit "Edit". Then select "Edit text..."
echo Finally, go to the end of the file and paste. The script will automatically overwrite your clipboard, so backup whatever you have pasted currently.
echo Done. Close out of the opened windows.
echo.
echo Ready? Click any key to continue.
pause>nul
set CLIPTEXT=;%GEMDIR%\scripts\windowscommands;%GEMDIR%\scripts\windows
echo|set/p=%CLIPTEXT%|clip
control.exe sysdm.cpl,System,3

echo It is recommended that you now move to a different directory and run "gemapp" to make sure everything is installed correctly
echo Done