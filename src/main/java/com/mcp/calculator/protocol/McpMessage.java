package com.mcp.calculator.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Base class for all MCP messages following the JSON-RPC 2.0 specification
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class McpMessage {
    
    @JsonProperty("jsonrpc")
    private final String jsonrpc = "2.0";
    
    @JsonProperty("id")
    private String id;
    
    public McpMessage() {
    }
    
    public McpMessage(String id) {
        this.id = id;
    }
    
    public String getJsonrpc() {
        return jsonrpc;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
}
