@echo off
setlocal enabledelayedexpansion
set CP=D:\projects\Campus-Lost-Found-Portal\target\classes
for /r "C:\Users\LOQ\.m2\repository" %%f in (*.jar) do (
    set CP=!CP!;%%~ff
)
javac -cp "!CP!" -d D:\projects\Campus-Lost-Found-Portal\target\classes D:\projects\Campus-Lost-Found-Portal\src\main\java\com\campus\lostfound\config\SecurityConfig.java D:\projects\Campus-Lost-Found-Portal\src\main\java\com\campus\lostfound\config\PortalAuthenticationProvider.java