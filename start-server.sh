#!/bin/bash

# Calculator MCP Server Startup Script

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    exit 1
fi

# Check Java version (requires Java 17+)
java_version=$(java -version 2>&1 | head -n 1 | awk -F '"' '{print $2}' | awk -F '.' '{print $1}')
if [ "$java_version" -lt 17 ]; then
    echo "Error: Java 17 or higher is required. Current version: $java_version"
    exit 1
fi

# Set up environment
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"
JAR_FILE="$PROJECT_DIR/target/calculator-mcp-server-1.0.0.jar"

# Create logs directory if it doesn't exist
mkdir -p "$PROJECT_DIR/logs"

# Check if JAR file exists
if [ ! -f "$JAR_FILE" ]; then
    echo "JAR file not found: $JAR_FILE"
    echo "Please run 'mvn clean package' to build the project first"
    exit 1
fi

# Set JVM options
JVM_OPTS="-Xmx512m -Xms256m -Dfile.encoding=UTF-8"

# Add debug options if DEBUG environment variable is set
if [ "$DEBUG" = "true" ]; then
    JVM_OPTS="$JVM_OPTS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
    echo "Debug mode enabled. Connect debugger to port 5005"
fi

# Start the MCP server
echo "Starting Calculator MCP Server..."
echo "Working directory: $PROJECT_DIR"
echo "JAR file: $JAR_FILE"
echo "Logs directory: $PROJECT_DIR/logs"

exec java $JVM_OPTS -jar "$JAR_FILE" "$@"
