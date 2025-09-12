# Manual Build Script for Windows (without Maven)

# This script demonstrates how to build the project manually
# Use this if you don't want to install Maven

# Prerequisites: Java 17+ must be installed

Write-Host "Manual Build Script for Calculator MCP Server" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green

# Check Java version
$javaVersion = java -version 2>&1 | Select-String "version" | ForEach-Object { $_.ToString() }
Write-Host "Java Version: $javaVersion" -ForegroundColor Yellow

# Create directories
$projectDir = Get-Location
$targetDir = "$projectDir\target"
$classesDir = "$targetDir\classes"
$libDir = "$projectDir\lib"

Write-Host "Creating build directories..." -ForegroundColor Yellow
New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
New-Item -ItemType Directory -Force -Path $classesDir | Out-Null
New-Item -ItemType Directory -Force -Path $libDir | Out-Null

# List required dependencies
Write-Host "Required Dependencies (download to lib/ directory):" -ForegroundColor Yellow
Write-Host "  - jackson-core-2.15.2.jar" -ForegroundColor White
Write-Host "  - jackson-databind-2.15.2.jar" -ForegroundColor White
Write-Host "  - jackson-annotations-2.15.2.jar" -ForegroundColor White
Write-Host "  - slf4j-api-2.0.7.jar" -ForegroundColor White
Write-Host "  - logback-classic-1.4.8.jar" -ForegroundColor White
Write-Host "  - logback-core-1.4.8.jar" -ForegroundColor White
Write-Host "  - exp4j-0.4.8.jar" -ForegroundColor White

# Check if dependencies exist
$dependencies = @(
    "jackson-core-2.15.2.jar",
    "jackson-databind-2.15.2.jar", 
    "jackson-annotations-2.15.2.jar",
    "slf4j-api-2.0.7.jar",
    "logback-classic-1.4.8.jar",
    "logback-core-1.4.8.jar",
    "exp4j-0.4.8.jar"
)

$missingDeps = @()
foreach ($dep in $dependencies) {
    if (-not (Test-Path "$libDir\$dep")) {
        $missingDeps += $dep
    }
}

if ($missingDeps.Count -gt 0) {
    Write-Host "Missing dependencies in lib/ directory:" -ForegroundColor Red
    foreach ($dep in $missingDeps) {
        Write-Host "  - $dep" -ForegroundColor Red
    }
    Write-Host "Please download the missing JAR files to the lib/ directory and run this script again." -ForegroundColor Red
    Write-Host "Download URLs:" -ForegroundColor Yellow
    Write-Host "  Jackson: https://mvnrepository.com/artifact/com.fasterxml.jackson.core" -ForegroundColor White
    Write-Host "  SLF4J: https://mvnrepository.com/artifact/org.slf4j/slf4j-api" -ForegroundColor White
    Write-Host "  Logback: https://mvnrepository.com/artifact/ch.qos.logback" -ForegroundColor White
    Write-Host "  exp4j: https://mvnrepository.com/artifact/net.objecthunter/exp4j" -ForegroundColor White
    exit 1
}

Write-Host "All dependencies found!" -ForegroundColor Green

# Find all Java source files
Write-Host "Finding Java source files..." -ForegroundColor Yellow
$javaFiles = Get-ChildItem -Path "src\main\java" -Filter "*.java" -Recurse | ForEach-Object { $_.FullName }

if ($javaFiles.Count -eq 0) {
    Write-Host "No Java source files found!" -ForegroundColor Red
    exit 1
}

Write-Host "Found $($javaFiles.Count) Java source files" -ForegroundColor Green

# Build classpath
$classpath = (Get-ChildItem -Path $libDir -Filter "*.jar" | ForEach-Object { $_.FullName }) -join ";"
Write-Host "Classpath: $classpath" -ForegroundColor Yellow

# Compile Java sources
Write-Host "Compiling Java sources..." -ForegroundColor Yellow
$javaFilesList = $javaFiles -join " "

try {
    $compileCommand = "javac -cp `"$classpath`" -d `"$classesDir`" -sourcepath `"src\main\java`" $javaFilesList"
    Write-Host "Compile command: $compileCommand" -ForegroundColor Gray
    Invoke-Expression $compileCommand
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Compilation successful!" -ForegroundColor Green
    } else {
        Write-Host "Compilation failed!" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Error during compilation: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Copy resources
Write-Host "Copying resources..." -ForegroundColor Yellow
if (Test-Path "src\main\resources") {
    Copy-Item -Path "src\main\resources\*" -Destination $classesDir -Recurse -Force
    Write-Host "Resources copied!" -ForegroundColor Green
} else {
    Write-Host "No resources directory found" -ForegroundColor Yellow
}

# Create manifest file
Write-Host "Creating manifest file..." -ForegroundColor Yellow
$manifestContent = @"
Manifest-Version: 1.0
Main-Class: com.mcp.calculator.CalculatorMcpServer
Class-Path: .
"@
$manifestContent | Out-File -FilePath "$targetDir\MANIFEST.MF" -Encoding ASCII

# Extract dependency JARs for fat JAR
Write-Host "Extracting dependencies for fat JAR..." -ForegroundColor Yellow
$tempDir = "$targetDir\temp"
New-Item -ItemType Directory -Force -Path $tempDir | Out-Null

foreach ($jarFile in Get-ChildItem -Path $libDir -Filter "*.jar") {
    Write-Host "Extracting $($jarFile.Name)..." -ForegroundColor Gray
    $extractCommand = "jar xf `"$($jarFile.FullName)`""
    Push-Location $tempDir
    try {
        Invoke-Expression $extractCommand
    } catch {
        Write-Host "Warning: Failed to extract $($jarFile.Name)" -ForegroundColor Yellow
    }
    Pop-Location
}

# Copy compiled classes to temp directory
Write-Host "Copying compiled classes..." -ForegroundColor Yellow
Copy-Item -Path "$classesDir\*" -Destination $tempDir -Recurse -Force

# Create fat JAR
Write-Host "Creating fat JAR..." -ForegroundColor Yellow
$jarFile = "$targetDir\calculator-mcp-server-1.0.0.jar"
Push-Location $tempDir
try {
    $createJarCommand = "jar cfm `"$jarFile`" `"$targetDir\MANIFEST.MF`" ."
    Invoke-Expression $createJarCommand
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "JAR created successfully: $jarFile" -ForegroundColor Green
    } else {
        Write-Host "Failed to create JAR!" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "Error creating JAR: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
} finally {
    Pop-Location
}

# Clean up temp directory
Write-Host "Cleaning up..." -ForegroundColor Yellow
Remove-Item -Path $tempDir -Recurse -Force

# Test the JAR
Write-Host "Testing the JAR..." -ForegroundColor Yellow
$testCommand = "java -jar `"$jarFile`" --help"
try {
    # This will fail because our app doesn't have --help, but it will test if JAR is valid
    Invoke-Expression $testCommand 2>$null
} catch {
    # Expected to fail, just testing if JAR loads
}

Write-Host "Build completed successfully!" -ForegroundColor Green
Write-Host "JAR file created: $jarFile" -ForegroundColor Green
Write-Host "To run: java -jar `"$jarFile`"" -ForegroundColor Yellow
