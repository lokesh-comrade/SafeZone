@echo off
title SafeZone Server Launcher
color 0C
echo ===============================================================
echo     SafeZone: Autonomous Women Safety Monitoring System
echo ===============================================================
echo.
echo Starting SafeZone Local Server on port 8080...
echo.

cd /d "C:\Users\admin\.gemini\antigravity\scratch\safezone"
start "" http://localhost:8080/index.html
python server.py

pause
