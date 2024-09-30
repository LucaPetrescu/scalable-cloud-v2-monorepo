@echo off
REM Change these paths to your actual Zookeeper and Kafka installation paths

REM Path to Zookeeper installation (point to zookeeper's config)
set ZOOKEEPER_HOME=C:\kafka\bin\windows
set ZOOKEEPER_CONFIG_FILE=C:\kafka\config\zookeeper.properties

REM Path to Kafka installation (point to kafka's config)
set KAFKA_HOME=C:\kafka\bin\windows
set KAFKA_CONFIG_FILE=C:\kafka\config\server.properties

REM Start Zookeeper
echo Starting Zookeeper...
start cmd /k "cd /d %ZOOKEEPER_HOME% && zookeeper-server-start.bat %ZOOKEEPER_CONFIG_FILE%"

REM Wait for a few seconds to ensure Zookeeper is started
timeout /t 10 /nobreak >nul

REM Start Kafka
echo Starting Kafka...
start cmd /k "cd /d %KAFKA_HOME% && kafka-server-start.bat %KAFKA_CONFIG_FILE%"

echo Kafka and Zookeeper started successfully.
pause
