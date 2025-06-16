#!/bin/bash

set -e  # Exit on error

echo "🛑 Stopping all services and cleaning up containers..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Function to kill process by port
kill_process_on_port() {
    local port=$1
    local pid=$(lsof -ti:$port)
    if [ ! -z "$pid" ]; then
        echo "Stopping process on port $port (PID: $pid)"
        kill -9 $pid 2>/dev/null || echo "No process found on port $port"
    fi
}

# Function to clean up Docker resources
cleanup_docker() {
    echo "🧹 Cleaning up Docker resources..."
    
    # Stop all running containers
    echo "Stopping all running containers..."
    docker ps -q | xargs -r docker stop || true
    
    # Remove all containers (including stopped ones)
    echo "Removing all containers..."
    docker ps -aq | xargs -r docker rm -f || true
    
    # Remove all networks except default ones
    echo "Removing unused networks..."
    docker network prune -f || true
    
    # Remove all volumes
    echo "Removing unused volumes..."
    docker volume prune -f || true
}

# Stop all services
echo "🛑 Stopping all services..."

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
echo "Stopping auth and inventory services..."
docker stop auth-service inventory-service 2>/dev/null || true
docker rm auth-service inventory-service 2>/dev/null || true

# Stop and remove metrics collector containers
cd "$SCRIPT_DIR/metricscollector/src/main/java/com/masterthesis/metricscollector"
echo "Stopping Kafka and Zookeeper containers..."
docker-compose down --remove-orphans --volumes || true

# Wait for Kafka to fully stop
echo "Waiting for Kafka to fully stop..."
sleep 5

# Stop and remove alerting system containers
cd "$SCRIPT_DIR/alertingsystem/src/main/java/com/masterthesis/alertingsystem"
echo "Stopping Alerting System containers..."
docker-compose down --remove-orphans --volumes || true

# Perform thorough cleanup
cleanup_docker

echo "✅ All services stopped and containers cleaned up"

# Show final state
echo "📋 Final container state:"
docker ps -a

echo "📋 Final network state:"
docker network ls

echo "📋 Final volume state:"
docker volume ls

echo "Closing Docker Desktop..."
pkill -f Docker || true
echo "✅ Docker Desktop closed"

echo "✨ Cleanup complete! All services and Docker resources have been stopped and cleaned up."