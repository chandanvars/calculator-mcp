package com.mcp.calculator.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * MCP Error object following JSON-RPC 2.0 specification
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpError {
    
    @JsonProperty("code")
    private int code;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
    private Object data;
    
    // Standard JSON-RPC error codes
    public static final int PARSE_ERROR = -32700;
    public static final int INVALID_REQUEST = -32600;
    public static final int METHOD_NOT_FOUND = -32601;
    public static final int INVALID_PARAMS = -32602;
    public static final int INTERNAL_ERROR = -32603;
    
    // MCP specific error codes
    public static final int TOOL_NOT_FOUND = -32000;
    public static final int TOOL_EXECUTION_ERROR = -32001;
    public static final int CALCULATION_ERROR = -32002;
    
    public McpError() {
    }
    
    public McpError(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public McpError(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
}
