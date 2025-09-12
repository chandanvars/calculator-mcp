package com.mcp.calculator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.calculator.context.CalculatorContext;
import com.mcp.calculator.protocol.*;
import com.mcp.calculator.service.CalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Main MCP server implementation for calculator operations
 */
public class CalculatorMcpServer {
    
    private static final Logger logger = LoggerFactory.getLogger(CalculatorMcpServer.class);
    private final ObjectMapper objectMapper;
    private final CalculatorContext context;
    private final CalculatorService calculatorService;
    private final Map<String, McpTool> availableTools;
    
    public CalculatorMcpServer() {
        this.objectMapper = new ObjectMapper();
        this.context = new CalculatorContext();
        this.calculatorService = new CalculatorService(context);
        this.availableTools = initializeTools();
    }
    
    /**
     * Start the MCP server and handle incoming requests
     */
    public void start() {
        logger.info("Starting Calculator MCP Server...");
        logger.info("Session ID: {}", context.getSessionId());
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processMessage(line);
            }
        } catch (IOException e) {
            logger.error("Error reading input: {}", e.getMessage());
        }
    }
    
    /**
     * Process incoming MCP message
     */
    private void processMessage(String messageJson) {
        try {
            JsonNode jsonNode = objectMapper.readTree(messageJson);
            
            if (jsonNode.has("method")) {
                // This is a request
                McpRequest request = objectMapper.treeToValue(jsonNode, McpRequest.class);
                handleRequest(request);
            } else {
                logger.warn("Received non-request message: {}", messageJson);
            }
        } catch (JsonProcessingException e) {
            logger.error("Error parsing message: {}", e.getMessage());
            sendErrorResponse(null, McpError.PARSE_ERROR, "Invalid JSON");
        } catch (Exception e) {
            logger.error("Unexpected error processing message: {}", e.getMessage());
            sendErrorResponse(null, McpError.INTERNAL_ERROR, "Internal server error");
        }
    }
    
    /**
     * Handle MCP request
     */
    private void handleRequest(McpRequest request) {
        String method = request.getMethod();
        String requestId = request.getId();
        
        logger.debug("Handling request - Method: {}, ID: {}", method, requestId);
        
        try {
            switch (method) {
                case "initialize":
                    handleInitialize(requestId, request.getParams());
                    break;
                case "tools/list":
                    handleToolsList(requestId);
                    break;
                case "tools/call":
                    handleToolCall(requestId, request.getParams());
                    break;
                default:
                    sendErrorResponse(requestId, McpError.METHOD_NOT_FOUND, 
                        "Method not found: " + method);
            }
        } catch (Exception e) {
            logger.error("Error handling request {}: {}", method, e.getMessage());
            sendErrorResponse(requestId, McpError.INTERNAL_ERROR, 
                "Error processing request: " + e.getMessage());
        }
    }
    
    /**
     * Handle initialize request
     */
    private void handleInitialize(String requestId, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("protocolVersion", "2024-11-05");
        result.put("serverInfo", Map.of(
            "name", "Calculator MCP Server",
            "version", "1.0.0"
        ));
        result.put("capabilities", Map.of(
            "tools", Map.of()
        ));
        
        sendResponse(requestId, result);
        logger.info("Server initialized");
    }
    
    /**
     * Handle tools list request
     */
    private void handleToolsList(String requestId) {
        Map<String, Object> result = new HashMap<>();
        result.put("tools", new ArrayList<>(availableTools.values()));
        sendResponse(requestId, result);
    }
    
    /**
     * Handle tool call request
     */
    private void handleToolCall(String requestId, Map<String, Object> params) {
        if (params == null || !params.containsKey("name")) {
            sendErrorResponse(requestId, McpError.INVALID_PARAMS, 
                "Tool name is required");
            return;
        }
        
        String toolName = (String) params.get("name");
        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) params.get("arguments");
        
        if (!availableTools.containsKey(toolName)) {
            sendErrorResponse(requestId, McpError.TOOL_NOT_FOUND, 
                "Tool not found: " + toolName);
            return;
        }
        
        try {
            ToolCallResult result = executeToolCall(toolName, arguments);
            sendResponse(requestId, result);
        } catch (Exception e) {
            logger.error("Error executing tool {}: {}", toolName, e.getMessage());
            sendErrorResponse(requestId, McpError.TOOL_EXECUTION_ERROR, 
                "Tool execution failed: " + e.getMessage());
        }
    }
    
    /**
     * Execute tool call
     */
    private ToolCallResult executeToolCall(String toolName, Map<String, Object> arguments) {
        switch (toolName) {
            case "calculate":
                return handleCalculate(arguments);
            case "set_variable":
                return handleSetVariable(arguments);
            case "get_variable":
                return handleGetVariable(arguments);
            case "list_variables":
                return handleListVariables(arguments);
            case "clear_variable":
                return handleClearVariable(arguments);
            case "memory_store":
                return handleMemoryStore(arguments);
            case "memory_recall":
                return handleMemoryRecall(arguments);
            case "memory_clear":
                return handleMemoryClear(arguments);
            case "get_history":
                return handleGetHistory(arguments);
            case "clear_history":
                return handleClearHistory(arguments);
            case "get_context":
                return handleGetContext(arguments);
            case "reset_context":
                return handleResetContext(arguments);
            default:
                throw new IllegalArgumentException("Unknown tool: " + toolName);
        }
    }
    
    /**
     * Tool implementations
     */
    private ToolCallResult handleCalculate(Map<String, Object> arguments) {
        String expression = (String) arguments.get("expression");
        if (expression == null || expression.trim().isEmpty()) {
            return createErrorResult("Expression is required");
        }
        
        try {
            double result = calculatorService.evaluate(expression);
            return createSuccessResult(String.format("Result: %s = %s", expression, result));
        } catch (CalculatorService.CalculationException e) {
            return createErrorResult("Calculation error: " + e.getMessage());
        }
    }
    
    private ToolCallResult handleSetVariable(Map<String, Object> arguments) {
        String name = (String) arguments.get("name");
        Object valueObj = arguments.get("value");
        
        if (name == null || valueObj == null) {
            return createErrorResult("Variable name and value are required");
        }
        
        try {
            double value = ((Number) valueObj).doubleValue();
            context.setVariable(name, value);
            return createSuccessResult(String.format("Variable %s set to %s", name, value));
        } catch (ClassCastException e) {
            return createErrorResult("Invalid value type, number expected");
        }
    }
    
    private ToolCallResult handleGetVariable(Map<String, Object> arguments) {
        String name = (String) arguments.get("name");
        if (name == null) {
            return createErrorResult("Variable name is required");
        }
        
        Double value = context.getVariable(name);
        if (value == null) {
            return createErrorResult("Variable not found: " + name);
        }
        
        return createSuccessResult(String.format("Variable %s = %s", name, value));
    }
    
    private ToolCallResult handleListVariables(Map<String, Object> arguments) {
        Map<String, Double> variables = context.getAllVariables();
        if (variables.isEmpty()) {
            return createSuccessResult("No variables defined");
        }
        
        StringBuilder sb = new StringBuilder("Variables:\n");
        variables.forEach((name, value) -> 
            sb.append(String.format("  %s = %s\n", name, value)));
        
        return createSuccessResult(sb.toString());
    }
    
    private ToolCallResult handleClearVariable(Map<String, Object> arguments) {
        String name = (String) arguments.get("name");
        if (name == null) {
            return createErrorResult("Variable name is required");
        }
        
        boolean existed = context.clearVariable(name);
        if (existed) {
            return createSuccessResult("Variable " + name + " cleared");
        } else {
            return createErrorResult("Variable not found: " + name);
        }
    }
    
    private ToolCallResult handleMemoryStore(Map<String, Object> arguments) {
        Object valueObj = arguments.get("value");
        if (valueObj == null) {
            return createErrorResult("Value is required");
        }
        
        try {
            double value = ((Number) valueObj).doubleValue();
            context.memoryStore(value);
            return createSuccessResult("Value " + value + " stored in memory");
        } catch (ClassCastException e) {
            return createErrorResult("Invalid value type, number expected");
        }
    }
    
    private ToolCallResult handleMemoryRecall(Map<String, Object> arguments) {
        Double value = context.memoryRecall();
        if (value == null) {
            return createErrorResult("Memory is empty");
        }
        
        return createSuccessResult("Memory value: " + value);
    }
    
    private ToolCallResult handleMemoryClear(Map<String, Object> arguments) {
        context.memoryClear();
        return createSuccessResult("Memory cleared");
    }
    
    private ToolCallResult handleGetHistory(Map<String, Object> arguments) {
        Object limitObj = arguments.get("limit");
        int limit = limitObj != null ? ((Number) limitObj).intValue() : 10;
        
        List<CalculatorContext.OperationHistory> history = context.getRecentHistory(limit);
        if (history.isEmpty()) {
            return createSuccessResult("No history available");
        }
        
        StringBuilder sb = new StringBuilder("Recent operations:\n");
        history.forEach(op -> sb.append("  ").append(op.toString()).append("\n"));
        
        return createSuccessResult(sb.toString());
    }
    
    private ToolCallResult handleClearHistory(Map<String, Object> arguments) {
        context.clearHistory();
        return createSuccessResult("History cleared");
    }
    
    private ToolCallResult handleGetContext(Map<String, Object> arguments) {
        CalculatorContext.ContextSummary summary = context.getSummary();
        
        String contextInfo = String.format(
            "Context Summary:\n" +
            "  Session ID: %s\n" +
            "  Variables: %d\n" +
            "  Memory size: %d\n" +
            "  History entries: %d\n" +
            "  Last result: %s\n" +
            "  Operations count: %d",
            summary.getSessionId(),
            summary.getVariableCount(),
            summary.getMemorySize(),
            summary.getHistorySize(),
            summary.getLastResult(),
            summary.getOperationCount()
        );
        
        return createSuccessResult(contextInfo);
    }
    
    private ToolCallResult handleResetContext(Map<String, Object> arguments) {
        context.reset();
        return createSuccessResult("Calculator context has been reset");
    }
    
    /**
     * Helper methods
     */
    private ToolCallResult createSuccessResult(String message) {
        return new ToolCallResult(
            List.of(ToolCallResult.Content.text(message)),
            false
        );
    }
    
    private ToolCallResult createErrorResult(String message) {
        return new ToolCallResult(
            List.of(ToolCallResult.Content.text("Error: " + message)),
            true
        );
    }
    
    private void sendResponse(String requestId, Object result) {
        McpResponse response = new McpResponse(requestId, result);
        sendMessage(response);
    }
    
    private void sendErrorResponse(String requestId, int errorCode, String errorMessage) {
        McpError error = new McpError(errorCode, errorMessage);
        McpResponse response = new McpResponse(requestId, error);
        sendMessage(response);
    }
    
    private void sendMessage(Object message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            System.out.println(json);
            System.out.flush();
        } catch (JsonProcessingException e) {
            logger.error("Error serializing message: {}", e.getMessage());
        }
    }
    
    /**
     * Initialize available tools
     */
    private Map<String, McpTool> initializeTools() {
        Map<String, McpTool> tools = new HashMap<>();
        
        // Calculate tool
        tools.put("calculate", new McpTool(
            "calculate",
            "Evaluate mathematical expressions with support for variables ($varname), memory ($M), and last result ($_)",
            new McpTool.ToolInputSchema(
                Map.of("expression", new McpTool.ToolProperty("string", "Mathematical expression to evaluate")),
                List.of("expression")
            )
        ));
        
        // Variable management tools
        tools.put("set_variable", new McpTool(
            "set_variable",
            "Set a variable value for use in calculations",
            new McpTool.ToolInputSchema(
                Map.of(
                    "name", new McpTool.ToolProperty("string", "Variable name"),
                    "value", new McpTool.ToolProperty("number", "Variable value")
                ),
                List.of("name", "value")
            )
        ));
        
        tools.put("get_variable", new McpTool(
            "get_variable",
            "Get a variable value",
            new McpTool.ToolInputSchema(
                Map.of("name", new McpTool.ToolProperty("string", "Variable name")),
                List.of("name")
            )
        ));
        
        tools.put("list_variables", new McpTool(
            "list_variables",
            "List all defined variables",
            new McpTool.ToolInputSchema(Map.of(), List.of())
        ));
        
        tools.put("clear_variable", new McpTool(
            "clear_variable",
            "Clear a specific variable",
            new McpTool.ToolInputSchema(
                Map.of("name", new McpTool.ToolProperty("string", "Variable name")),
                List.of("name")
            )
        ));
        
        // Memory management tools
        tools.put("memory_store", new McpTool(
            "memory_store",
            "Store a value in memory",
            new McpTool.ToolInputSchema(
                Map.of("value", new McpTool.ToolProperty("number", "Value to store")),
                List.of("value")
            )
        ));
        
        tools.put("memory_recall", new McpTool(
            "memory_recall",
            "Recall the last stored memory value",
            new McpTool.ToolInputSchema(Map.of(), List.of())
        ));
        
        tools.put("memory_clear", new McpTool(
            "memory_clear",
            "Clear all memory values",
            new McpTool.ToolInputSchema(Map.of(), List.of())
        ));
        
        // History and context tools
        tools.put("get_history", new McpTool(
            "get_history",
            "Get calculation history",
            new McpTool.ToolInputSchema(
                Map.of("limit", new McpTool.ToolProperty("number", "Maximum number of entries to return (default: 10)")),
                List.of()
            )
        ));
        
        tools.put("clear_history", new McpTool(
            "clear_history",
            "Clear calculation history",
            new McpTool.ToolInputSchema(Map.of(), List.of())
        ));
        
        tools.put("get_context", new McpTool(
            "get_context",
            "Get calculator context summary",
            new McpTool.ToolInputSchema(Map.of(), List.of())
        ));
        
        tools.put("reset_context", new McpTool(
            "reset_context",
            "Reset entire calculator context (variables, memory, history)",
            new McpTool.ToolInputSchema(Map.of(), List.of())
        ));
        
        return tools;
    }
    
    /**
     * Main entry point
     */
    public static void main(String[] args) {
        new CalculatorMcpServer().start();
    }
}
