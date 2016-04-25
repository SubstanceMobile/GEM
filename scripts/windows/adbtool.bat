@echo off
cls

:menu
cls
echo.
echo Welcome to ADB tool!
echo.
echo If you need help, type "commands" or "help"
goto menuitems

:menuitems
echo.
title ADB Tool
echo Enter a command
set /p choice=">"
if '%choice%'=='connect' GOTO connect
if '%choice%'=='adb' GOTO adb
if '%choice%'=='device' GOTO devicemenu
if '%choice%'=='list' GOTO list
if '%choice%'=='app' GOTO appmenu
if '%choice%'=='commands' goto commands
if '%choice%'=='help' goto commands
if '%choice%'=='log' goto logcat
if '%choice%'=='cls' goto menu
if '%choice%'=='exit' GOTO :EOF
echo "%choice%" is not valid, try again
goto menuitems

:commands
echo.
echo Here are the commands:
echo connect- connect to a device wirelessly
echo adb- allows you to input all adb commands
echo device- Allows you to enter commands directly into device
echo list- Lists current connected devices
echo app- App submenu
echo log- shows device's logcat
echo cls- clears screen and returns to menu
echo.
echo exit- exits the tool
goto menuitems

:appmenu
cls
title ADB Tool: Apps
echo.
echo Welcome to Apps!
echo.
echo If you need help, type "commands" or "help"
goto appcommands

:appcommands
echo.
echo Here are the commands for this section:
echo launch- launches app on device
echo kill- force stops apps
echo cache- clears app's cache
echo data- clears app's data
echo permissions- Permissions sub menu
echo cls- clears screen and returns to app menu
echo.
echo back- returns to menu
goto appmenuitems

:appmenuitems
echo.
echo Enter a command
set /p choice=">"
if '%choice%'=='launch' GOTO launch
if '%choice%'=='kill' GOTO comingsoon
if '%choice%'=='cache' GOTO comingsoon
if '%choice%'=='data' GOTO comingsoon
if '%choice%'=='permissions' GOTO comingsoon
if '%choice%'=='commands' goto appcommands
if '%choice%'=='help' goto appcommands
if '%choice%'=='back' GOTO menu
if '%choice%'=='cls' goto appmenu
echo "%choice%" is not valid, try again
goto appmenuitems

:comingsoon
echo Coming Soon
goto appmenuitems

:connect
title Connect to device
echo.
echo Welcome to connect! Type in device ip that you want to connect to.
set /p ip=Enter Device IP:
if '%ip%'=='' goto menuitems
echo.
echo Attemting to connect to %ip%:5555. Result:
cd C:\Users\Adrian\AppData\Local\Android\sdk\platform-tools
adb connect %ip%
goto menuitems

:adb
echo.
echo Type Exit to return to tool
echo Unlocking Terminal for ADB input
cmd /k "cd C:\Users\Adrian\AppData\Local\Android\sdk\platform-tools"
goto menuitems

:devicemenu
echo.
set /p root="Root Access? [Y/N]:"
if '%root%'=='y' GOTO deviceroot
if '%root%'=='n' GOTO device
if '%root%'=='Y' GOTO deviceroot
if '%root%'=='N' GOTO device
echo "%root%" is not valid, try again
goto devicemenu

:device
cls
title Device Shell
echo Type Exit to return to tool
cd C:\Users\Adrian\AppData\Local\Android\sdk\platform-tools
adb shell
goto menu

:deviceroot
cls
title Device Shell Root
echo Type Exit to return to tool
cd C:\Users\Adrian\AppData\Local\Android\sdk\platform-tools
adb root
adb shell
su
goto menu

:list
title List Devices
echo.
cd %userprofile%\AppData\Local\Android\sdk\platform-tools
adb devices
goto menuitems

:launch
title Launch Apps
echo.
echo Launch an app! First enter the package name and then the component name.
set /p id="Specify app package name [com.example.app]:"
set /p component="Specify component name:"
cd C:\Users\Adrian\AppData\Local\Android\sdk\platform-tools
adb shell am start -n %id%/%component%
goto appmenuitems

:logcat
title Logcat
echo.
echo You will need to relaunch the tool to exit this.
pause
cd C:\Users\Adrian\AppData\Local\Android\sdk\platform-tools
adb logcat
goto menuitems