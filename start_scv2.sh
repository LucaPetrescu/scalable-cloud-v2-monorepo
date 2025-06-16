echo "Script started..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$SCRIPT_DIR"

echo "Checking if Docker Daemon is running..."

if docker ps &>/dev/null; then
    echo "✅ Docker daemon is running"
else
    echo "ℹ️ Docker daemon is not running. Starting Docker Desktop..."
    open -a Docker
    
    # Wait for Docker daemon to be ready
    echo "Waiting for Docker daemon to start..."
    DOCKER_READY=false
    for i in {1..30}; do
        if docker ps &>/dev/null; then
            DOCKER_READY=true
            echo "✅ Docker daemon is ready"
            break
        fi
        echo "🔄 Waiting for Docker daemon... Attempt $i/30"
        sleep 2
    done
    
    if [ "$DOCKER_READY" = false ]; then
        echo "❌ Docker daemon failed to start properly"
        exit 1
    fi
    
    # Additional wait to ensure Docker is fully initialized
    echo "Waiting additional 5 seconds for Docker to fully initialize..."
    sleep 5
fi

# Function to clean up existing resources
cleanup_resources() {
    echo "🧹 Cleaning up existing resources..."
    
    # Stop and remove all running containers
    echo "Stopping and removing running containers..."
    docker ps -q | xargs -r docker stop
    docker ps -aq | xargs -r docker rm
    
    # Remove dangling images
    echo "Removing dangling images..."
    docker image prune -f
    
    # Kill any running Java processes for our applications
    echo "Stopping running Java applications..."
    pkill -f "metricscollector.jar" || true
    pkill -f "alertingsystem.jar" || true
    
    # Remove nohup.out files
    echo "Cleaning up log files..."
    rm -f "$SCRIPT_DIR/metricscollector/target/nohup.out"
    rm -f "$SCRIPT_DIR/alertingsystem/target/nohup.out"
    
    echo "✅ Cleanup completed"
}

# Call cleanup before starting services
cleanup_resources

# Function to check if containers are running
check_containers_running() {
    local containers=("$@")
    local all_up=true
    
    for container in "${containers[@]}"; do
        status=$(docker inspect -f '{{.State.Status}}' "$container" 2>/dev/null)
        if [[ "$status" != "running" ]]; then
            all_up=false
            echo "❗ $container is not running (status: $status)"
            return 1
        fi
    done
    
    if $all_up; then
        echo "✅ All containers are running"
        return 0
    fi
}

# Function to wait for containers
wait_for_containers() {
    local containers=("$@")
    local retries=30
    local sleep_interval=3
    
    for ((i=1; i<=retries; i++)); do
        echo "🔄 Checking running containers... Attempt $i/$retries"
        if check_containers_running "${containers[@]}"; then
            return 0
        fi
        sleep $sleep_interval
    done
    
    echo "❌ Timeout reached. Some containers are still not running."
    return 1
}

# Function to check if a port is listening
check_port_listening() {
    local port=$1
    local retries=30
    local sleep_interval=3
    
    for ((i=1; i<=retries; i++)); do
        echo "🔄 Checking if port $port is listening... Attempt $i/$retries"
        if lsof -i :$port -sTCP:LISTEN >/dev/null 2>&1; then
            echo "✅ Port $port is now listening"
            return 0
        fi
        sleep $sleep_interval
    done
    
    echo "❌ Timeout reached. Port $port is not listening."
    return 1
}

# Function to check if backend is ready
check_backend_ready() {
    local retries=30
    local sleep_interval=2
    
    for ((i=1; i<=retries; i++)); do
        echo "🔄 Checking if backend is ready... Attempt $i/$retries"
        if curl -s "http://localhost:8085/rules/getRulesForService?serviceName=auth-service" > /dev/null; then
            echo "✅ Backend is ready"
            return 0
        fi
        sleep $sleep_interval
    done
    
    echo "❌ Timeout reached. Backend is not ready."
    return 1
}

# Start Metrics Collector containers
echo "Starting Metrics Collector containers..."
cd "$SCRIPT_DIR/metricscollector/src/main/java/com/masterthesis/metricscollector"
docker-compose up --build -d

# Wait for Kafka to be ready
echo "Waiting for Kafka to be ready..."
KAFKA_READY=false
for i in {1..30}; do
    if docker exec metricscollector-kafka-1 kafka-topics.sh --bootstrap-server localhost:9092 --list > /dev/null 2>&1; then
        KAFKA_READY=true
        echo "✅ Kafka is ready"
        break
    fi
    echo "🔄 Waiting for Kafka to be ready... Attempt $i/30"
    sleep 2
done

if [ "$KAFKA_READY" = false ]; then
    echo "❌ Kafka failed to start properly"
    exit 1
fi

METRICS_COLLECTOR_CONTAINERS=("metricscollector-kafka-1" "metricscollector-zookeeper-1" "prometheus")
if ! wait_for_containers "${METRICS_COLLECTOR_CONTAINERS[@]}"; then
    echo "❌ Failed to start Metrics Collector containers"
    exit 1
fi

# Start Alerting System containers
echo "Starting Alerting System containers..."
cd "$SCRIPT_DIR/alertingsystem/src/main/java/com/masterthesis/alertingsystem"
docker-compose up --build -d

ALERTING_SYSTEM_CONTAINERS=("rabbitmq" "memcached")
if ! wait_for_containers "${ALERTING_SYSTEM_CONTAINERS[@]}"; then
    echo "❌ Failed to start Alerting System containers"
    exit 1
fi

# Start Metrics Collector application
cd "$SCRIPT_DIR/metricscollector"
echo "⚒️ Building Metrics Collector Project"
mvn clean package
mv target/metricscollector-0.0.1-SNAPSHOT.jar target/metricscollector.jar

cd "$SCRIPT_DIR/metricscollector/target"
echo "🍃 Running Metrics Collector Project"
nohup java -jar metricscollector.jar &

# Wait for metrics collector to be ready
if ! check_port_listening 8080; then
    echo "❌ Failed to start Metrics Collector application"
    exit 1
fi

# Start Alerting System application
cd "$SCRIPT_DIR/alertingsystem"
echo "⚒️ Building Alerting System Project"
mvn clean package
mv target/alertingsystem-0.0.1-SNAPSHOT.jar target/alertingsystem.jar

cd "$SCRIPT_DIR/alertingsystem/target"
echo "🍃 Running Alerting System Project"
nohup java -jar alertingsystem.jar &

# Start Auth Service container
cd "$SCRIPT_DIR/dummy-services/auth"
echo "🔄 Building and starting auth service container..."
docker build -t auth-service .
docker run -d --network host --name auth-service auth-service

# Start Inventory Service container
cd "$SCRIPT_DIR/dummy-services/inventory"
echo "🔄 Building and starting inventory service container..."
docker build -t inventory-service .
docker run -d --network host --name inventory-service inventory-service

# Start Metrics Dashboard
cd "$SCRIPT_DIR/metrics-dashboard"
echo "🔄 Waiting for backend services to be ready..."
if ! check_backend_ready; then
    echo "⚠️ Backend services not fully ready, but continuing with frontend startup..."
fi

echo "⏳ Waiting additional 5 seconds to ensure all services are stable..."
sleep 5

echo "🔄 Starting metrics dashboard on port 3002..."
PORT=3002 npm run start &

echo "✅ All services have been started"
echo "📊 Metrics Dashboard is available at http://localhost:3002"
echo "🔍 You can check container status with: docker ps"