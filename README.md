# Calculator MCP Server

A Model Context Protocol (MCP) server implementation in Java that provides calculator operations with stateful context management.

## Features

### Core Calculator Operations
- **Basic Arithmetic**: Addition, subtraction, multiplication, division, power, square root
- **Advanced Functions**: Logarithms (natural and base-10), trigonometric functions (sin, cos, tan)
- **Mathematical Utilities**: Factorial, degree/radian conversion
- **Expression Evaluation**: Complex mathematical expressions using exp4j library

### Stateful Context Management
- **Variables**: Store and use named variables in calculations (referenced as `$varname`)
- **Memory Stack**: Store, recall, and manipulate memory values (referenced as `$M`)
- **Operation History**: Track all operations with timestamps and results
- **Last Result**: Reference the last calculation result (referenced as `$_`)

### MCP Protocol Compliance
- Full JSON-RPC 2.0 implementation
- Standard MCP tool calling interface
- Error handling with appropriate error codes
- Proper request/response message formatting

## Project Structure

```
src/
├── main/
│   ├── java/com/mcp/calculator/
│   │   ├── CalculatorMcpServer.java          # Main MCP server implementation
│   │   ├── context/
│   │   │   └── CalculatorContext.java        # Stateful context management
│   │   ├── protocol/                         # MCP protocol message classes
│   │   │   ├── McpMessage.java
│   │   │   ├── McpRequest.java
│   │   │   ├── McpResponse.java
│   │   │   ├── McpError.java
│   │   │   ├── McpTool.java
│   │   │   └── ToolCallResult.java
│   │   └── service/
│   │       └── CalculatorService.java        # Calculator operations
│   └── resources/
│       └── logback.xml                       # Logging configuration
└── test/                                     # Test files (to be implemented)
```

## Available Tools

### 1. `calculate`
Evaluate mathematical expressions with variable and memory support.
- **Parameters**: `expression` (string) - Mathematical expression to evaluate
- **Example**: `"2 + 3 * $x + $M"` where `$x` is a variable and `$M` is memory

### 2. Variable Management
- `set_variable`: Set a variable value
- `get_variable`: Get a variable value
- `list_variables`: List all defined variables
- `clear_variable`: Clear a specific variable

### 3. Memory Management
- `memory_store`: Store a value in memory
- `memory_recall`: Recall the last stored memory value
- `memory_clear`: Clear all memory values

### 4. History and Context
- `get_history`: Get calculation history (with optional limit)
- `clear_history`: Clear calculation history
- `get_context`: Get calculator context summary
- `reset_context`: Reset entire calculator context

## Special References in Expressions

- `$variableName`: Reference a stored variable
- `$M`: Reference the last stored memory value
- `$_`: Reference the last calculation result

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build
```bash
mvn clean package
```

### Run
```bash
# Windows
start-server.bat

# Unix/Linux/macOS
./start-server.sh
```

### Debug Mode
Set the `DEBUG` environment variable to enable debug mode:
```bash
# Windows
set DEBUG=true
start-server.bat

# Unix/Linux/macOS
DEBUG=true ./start-server.sh
```

## Dependencies

- **Jackson**: JSON processing for MCP protocol messages
- **exp4j**: Mathematical expression evaluation
- **SLF4J + Logback**: Logging framework
- **JUnit 5**: Testing framework

## Configuration

### Logging
Logging is configured in `src/main/resources/logback.xml`:
- Console output goes to stderr (to avoid mixing with MCP protocol output)
- File logging to `logs/calculator-mcp-server.log` with rotation
- Debug level logging for calculator package

### JVM Options
Default JVM options in startup scripts:
- Initial heap: 256MB
- Maximum heap: 512MB
- UTF-8 encoding

## Error Handling

The server implements comprehensive error handling:
- **Parse errors**: Invalid JSON messages
- **Invalid requests**: Malformed MCP requests
- **Method not found**: Unknown MCP methods
- **Tool not found**: Unknown tool names
- **Tool execution errors**: Errors during tool execution
- **Calculation errors**: Mathematical errors (division by zero, etc.)

## Session Management

Each server instance maintains:
- Unique session ID
- Independent variable namespace
- Separate memory stack
- Complete operation history
- Persistent context until reset

## Example Usage

```json
// Initialize
{"jsonrpc": "2.0", "id": "1", "method": "initialize", "params": {}}

// Set a variable
{"jsonrpc": "2.0", "id": "2", "method": "tools/call", "params": {"name": "set_variable", "arguments": {"name": "x", "value": 10}}}

// Store value in memory
{"jsonrpc": "2.0", "id": "3", "method": "tools/call", "params": {"name": "memory_store", "arguments": {"value": 5}}}

// Calculate expression using variable and memory
{"jsonrpc": "2.0", "id": "4", "method": "tools/call", "params": {"name": "calculate", "arguments": {"expression": "2 * $x + $M"}}}

// Get calculation history
{"jsonrpc": "2.0", "id": "5", "method": "tools/call", "params": {"name": "get_history", "arguments": {"limit": 5}}}
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
