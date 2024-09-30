@echo off
REM ===========================================
REM Script to create directories and start multiple MongoDB instances
REM ===========================================

REM Set MongoDB executable path
set MONGOD_PATH="C:\Program Files\MongoDB\Server\7.0\bin\mongod.exe"

REM Create directories for the first MongoDB instance
if not exist "C:\data\db1" (
    mkdir C:\data\db1
    echo Created directory C:\data\db1
)
if not exist "C:\data\log1" (
    mkdir C:\data\log1
    echo Created directory C:\data\log1
)

REM Create directories for the second MongoDB instance
if not exist "C:\data\db2" (
    mkdir C:\data\db2
    echo Created directory C:\data\db2
)
if not exist "C:\data\log2" (
    mkdir C:\data\log2
    echo Created directory C:\data\log2
)

REM ===========================================
REM Start the first MongoDB instance on port 27017
REM ===========================================
echo Starting the first MongoDB instance on port 27017...
start "" %MONGOD_PATH% --dbpath "C:\data\db1" --port 27017 --logpath "C:\data\log1\mongodb1.log" --logappend

REM ===========================================
REM Start the second MongoDB instance on port 27018
REM ===========================================
echo Starting the second MongoDB instance on port 27018...
start "" %MONGOD_PATH% --dbpath "C:\data\db2" --port 27018 --logpath "C:\data\log2\mongodb2.log" --logappend

REM ===========================================
REM Confirmation message
REM ===========================================
echo Two MongoDB instances have been started:
echo - Instance 1: Port 27017, Data Path C:\data\db1, Log Path C:\data\log1
echo - Instance 2: Port 27018, Data Path C:\data\db2, Log Path C:\data\log2
pause
