@echo off
REM Calculator MCP Server Startup Script for Windows

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    exit /b 1
)

REM Set up environment
set SCRIPT_DIR=%~dp0
set PROJECT_DIR=%SCRIPT_DIR%
set JAR_FILE=%PROJECT_DIR%target\calculator-mcp-server-1.0.0.jar

REM Create logs directory if it doesn't exist
if not exist "%PROJECT_DIR%logs" mkdir "%PROJECT_DIR%logs"

REM Check if JAR file exists
if not exist "%JAR_FILE%" (
    echo JAR file not found: %JAR_FILE%
    echo Please run 'mvn clean package' to build the project first
    exit /b 1
)

REM Set JVM options
set JVM_OPTS=-Xmx512m -Xms256m -Dfile.encoding=UTF-8

REM Add debug options if DEBUG environment variable is set
if "%DEBUG%"=="true" (
    set JVM_OPTS=%JVM_OPTS% -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
    echo Debug mode enabled. Connect debugger to port 5005
)

REM Start the MCP server
echo Starting Calculator MCP Server...
echo Working directory: %PROJECT_DIR%
echo JAR file: %JAR_FILE%
echo Logs directory: %PROJECT_DIR%logs

java %JVM_OPTS% -jar "%JAR_FILE%" %*
