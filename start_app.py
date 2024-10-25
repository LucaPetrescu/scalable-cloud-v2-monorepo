import os
import subprocess
from pathlib import Path

def start_mongodb(dbpath, port, logpath):
    mongod_path = Path("C:/Program Files/MongoDB/Server/7.0/bin/mongod.exe")
    if not mongod_path.exists():
        raise FileNotFoundError(f"MongoDB executable not found at {mongod_path}")
    
    os.makedirs(dbpath, exist_ok=True)
    os.makedirs(logpath.parent, exist_ok=True)

    command = [
        str(mongod_path),
        "--dbpath", str(dbpath),
        "--port", str(port),
        "--logpath", str(logpath),
        "--logappend"
    ]
    
    print(f"Starting MongoDB instance on port {port}...")
    subprocess.Popen(command)

def start_docker_compose(compose_file):
    print("Starting Docker containers using docker-compose")
    command = ["docker-compose", "-f", compose_file, "up", "-d", "--build"]
    subprocess.run(command, check=True)

def main():

    start_mongodb(Path("C:/data/db1"), 27017, Path("C:/data/log1/mongodb1.log"))
    start_mongodb(Path("C:/data/db2"), 27018, Path("C:/data/log2/mongodb2.log"))

    docker_compose_file = Path("docker-compose.yaml")
    start_docker_compose(docker_compose_file)

if __name__ == "__main__":
    main()