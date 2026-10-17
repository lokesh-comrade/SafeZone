@echo off
title PRJ_650 - SafeZone Local Server Launcher
color 0B
echo ==========================================================================
echo   PRJ_650: Real-Time Women Safety Monitoring & Response Platform
echo   Technologies: Web Technologies, Cybersecurity, Computer Vision, Big Data
echo   SDG Mapping:  SDG 5 (Gender Equality - Target 5.2)
echo ==========================================================================
echo.
echo Starting PRJ_650 Local Server on port 8080...
echo.

cd /d "%~dp0"
start "" http://localhost:8080/index.html
python server.py

pause
