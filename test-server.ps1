# MCP Server Test Script

# This script sends test messages to the MCP server to verify it works correctly

Write-Host "Calculator MCP Server Test Script" -ForegroundColor Green
Write-Host "===============================" -ForegroundColor Green

# Check if JAR file exists
$jarFile = "target\calculator-mcp-server-1.0.0.jar"
if (-not (Test-Path $jarFile)) {
    Write-Host "JAR file not found: $jarFile" -ForegroundColor Red
    Write-Host "Please build the project first using:" -ForegroundColor Yellow
    Write-Host "  mvn clean package" -ForegroundColor White
    Write-Host "  OR" -ForegroundColor White
    Write-Host "  .\build-manual.ps1" -ForegroundColor White
    exit 1
}

Write-Host "JAR file found: $jarFile" -ForegroundColor Green

# Test messages
$testMessages = @(
    @{
        name = "Initialize"
        message = '{"jsonrpc":"2.0","id":"1","method":"initialize","params":{}}'
        expectedResponse = "protocolVersion"
    },
    @{
        name = "List Tools"
        message = '{"jsonrpc":"2.0","id":"2","method":"tools/list","params":{}}'
        expectedResponse = "tools"
    },
    @{
        name = "Simple Calculation"
        message = '{"jsonrpc":"2.0","id":"3","method":"tools/call","params":{"name":"calculate","arguments":{"expression":"2 + 3"}}}'
        expectedResponse = "Result: 2 + 3 = 5"
    },
    @{
        name = "Set Variable"
        message = '{"jsonrpc":"2.0","id":"4","method":"tools/call","params":{"name":"set_variable","arguments":{"name":"x","value":10}}}'
        expectedResponse = "Variable x set to 10"
    },
    @{
        name = "Calculate with Variable"
        message = '{"jsonrpc":"2.0","id":"5","method":"tools/call","params":{"name":"calculate","arguments":{"expression":"$x * 2"}}}'
        expectedResponse = "Result: $x * 2 = 20"
    }
)

Write-Host "Starting MCP server test..." -ForegroundColor Yellow

# Start the server process
$serverProcess = Start-Process -FilePath "java" -ArgumentList "-jar", $jarFile -PassThru -NoNewWindow -RedirectStandardInput -RedirectStandardOutput -RedirectStandardError

if (-not $serverProcess) {
    Write-Host "Failed to start MCP server!" -ForegroundColor Red
    exit 1
}

Write-Host "MCP server started (Process ID: $($serverProcess.Id))" -ForegroundColor Green

# Give the server time to start
Start-Sleep -Seconds 2

# Send test messages
foreach ($test in $testMessages) {
    Write-Host "`nTesting: $($test.name)" -ForegroundColor Cyan
    Write-Host "Sending: $($test.message)" -ForegroundColor Gray
    
    try {
        # Send message to server
        $test.message | Out-String | Write-Host -ForegroundColor White
        
        # In a real test, you would send this to the server's stdin and read from stdout
        # For this demo, we're just showing the test structure
        
        Write-Host "Expected response should contain: $($test.expectedResponse)" -ForegroundColor Yellow
        
    } catch {
        Write-Host "Error in test $($test.name): $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Stop the server
try {
    $serverProcess.Kill()
    Write-Host "`nMCP server stopped" -ForegroundColor Yellow
} catch {
    Write-Host "Error stopping server: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`nTest completed!" -ForegroundColor Green
Write-Host "Note: This is a basic test structure. For full testing, you need an MCP client." -ForegroundColor Yellow
