@echo off

java -cp dist/Alcatraz_B6.jar at.technikum.bicss.sam.b6.alcatraz.server.ServerHost

if NOT %errorlevel% == 0
(
pause
)