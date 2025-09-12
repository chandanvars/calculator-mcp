# Setup Guide for Calculator MCP Server

## Prerequisites Installation

### 1. Java Development Kit (JDK) 17+
**Current Status**: ❌ You have Java 11, but Java 17+ is required.

**Download and Install:**
- Download Oracle JDK 17+ from: https://www.oracle.com/java/technologies/javase-downloads.html
- Or download OpenJDK 17+ from: https://adoptium.net/

**Set JAVA_HOME:**
```powershell
# Windows PowerShell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Or set permanently in System Environment Variables
```

### 2. Apache Maven 3.6+
**Current Status**: ❌ Maven is not installed.

**Download and Install:**
1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract to a directory (e.g., `C:\apache-maven-3.9.0`)
3. Add to PATH:

```powershell
# Windows PowerShell
$env:MAVEN_HOME = "C:\apache-maven-3.9.0"
$env:PATH = "$env:MAVEN_HOME\bin;$env:PATH"

# Or set permanently in System Environment Variables
```

## Alternative: Manual Compilation (without Maven)

If you prefer not to install Maven, you can compile manually:

### 1. Create lib directory and download dependencies
```powershell
mkdir lib
# Download these JAR files to the lib directory:
# - jackson-core-2.15.2.jar
# - jackson-databind-2.15.2.jar
# - jackson-annotations-2.15.2.jar
# - slf4j-api-2.0.7.jar
# - logback-classic-1.4.8.jar
# - logback-core-1.4.8.jar
# - exp4j-0.4.8.jar
```

### 2. Compile Java sources
```powershell
# Create output directory
mkdir target\classes

# Compile with classpath
javac -cp "lib\*" -d target\classes -sourcepath src\main\java src\main\java\com\mcp\calculator\*.java src\main\java\com\mcp\calculator\**\*.java

# Copy resources
xcopy src\main\resources target\classes /E /H /Y
```

### 3. Create JAR file
```powershell
# Create manifest file
echo Main-Class: com.mcp.calculator.CalculatorMcpServer > manifest.txt

# Create JAR
jar cfm calculator-mcp-server.jar manifest.txt -C target\classes .

# Create fat JAR with dependencies
mkdir temp
cd temp
# Extract all dependency JARs
# Then create final JAR with all classes
```

## Quick Start with Maven (Recommended)

Once Maven is installed:

```powershell
# Navigate to project directory
cd "c:\Users\ChandanVarshney\VSCodeProjects\mcp-test"

# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package into JAR
mvn clean package

# Run the server
java -jar target\calculator-mcp-server-1.0.0.jar

# Or use the startup script
.\start-server.bat
```

## Verification

After installation, verify your setup:

```powershell
# Check Java version (should be 17+)
java -version

# Check Maven version
mvn -version

# Check if JAVA_HOME is set correctly
echo $env:JAVA_HOME
```

## IDE Setup

### Visual Studio Code
1. Install "Extension Pack for Java" extension
2. Install "Maven for Java" extension
3. The project should be automatically recognized as a Maven project

### IntelliJ IDEA
1. Open the project folder
2. IDEA will automatically detect it as a Maven project
3. Import Maven dependencies when prompted

## Troubleshooting

### Common Issues:

1. **"java: error: release version 17 not available"**
   - Solution: Install JDK 17+ and set JAVA_HOME correctly

2. **"mvn: command not found"**
   - Solution: Install Maven and add to PATH

3. **"Permission denied" on startup script**
   - Solution: Run `chmod +x start-server.sh` (Unix) or run PowerShell as Administrator (Windows)

4. **Port already in use**
   - Solution: The MCP server uses stdio, not network ports

## Next Steps

1. Install Java 17+ and Maven
2. Run `mvn clean package` to build the project
3. Test the server with the provided examples
4. Integrate with your MCP client application
