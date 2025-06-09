#!/bin/bash

echo "🛑 Stopping all services and cleaning up containers..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Function to kill process by port
kill_process_on_port() {
    local port=$1
    local pid=$(lsof -ti:$port)
    if [ ! -z "$pid" ]; then
        echo "Stopping process on port $port (PID: $pid)"
        kill -9 $pid 2>/dev/null
    fi
}

# Stop Metrics Dashboard (port 3002)
echo "Stopping Metrics Dashboard..."
kill_process_on_port 3002

# Stop Metrics Collector (port 8080)
echo "Stopping Metrics Collector..."
kill_process_on_port 8080

# Stop Alerting System (port 8085)
echo "Stopping Alerting System..."
kill_process_on_port 8085

# Stop Auth Service (port 3000)
echo "Stopping Auth Service..."
kill_process_on_port 3000

# Stop Inventory Service (port 3001)
echo "Stopping Inventory Service..."
kill_process_on_port 3001

# Stop and remove all containers
echo "Stopping and removing all containers..."

# Stop and remove auth and inventory service containers
docker stop auth-service inventory-service 2>/dev/null
docker rm auth-service inventory-service 2>/dev/null

# Stop and remove metrics collector containers
cd "$SCRIPT_DIR/metricscollector/src/main/java/com/masterthesis/metricscollector"
docker-compose down --remove-orphans

# Stop and remove alerting system containers
cd "$SCRIPT_DIR/alertingsystem/src/main/java/com/masterthesis/alertingsystem"
docker-compose down --remove-orphans

# Remove any dangling containers
echo "Cleaning up any remaining containers..."
docker ps -aq | xargs -r docker rm -f

# Remove any dangling images
echo "Cleaning up unused images..."
docker image prune -f

echo "✅ All services stopped and containers cleaned up"
echo "📋 Remaining containers:"
docker ps -a 