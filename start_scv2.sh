echo "Script started..."

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$SCRIPT_DIR"

if [ ! -f "$SCRIPT_DIR/metricscollector/src/main/java/com/masterthesis/metricscollector/docker-compose.yml" ]; then
  echo "❌ Error: docker-compose.yml file not found in $SCRIPT_DIR"
  exit 1
fi

echo "Checking if Docker Deamon is running..."

if docker ps &>/dev/null; then
    echo "✅ Docker daemon is running"
else
    start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"

    echo "⏳ Waiting for Docker to start (60 seconds timeout)..."
    
    counter=0
    max_wait=60
    
    while ! docker ps &>/dev/null; do
        sleep 5
        counter=$((counter + 5))
        
        if [ $counter -ge $max_wait ]; then
            echo "❌ Timeout reached. Docker did not start within $max_wait seconds."
            exit 1
        fi
    done
    
    echo "✅ Docker daemon is running"
fi

echo "Starting system containers..."

docker-compose -f "$SCRIPT_DIR/metricscollector/src/main/java/com/masterthesis/metricscollector/docker-compose.yml" up --build -d > /dev/null 2>&1

echo "Waiting for containers to be up and running..."

REQUIRED_CONTAINERS=("metricscollector-kafka-1" "metricscollector-zookeeper-1" "prometheus")

RETRIES=30
SLEEP_INTERVAL=3

for ((i=1; i<=RETRIES; i++)); do
    echo "🔄 Checking running containers... Attempt $i/$RETRIES"
    all_up=true

    for container in "${REQUIRED_CONTAINERS[@]}"; do
        status=$(docker inspect -f '{{.State.Status}}' "$container" 2>/dev/null)

        if [[ "$status" != "running" ]]; then
            all_up=false
            echo "❗ $container is not yet running (status: $status)"
            echo "▶️ Attempting container starting"
            docker start "$container" 2>/dev/null

            sleep 2

            new_status=$(docker inspect -f '{{.State.Status}}' "$container" 2>/dev/null)
        fi
    done

    if $all_up; then
        echo "✅ All required containers are running."
        break
    else
        sleep $SLEEP_INTERVAL
    fi

    if [[ $i -eq $RETRIES ]]; then
        echo "❌ Timeout reached. Some containers are still not running."
        exit 1
    fi

    sleep $SLEEP_INTERVAL

done

docker ps

# Metrics Collector

cd "$SCRIPT_DIR/metricscollector"

echo "⚒️ Building Metrics Collector Project"

mvn clean package

mv target/metricscollector-0.0.1-SNAPSHOT.jar target/metricscollector.jar

cd "$SCRIPT_DIR/metricscollector/target"

echo "🍃 Running Metrics Collector Project"

nohup java -jar metricscollector.jar &

# Auth Service

cd "$SCRIPT_DIR/dummy-services/auth"

echo "🔄 Starting auth service..."

npm run start &

# Alerting System

cd "$SCRIPT_DIR/alertingsystem"

echo "⚒️ Building Alerting System Project"

mvn clean package

mv target/alertingsystem-0.0.1-SNAPSHOT.jar target/alertingsystem.jar

cd "$SCRIPT_DIR/alertingsystem/target"

echo "🍃 Running Alerting System Project"

nohup java -jar alertingsystem.jar &